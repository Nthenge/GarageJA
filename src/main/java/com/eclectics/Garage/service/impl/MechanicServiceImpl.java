package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.*;
import com.eclectics.Garage.exception.GarageExceptions.BadRequestException;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;
import com.eclectics.Garage.mapper.MechanicMapper;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.UsersRepository;
import com.eclectics.Garage.service.AuthenticationService;
import com.eclectics.Garage.service.MechanicService;
import com.eclectics.Garage.service.OSSService;

import com.eclectics.Garage.specificationExecutor.MechanicSpecificationExecutor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@CacheConfig(cacheNames = {"mechanics"})
public class MechanicServiceImpl implements MechanicService {

    private static final Logger logger = LoggerFactory.getLogger(MechanicServiceImpl.class);

    private final MechanicRepository mechanicRepository;
    private final AuthenticationService authenticationService;
    private final MechanicMapper mapper;
    private final OSSService ossService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Long, MechanicResponseDTO> mechanicCacheByUserId = new ConcurrentHashMap<>();
    private final Map<Long, MechanicResponseDTO> mechanicCacheById = new ConcurrentHashMap<>();
    private final Map<Long, List<MechanicResponseDTO>> mechanicCacheByGarageId = new ConcurrentHashMap<>();
    private final Map<Integer, MechanicResponseDTO> mechanicCacheByNationalId = new ConcurrentHashMap<>();
    private final Set<Integer> nationalIdSet = Collections.synchronizedSet(new HashSet<>());

    private List<MechanicResponseDTO> cachedAllMechanics = Collections.synchronizedList(new ArrayList<>());

    public MechanicServiceImpl(MechanicRepository mechanicRepository,
                               AuthenticationService authenticationService, MechanicMapper mapper, OSSService ossService, UsersRepository usersRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, SimpMessagingTemplate messagingTemplate) {
        this.mechanicRepository = mechanicRepository;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
        this.ossService = ossService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.messagingTemplate = messagingTemplate;
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
    public User registerMechanic(MechanicGarageRegisterRequestDTO dto) {

        String MechRawPassword = generateRandomPassword();

        User garageAdmin = authenticationService.getCurrentUser();
        Garage garage = garageAdmin.getGarage();

        User mechanicUser = new User();
        mechanicUser.setFirstname(dto.getFirstname());
        mechanicUser.setSecondname(dto.getSecondname());
        mechanicUser.setEmail(dto.getEmail());
        mechanicUser.setPhoneNumber(dto.getPhoneNumber());
        mechanicUser.setPassword(passwordEncoder.encode(MechRawPassword));
        mechanicUser.setRole(Role.MECHANIC);
        mechanicUser.setEnabled(true);

        Mechanic mechanic = new Mechanic();
        mechanic.setUser(mechanicUser);
        mechanic.setGarage(garage);
        mechanicUser.setMechanic(mechanic);;

        usersRepository.save(mechanicUser);

        sendMechanicCredentials(mechanicUser.getEmail(),MechRawPassword,garage.getBusinessName());

        return mechanicUser;
    }
    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+=-{}[]|:;<>?,./";
        String allChars = upper + lower + digits + special;

        StringBuilder password = getStringBuilder(digits, special, allChars);
        String shuffledPassword = shuffleString(password.toString());

        return shuffledPassword;
    }

    @NotNull
    private static StringBuilder getStringBuilder(String digits, String special, String allChars) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));
        int remainingLength = 7 - password.length();

        for (int i = 0; i < remainingLength; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        return password;
    }

    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);

        StringBuilder output = new StringBuilder();
        for (char c : characters) {
            output.append(c);
        }
        return output.toString();
    }

    public void sendMechanicCredentials(String toEmail, String rawPassword, String garageName) {
        String subject = "Your mechanic account at " + garageName;
        String body = "Hello,\n\n" +
                "An account has been created for you at " + garageName + ".\n" +
                "Please use the credentials below to log in and complete your profile:\n\n" +
                "Email: " + toEmail + "\n" +
                "Password: " + rawPassword + "\n\n" +
                "After login, you will be prompted to complete your profile.\n\n" +
                "Regards,\n" + "GARAGE";

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(body);
        javaMailSender.send(msg);
    }

    private void uploadFilesForMechanic(Mechanic mechanic, Long userId,
                                        MultipartFile profilePic,
                                        MultipartFile nationalIDPic,
                                        MultipartFile professionalCertfificate,
                                        MultipartFile anyRelevantCertificate,
                                        MultipartFile policeClearanceCertficate) throws IOException {

        String baseFolder = "MechanicFiles/" + userId + "/";
        Map<MultipartFile, Consumer<String>> filesToProcess = new HashMap<>();

        if (profilePic != null && !profilePic.isEmpty())
            filesToProcess.put(profilePic, mechanic::setProfilePic);

        if (nationalIDPic != null && !nationalIDPic.isEmpty())
            filesToProcess.put(nationalIDPic, mechanic::setNationalIDPic);

        if (professionalCertfificate != null && !professionalCertfificate.isEmpty())
            filesToProcess.put(professionalCertfificate, mechanic::setProfessionalCertfificate);

        if (anyRelevantCertificate != null && !anyRelevantCertificate.isEmpty())
            filesToProcess.put(anyRelevantCertificate, mechanic::setAnyRelevantCertificate);

        if (policeClearanceCertficate != null && !policeClearanceCertficate.isEmpty())
            filesToProcess.put(policeClearanceCertficate, mechanic::setPoliceClearanceCertficate);

        for (Map.Entry<MultipartFile, Consumer<String>> entry : filesToProcess.entrySet()) {
            MultipartFile file = entry.getKey();
            String ext = getFileExtension(file.getOriginalFilename());
            String uniqueName = baseFolder + UUID.randomUUID() + ext;
            String fileUrl = ossService.uploadFile(uniqueName, file.getInputStream());
            entry.getValue().accept(fileUrl);
        }
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "mechanicsByUser")
    public List<MechanicResponseDTO> filterMechanics(
            String vehicleBrands,
            Integer nationalIdNumber,
            Long garageId
    ) {

        Specification<Mechanic> spec = Specification.allOf(
                MechanicSpecificationExecutor.vehicleBrandsContains(vehicleBrands),
                MechanicSpecificationExecutor.nationalIdEquals(nationalIdNumber),
                MechanicSpecificationExecutor.garageIdEquals(garageId)
        );

        List<Mechanic> mechanics = mechanicRepository.findAll(spec);

        if (mechanics.isEmpty()) {
            throw new ResourceNotFoundException("No mechanics match the given criteria.");
        }

        return mechanics.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }


    @Transactional
    @Override
    @CachePut(value = "mechanicsByUser", key = "#result.id")
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
    public Mechanic updateMechanicLiveLocation(Long mechanicId, MechanicLocationUpdateDTO locationDto) {

        // 1. Fetch the Mechanic entity by ID
        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(() -> {
                    logger.warn("Attempted to update location for non-existent mechanic ID: {}", mechanicId);
                    return new ResourceNotFoundException("Mechanic not found with id: " + mechanicId);
                });

        // 2. Create and configure the new Location object
        Location newLocation = new Location();
        newLocation.setLatitude(locationDto.getLatitude());
        newLocation.setLongitude(locationDto.getLongitude());

        // 3. Update the embedded 'liveLocation' field
        mechanic.setLiveLocation(newLocation);

        logger.info("[LIVE LOCATION] Updated live location for mechanic ID {} to: ({}, {})",
                mechanicId, locationDto.getLatitude(), locationDto.getLongitude());

        // 4. Save the updated entity
        // Since live location changes frequently, we might choose not to update the response DTO caches,
        // but the database persistence is essential.
        Mechanic updatedMechanic = mechanicRepository.save(mechanic);

        try {
            MechanicResponseDTO responseDTO = mapper.toResponseDTO(updatedMechanic);

            String destination = "/topic/mechanic-location/" + mechanicId;

            messagingTemplate.convertAndSend(destination, responseDTO);
            logger.debug("[WS BROADCAST] Sent live location update to destination: {}", destination);
        } catch (Exception e) {
            logger.error("Failed to broadcast live location update for mechanic {}: {}", mechanicId, e.getMessage());
        }
        return updatedMechanic;
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

    @Override
    public List<Mechanic> findMechanicsByGarageId(Long garageId) {
        return mechanicRepository.findByGarage_GarageId(garageId);
    }
}
