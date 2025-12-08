package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.GarageRequestsDTO;
import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.mapper.GarageMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Location;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.GarageService;
import com.eclectics.Garage.service.GoogleMapsService;
import com.eclectics.Garage.service.OSSService;
import com.eclectics.Garage.specificationExecutor.GarageSpecificationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@CacheConfig(cacheNames = {"garages"})
public class GarageServiceImpl implements GarageService {

    private static final Logger logger = LoggerFactory.getLogger(GarageServiceImpl.class);

    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final GarageMapper mapper;
    private final OSSService ossService;
    private final GoogleMapsService googleMapsService;

    private final Map<Long, Garage> garageCacheById = new ConcurrentHashMap<>();
    private final Map<String, Garage> garageCacheByName = new ConcurrentHashMap<>();
    private final Map<Long, Garage> garageCacheByUser = new ConcurrentHashMap<>();
    private final List<Garage> allGaragesCache = Collections.synchronizedList(new ArrayList<>());

    public GarageServiceImpl(GarageRepository garageRepository, AuthenticationService authenticationService,
                             GarageMapper mapper, OSSService ossService, GoogleMapsService googleMapsService) {
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
        this.googleMapsService = googleMapsService;
        loadCaches();
    }

    private synchronized void loadCaches() {
        logger.info("Loading garages into in-memory cache...");
        List<Garage> garages = garageRepository.findAll();
        garageCacheById.clear();
        garageCacheByName.clear();
        garageCacheByUser.clear();
        allGaragesCache.clear();

        for (Garage g : garages) {
            garageCacheById.put(g.getGarageId(), g);
            garageCacheByName.put(g.getBusinessName(), g);
            garageCacheByUser.put(g.getUser().getId(), g);
            allGaragesCache.add(g);
        }
        logger.info("✅ Loaded {} garages into memory.", garages.size());
    }

    @Override
    public ProfileCompleteDTO checkProfileCompletion(Garage garage) {
        List<String> missingFields = garage.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    @Override
    @CacheEvict(value = {"allGarages"}, allEntries = true)
    public synchronized Garage createGarage(GarageRequestsDTO garageRequestsDTO, MultipartFile businessLicense,
                                            MultipartFile professionalCertificate, MultipartFile facilityPhotos) { // Removed 'throws IOException' from signature

        Garage garage = mapper.toEntity(garageRequestsDTO);
        User user = authenticationService.getCurrentUser();
        garage.setUser(user);

        if (garageCacheByName.containsKey(garage.getBusinessName())) {
            throw new ResourceNotFoundException("Garage with this name already exists");
        }

        try {
            if (businessLicense != null && !businessLicense.isEmpty()) {
                String ext = getFileExtension(businessLicense.getOriginalFilename());
                String path = "Garages/licences/" + user.getId() + "-" + UUID.randomUUID() + ext;
                garage.setBusinessLicense(ossService.uploadFile(path, businessLicense.getInputStream()));
            }
            if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
                String ext = getFileExtension(professionalCertificate.getOriginalFilename());
                String path = "Garages/profCerts/" + user.getId() + "-" + UUID.randomUUID() + ext;
                garage.setProfessionalCertificate(ossService.uploadFile(path, professionalCertificate.getInputStream()));
            }
            if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
                String ext = getFileExtension(facilityPhotos.getOriginalFilename());
                String path = "Garages/photos/" + user.getId() + "-" + UUID.randomUUID() + ext;
                garage.setFacilityPhotos(ossService.uploadFile(path, facilityPhotos.getInputStream()));
            }

        } catch (IOException e) {
            throw new BadRequestException("Failed to upload garage files");
        }

        // --- 2. Geocoding Logic (New) ---
        String address = garage.getPhysicalBusinessAddress();
        if (address != null && !address.isBlank()) {
            try {
                // Block the reactive call to ensure coordinates are set before JPA save
                googleMapsService.geocode(address)
                        .map(coords -> {
                            Location location = new Location(coords.get("lat"), coords.get("lng"));
                            garage.setBusinessLocation(location);
                            return garage;
                        })
                        .block(); // WARNING: This blocks the thread. Acceptable if app is not fully reactive.
            } catch (Exception e) {
                logger.error("Geocoding failed for garage address: {}", address, e);
                // Decide if you want to throw an exception or continue without location
                // For now, we'll log and continue without location set.
            }
        }

        long uniqueId;
        Random random = new Random();
        do {
            uniqueId = random.nextInt(90000) + 10000;
        } while (garageCacheById.containsKey(uniqueId));

        garage.setGarageId(uniqueId);
        Garage saved = garageRepository.save(garage);

        garageCacheById.put(saved.getGarageId(), saved);
        garageCacheByName.put(saved.getBusinessName(), saved);
        garageCacheByUser.put(saved.getUser().getId(), saved);
        allGaragesCache.add(saved);

        return saved;
    }


    @Override
    public boolean isDetailsCompleted(Long userId) {
        Garage cached = garageCacheByUser.get(userId);
        if (cached != null) return cached.isComplete();

        return garageRepository.findByUserId(userId)
                .map(Garage::isComplete)
                .orElse(false);
    }

    @Cacheable(value = "garageFilter")
    @Override
    public List<GarageResponseDTO> filterGarages(
            String businessName,
            String physicalBusinessAddress
    ) {

        Specification<Garage> spec = Specification.allOf(
                GarageSpecificationExecutor.businessNameContains(businessName),
                GarageSpecificationExecutor.physicalAddressContains(physicalBusinessAddress));

        List<Garage> garages = garageRepository.findAll(spec);

        if (garages.isEmpty()) {
            throw new ResourceNotFoundException("No garages match the given criteria.");
        }

        return garages.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }



    @Override
    public long countAllGarages() {
        return allGaragesCache.isEmpty() ? garageRepository.count() : allGaragesCache.size();
    }

    private String getObjectNameFromUrl(String fileUrl, String bucketName, String endpoint) {
        String baseUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        return null;
    }

    @Override
    @CachePut(value = "garageByUser", key = "#result.user.id")
    @CacheEvict(value = {"allGarages", "garageById", "garageByName"}, allEntries = true)
    public synchronized GarageResponseDTO updateOwnGarage(GarageRequestsDTO dto,
                                                          MultipartFile businessLicense,
                                                          MultipartFile professionalCertificate,
                                                          MultipartFile facilityPhotos) {

        logger.info("[UPDATE] Updating Garage profile for currently authenticated user");

        // 1️⃣ Get the authenticated user
        User user = authenticationService.getCurrentUser();

        // 2️⃣ Get the linked Garage entity
        Garage garage = garageRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Garage profile not found for user: " + user.getEmail()));

        // 3️⃣ Update basic garage fields
        if (dto.getBusinessName() != null) garage.setBusinessName(dto.getBusinessName());
        if (dto.getOperatingHours() != null) garage.setOperatingHours(dto.getOperatingHours());
        if (dto.getBusinessEmailAddress() != null) garage.setBusinessEmailAddress(dto.getBusinessEmailAddress());
        if (dto.getBusinessRegNumber() != null) garage.setBusinessRegNumber(dto.getBusinessRegNumber());
        if (dto.getTwentyFourHours() != null) garage.setTwentyFourHours(dto.getTwentyFourHours());
        if (dto.getServiceCategories() != null) garage.setServiceCategories(dto.getServiceCategories());
        if (dto.getSpecialisedServices() != null) garage.setSpecialisedServices(dto.getSpecialisedServices());
        if (dto.getPhysicalBusinessAddress() != null) garage.setPhysicalBusinessAddress(dto.getPhysicalBusinessAddress());
        if (dto.getBusinessPhoneNumber() != null) garage.setBusinessPhoneNumber(dto.getBusinessPhoneNumber());
        if (dto.getYearsInOperation() != null) garage.setYearsInOperation(dto.getYearsInOperation());
        if (dto.getMpesaPayBill() != null) garage.setMpesaPayBill(dto.getMpesaPayBill());
        if (dto.getMpesaTill() != null) garage.setMpesaTill(dto.getMpesaTill());

        // 4️⃣ Handle file uploads
        try {
            if (businessLicense != null && !businessLicense.isEmpty()) {
                String ext = getFileExtension(businessLicense.getOriginalFilename());
                String uniqueFileName = "Garages/licences/" + garage.getGarageId() + "-" + UUID.randomUUID() + ext;
                String fileUrl = ossService.uploadFile(uniqueFileName, businessLicense.getInputStream());
                garage.setBusinessLicense(fileUrl);
                logger.debug("[UPDATE] Business license updated: {}", fileUrl);
            }

            if (professionalCertificate != null && !professionalCertificate.isEmpty()) {
                String ext = getFileExtension(professionalCertificate.getOriginalFilename());
                String uniqueFileName = "Garages/profCerts/" + garage.getGarageId() + "-" + UUID.randomUUID() + ext;
                String fileUrl = ossService.uploadFile(uniqueFileName, professionalCertificate.getInputStream());
                garage.setProfessionalCertificate(fileUrl);
                logger.debug("[UPDATE] Professional certificate updated: {}", fileUrl);
            }

            if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
                String ext = getFileExtension(facilityPhotos.getOriginalFilename());
                String uniqueFileName = "Garages/photos/" + garage.getGarageId() + "-" + UUID.randomUUID() + ext;
                String fileUrl = ossService.uploadFile(uniqueFileName, facilityPhotos.getInputStream());
                garage.setFacilityPhotos(fileUrl);
                logger.debug("[UPDATE] Facility photos updated: {}", fileUrl);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to update garage files: " + e.getMessage());
        }

        if (dto.getPhysicalBusinessAddress() != null) {
            String newAddress = dto.getPhysicalBusinessAddress();
            try {
                // Block the reactive call to ensure coordinates are set before JPA save
                googleMapsService.geocode(newAddress)
                        .map(coords -> {
                            Location location = new Location(coords.get("lat"), coords.get("lng"));
                            garage.setBusinessLocation(location);
                            return garage;
                        })
                        .block(); // WARNING: This blocks the thread.
            } catch (Exception e) {
                logger.error("Geocoding failed during update for garage address: {}", newAddress, e);
                // Log and continue without updating location, or throw an exception.
            }
        }

        // 5️⃣ Save and cache the updated garage
        Garage updated = garageRepository.save(garage);

        garageCacheById.put(updated.getGarageId(), updated);
        garageCacheByName.put(updated.getBusinessName(), updated);
        garageCacheByUser.put(updated.getUser().getId(), updated);

        allGaragesCache.removeIf(g -> Objects.equals(g.getGarageId(), garage.getGarageId()));
        allGaragesCache.add(updated);

        logger.info("[UPDATE SUCCESS] Garage profile updated for user={}", user.getEmail());
        return mapper.toResponseDTO(updated);
    }


    @Override
    @CacheEvict(value = {"allGarages", "garageById", "garageByName", "garageByUser"}, allEntries = true)
    public synchronized void deleteGarage(Long id) {
        if (!garageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Garage with id " + id + " does not exist");
        }
        garageRepository.deleteById(id);

        garageCacheById.remove(id);
        garageCacheByName.entrySet().removeIf(e -> e.getValue().getId().equals(id));
        garageCacheByUser.entrySet().removeIf(e -> e.getValue().getId().equals(id));
        allGaragesCache.removeIf(g -> g.getId().equals(id));
    }

    @Override
    public Optional<String> getGarageUrlByUniqueId(Long uniqueId, int expiryMinutes) {
        Garage cached = garageCacheById.get(uniqueId);
        if (cached != null && cached.getBusinessLicense() != null) {
            return Optional.of(ossService.generatePresignedUrl(cached.getBusinessLicense(), expiryMinutes));
        }

        return garageRepository.findByGarageId(uniqueId)
                .map(g -> ossService.generatePresignedUrl(g.getBusinessLicense(), expiryMinutes));
    }
}
