package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
import com.eclectics.Garage.mapper.AssignMechanicsMapper;
import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.AssignMechanicsRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.RequestServiceRepository;
import com.eclectics.Garage.service.AssignMechanicService;
import com.eclectics.Garage.exception.GarageExceptions.ResourceNotFoundException;

import com.eclectics.Garage.specificationExecutor.AssingMechanicsSpecificationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AssingMechanicServiceImpl implements AssignMechanicService {

    private static final Logger logger = LoggerFactory.getLogger(AssingMechanicServiceImpl.class);

    private final AssignMechanicsRepository assignMechanicsRepository;
    private final MechanicRepository mechanicRepository;
    private final RequestServiceRepository requestServiceRepository;
    private final AssignMechanicsMapper mapper;

    private final Map<Long, List<AssignMechanics>> mechanicAssignmentsMap = new ConcurrentHashMap<>();
    private final Map<Long, List<AssignMechanics>> requestAssignmentsMap = new ConcurrentHashMap<>();
    private final Set<String> assignedPairs = Collections.synchronizedSet(new HashSet<>()); // to prevent duplicates

    public AssingMechanicServiceImpl(
            AssignMechanicsRepository assignMechanicsRepository,
            MechanicRepository mechanicRepository,
            RequestServiceRepository requestServiceRepository,
            AssignMechanicsMapper mapper
    ) {
        this.assignMechanicsRepository = assignMechanicsRepository;
        this.mechanicRepository = mechanicRepository;
        this.requestServiceRepository = requestServiceRepository;
        this.mapper = mapper;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAssignments", allEntries = true),
            @CacheEvict(value = "assignmentsByMechanic", allEntries = true),
            @CacheEvict(value = "assignmentsByRequest", allEntries = true)
    })
    public AssignMechanicsResponseDTO assignRequestToMechanic(Long requestId, Long mechanicId) {
        logger.info("Assigning service request ID {} to mechanic ID {}", requestId, mechanicId);

        String key = mechanicId + "-" + requestId;

        if (assignedPairs.contains(key)) {
            throw new IllegalStateException("This mechanic is already assigned to this request");
        }

        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with this id does not exist"));

        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic with this id does not exist"));

        AssignMechanics assignRequests = new AssignMechanics();
        assignRequests.setService(serviceRequest);
        assignRequests.setMechanic(mechanic);
        assignRequests.setStatus(AssignMechanicStatus.ASSINGED);
        assignRequests.setAssignedAt(LocalDateTime.now());
        assignRequests.setUpdatedAt(LocalDateTime.now());

        AssignMechanics savedAssignment = assignMechanicsRepository.save(assignRequests);

        mechanicAssignmentsMap
                .computeIfAbsent(mechanicId, k -> new LinkedList<>())
                .add(savedAssignment);

        requestAssignmentsMap
                .computeIfAbsent(requestId, k -> new LinkedList<>())
                .add(savedAssignment);

        assignedPairs.add(key);

        logger.info("Successfully assigned mechanic ID {} to service request ID {}", mechanicId, requestId);
        return mapper.toResponseDTO(savedAssignment);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "allAssignments", allEntries = true),
            @CacheEvict(value = "assignmentsByMechanic", allEntries = true),
            @CacheEvict(value = "assignmentsByRequest", allEntries = true)
    })
    public AssignMechanicsResponseDTO updateAssignmentStatus(Long assignmentId, AssignMechanicStatus status) {
        logger.info("Updating assignment ID {} to status {}", assignmentId, status);

        AssignMechanics assignRequests = assignMechanicsRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with this id does not exist"));

        assignRequests.setStatus(status);
        assignRequests.setUpdatedAt(LocalDateTime.now());

        AssignMechanics updatedAssignment = assignMechanicsRepository.save(assignRequests);

        mechanicAssignmentsMap.getOrDefault(assignRequests.getMechanic().getId(), new LinkedList<>())
                .replaceAll(a -> a.getId().equals(assignmentId) ? updatedAssignment : a);

        requestAssignmentsMap.getOrDefault(assignRequests.getService().getId(), new LinkedList<>())
                .replaceAll(a -> a.getId().equals(assignmentId) ? updatedAssignment : a);

        ServiceRequest serviceRequest = assignRequests.getService();
        if (status == AssignMechanicStatus.ACCEPTED) {
            serviceRequest.setStatus(RequestStatus.IN_PROGRESS);
        } else if (status == AssignMechanicStatus.REJECTED) {
            serviceRequest.setStatus(RequestStatus.CANCELLED);
        }else if (status == AssignMechanicStatus.COMPLETED) {
            serviceRequest.setStatus(RequestStatus.COMPLETED);
        }
        serviceRequest.setUpdatedAt(LocalDateTime.now());
        ServiceRequest updatedRequest = requestServiceRepository.save(serviceRequest);
        logger.info("Service request ID {} automatically updated to IN_PROGRESS", updatedRequest.getId());
        logger.info("Assignment ID {} updated to status {}", assignmentId, status);
        return mapper.toResponseDTO(updatedAssignment);
    }


    @Transactional(readOnly = true)
    @Cacheable(
            value = "allAssignments"
    )
    @Override
    public List<AssignMechanicsResponseDTO> filterAssignMechanics(
            AssignMechanicStatus status,
            LocalDate assignedDate,
            Long requestId,
            Long mechanicId
    ) {
        Specification<AssignMechanics> spec = Specification.allOf(
                AssingMechanicsSpecificationExecutor.statusEquals(status),
                AssingMechanicsSpecificationExecutor.assignedOnDate(assignedDate),
                AssingMechanicsSpecificationExecutor.requestIdEquals(requestId),
                AssingMechanicsSpecificationExecutor.mechanicIdEquals(mechanicId)
        );

        List<AssignMechanics> assignments = assignMechanicsRepository.findAll(spec);

        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException("No assignments match the given criteria.");
        }

        return assignments.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
}
