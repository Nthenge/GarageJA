package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.MechanicRequestDTO;
import com.eclectics.Garage.dto.MechanicResponseDTO;
import com.eclectics.Garage.dto.ProfileCompleteDTO;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.mapper.MechanicMapper;
import com.eclectics.Garage.model.Garage;
import com.eclectics.Garage.model.Mechanic;
import com.eclectics.Garage.model.User;
import com.eclectics.Garage.repository.GarageRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.MechanicService;
import com.eclectics.Garage.service.OSSService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@CacheConfig(cacheNames = {"mechanics"})
public class MechanicServiceImpl implements MechanicService {

    private static final Logger logger = LoggerFactory.getLogger(MechanicServiceImpl.class);

    private final MechanicRepository mechanicRepository;
    private final GarageRepository garageRepository;
    private final AuthenticationService authenticationService;
    private final MechanicMapper mapper;
    private final OSSService ossService;

    private final Map<Long, MechanicResponseDTO> mechanicCacheByUserId = new ConcurrentHashMap<>();
    private final Map<Long, MechanicResponseDTO> mechanicCacheById = new ConcurrentHashMap<>();
    private final Map<Long, List<MechanicResponseDTO>> mechanicCacheByGarageId = new ConcurrentHashMap<>();
    private final Map<Integer, MechanicResponseDTO> mechanicCacheByNationalId = new ConcurrentHashMap<>();
    private final Set<Integer> nationalIdSet = Collections.synchronizedSet(new HashSet<>());

    private List<MechanicResponseDTO> cachedAllMechanics = Collections.synchronizedList(new ArrayList<>());

    public MechanicServiceImpl(MechanicRepository mechanicRepository, GarageRepository garageRepository,
                               AuthenticationService authenticationService, MechanicMapper mapper, OSSService ossService) {
        this.mechanicRepository = mechanicRepository;
        this.garageRepository = garageRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    private String getObjectNameFromUrl(String fileUrl, String bucketName, String endpoint) {
        String baseUrl = "https://" + bucketName + "." + endpoint.replace("https://", "") + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        return null;
    }

    public ProfileCompleteDTO checkProfileCompletion(Mechanic mechanic) {
        logger.debug("Checking profile completion for mechanic with ID: {}", mechanic.getId());
        List<String> missingFields = mechanic.getMissingFields();
        return new ProfileCompleteDTO(missingFields.isEmpty(), missingFields);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByUser", key = "#id")
    public Optional<MechanicResponseDTO> findByUserId(Long id) {
        if (mechanicCacheByUserId.containsKey(id)) {
            logger.debug("[CACHE HIT] Mechanic fetched by userId={}", id);
            return Optional.of(mechanicCacheByUserId.get(id));
        }

        logger.info("[FETCH] Fetching mechanic by user ID={}", id);
        Optional<MechanicResponseDTO> dto = mechanicRepository.findByUserId(id).map(mapper::toResponseDTO);
        dto.ifPresent(mech -> mechanicCacheByUserId.put(id, mech));
        return dto;
    }

    @Override
    public boolean isDetailsCompleted(Long userId) {
        return mechanicRepository.findByUserId(userId)
                .map(Mechanic::isComplete)
                .orElse(false);
    }

    @Transactional
    @Override
    @CachePut(value = "mechanics", key = "#result.id")
    @CacheEvict(value = {"allMechanics", "mechanicsByGarage"}, allEntries = true)
    public MechanicResponseDTO createMechanic(MechanicRequestDTO mechanicRequestDTO,
                                              MultipartFile profilePic,
                                              MultipartFile nationalIDPic,
                                              MultipartFile professionalCertificate,
                                              MultipartFile anyRelevantCertificate,
                                              MultipartFile policeClearanceCertificate) throws IOException {

        logger.info("[CREATE] Creating mechanic for national ID: {}", mechanicRequestDTO.getNationalIdNumber());

        if (nationalIdSet.contains(mechanicRequestDTO.getNationalIdNumber()) ||
                mechanicRepository.findMechanicByNationalIdNumber(mechanicRequestDTO.getNationalIdNumber()).isPresent()) {
            throw new ResourceNotFoundException("Mechanic with this National ID already exists");
        }

        Mechanic mechanic = mapper.toEntity(mechanicRequestDTO);
        User user = authenticationService.getCurrentUser();
        mechanic.setUser(user);

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));
            mechanic.setGarage(garage);
        }

        uploadFilesForMechanic(mechanic, user.getId(), profilePic, nationalIDPic, professionalCertificate, anyRelevantCertificate, policeClearanceCertificate);

        Mechanic saved = mechanicRepository.save(mechanic);
        MechanicResponseDTO dto = mapper.toResponseDTO(saved);

        nationalIdSet.add(mechanic.getNationalIdNumber());
        mechanicCacheById.put(saved.getId(), dto);
        mechanicCacheByUserId.put(user.getId(), dto);
        mechanicCacheByNationalId.put(mechanic.getNationalIdNumber(), dto);
        cachedAllMechanics.add(dto);

        if (mechanic.getGarage() != null)
            mechanicCacheByGarageId.computeIfAbsent(mechanic.getGarage().getGarageId(), k -> new ArrayList<>()).add(dto);

        return dto;
    }


    private void uploadFilesForMechanic(Mechanic mechanic, Long userId,
                                        MultipartFile profilePic,
                                        MultipartFile nationalIDPic,
                                        MultipartFile professionalCertfificate,
                                        MultipartFile anyRelevantCertificate,
                                        MultipartFile policeClearanceCertficate) throws IOException {
        String baseFolder = "MechanicFiles/" + userId + "/";

        Map<MultipartFile, Consumer<String>> files = Map.of(
                profilePic, mechanic::setProfilePic,
                nationalIDPic, mechanic::setNationalIDPic,
                professionalCertfificate, mechanic::setProfessionalCertfificate,
                anyRelevantCertificate, mechanic::setAnyRelevantCertificate,
                policeClearanceCertficate, mechanic::setPoliceClearanceCertficate
        );

        for (Map.Entry<MultipartFile, Consumer<String>> entry : files.entrySet()) {
            MultipartFile file = entry.getKey();
            if (file != null && !file.isEmpty()) {
                String ext = getFileExtension(file.getOriginalFilename());
                String uniqueName = baseFolder + UUID.randomUUID() + ext;
                String fileUrl = ossService.uploadFile(uniqueName, file.getInputStream());
                entry.getValue().accept(fileUrl);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByNationalId", key = "#nationalIdNumber")
    public Optional<MechanicResponseDTO> getMechanicByNationalId(Integer nationalIdNumber) {
        if (mechanicCacheByNationalId.containsKey(nationalIdNumber)) {
            logger.debug("[CACHE HIT] Mechanic fetched by National ID={}", nationalIdNumber);
            return Optional.of(mechanicCacheByNationalId.get(nationalIdNumber));
        }

        Optional<MechanicResponseDTO> dto = mechanicRepository.findMechanicByNationalIdNumber(nationalIdNumber)
                .map(mapper::toResponseDTO);
        dto.ifPresent(val -> mechanicCacheByNationalId.put(nationalIdNumber, val));
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "allMechanics")
    public List<MechanicResponseDTO> getAllMechanics() {
        if (!cachedAllMechanics.isEmpty()) {
            logger.debug("[CACHE HIT] Returning all mechanics from in-memory cache");
            return cachedAllMechanics;
        }

        List<MechanicResponseDTO> mechanics = mechanicRepository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
        cachedAllMechanics.addAll(mechanics);
        return mechanics;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByGarage", key = "#garageId")
    public List<MechanicResponseDTO> getMechanicsByGarageId(Long garageId) {
        if (mechanicCacheByGarageId.containsKey(garageId)) {
            logger.debug("[CACHE HIT] Mechanics fetched by garageId={}", garageId);
            return mechanicCacheByGarageId.get(garageId);
        }

        List<MechanicResponseDTO> mechanics = mechanicRepository.findByGarageId(garageId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
        mechanicCacheByGarageId.put(garageId, mechanics);
        return mechanics;
    }

    @Transactional
    @Override
    @CachePut(value = "mechanicsByUser", key = "#result.user.id")
    @CacheEvict(value = {"allMechanics", "mechanicsByGarage"}, allEntries = true)
    public MechanicResponseDTO updateOwnMechanic(MechanicRequestDTO mechanicRequestDTO,
                                                 MultipartFile profilePic,
                                                 MultipartFile nationalIDPic,
                                                 MultipartFile professionalCertificate,
                                                 MultipartFile anyRelevantCertificate,
                                                 MultipartFile policeClearanceCertificate) {

        logger.info("[UPDATE] Updating profile for currently authenticated Mechanic");

        User user = authenticationService.getCurrentUser();
        Mechanic mechanic = mechanicRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile not found for user: " + user.getEmail()));

        mapper.updateEntityFromDTO(mechanicRequestDTO, mechanic);

        if (mechanicRequestDTO.getGarageId() != null) {
            Garage garage = garageRepository.findByGarageId(mechanicRequestDTO.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage not found"));
            mechanic.setGarage(garage);
        }

        try {
            uploadFilesForMechanic(mechanic, user.getId(),
                    profilePic,
                    nationalIDPic,
                    professionalCertificate,
                    anyRelevantCertificate,
                    policeClearanceCertificate);
        } catch (IOException e) {
            throw new BadRequestException("Error processing uploaded file(s): " + e.getMessage());
        }

        Mechanic updated = mechanicRepository.save(mechanic);
        MechanicResponseDTO dto = mapper.toResponseDTO(updated);

        mechanicCacheById.put(updated.getId(), dto);
        mechanicCacheByUserId.put(user.getId(), dto);
        mechanicCacheByNationalId.put(updated.getNationalIdNumber(), dto);
        cachedAllMechanics.replaceAll(m -> Objects.equals(m.getId(), updated.getId()) ? dto : m);

        if (mechanic.getGarage() != null)
            mechanicCacheByGarageId.computeIfAbsent(mechanic.getGarage().getGarageId(), k -> new ArrayList<>()).add(dto);

        logger.info("[UPDATE SUCCESS] Mechanic profile updated for user={}", user.getEmail());
        return dto;
    }


    @Transactional
    @Override
    @CacheEvict(value = {"mechanics", "allMechanics", "mechanicsByGarage"}, allEntries = true)
    public String deleteMechanic(Long id) {
        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found"));

        mechanicCacheById.remove(id);
        mechanicCacheByUserId.remove(mechanic.getUser().getId());
        mechanicCacheByNationalId.remove(mechanic.getNationalIdNumber());
        nationalIdSet.remove(mechanic.getNationalIdNumber());
        cachedAllMechanics.removeIf(m -> Objects.equals(m.getId(), id));

        if (mechanic.getGarage() != null)
            mechanicCacheByGarageId.getOrDefault(mechanic.getGarage().getGarageId(), new ArrayList<>())
                    .removeIf(m -> Objects.equals(m.getId(), id));

        mechanicRepository.deleteById(id);
        return "Mechanic deleted successfully";
    }

    @Override
    public Optional<String> getMechanicFilesUrlByNationalId(Integer nationalId, int expiryMinutes) {
        return mechanicRepository.findMechanicByNationalIdNumber(nationalId)
                .map(mechanic -> {
                    String savedUrl = mechanic.getNationalIDPic();
                    if (savedUrl == null || savedUrl.isBlank()) return null;
                    String objectKey = getObjectNameFromUrl(savedUrl, ossService.getBucketName(), ossService.getEndpoint());
                    return ossService.generatePresignedUrl(objectKey, expiryMinutes);
                });
    }
}
