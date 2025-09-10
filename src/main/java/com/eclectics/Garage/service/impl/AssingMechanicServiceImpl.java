package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.model.*;
import com.eclectics.Garage.repository.AssignMechanicsRepository;
import com.eclectics.Garage.repository.MechanicRepository;
import com.eclectics.Garage.repository.RequestServiceRepository;
import com.eclectics.Garage.service.AssignMechanicService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssingMechanicServiceImpl implements AssignMechanicService {

    private final AssignMechanicsRepository assignMechanicsRepository;
    private final MechanicRepository mechanicRepository;
    private final RequestServiceRepository requestServiceRepository;

    public AssingMechanicServiceImpl(AssignMechanicsRepository assignMechanicsRepository, MechanicRepository mechanicRepository, RequestServiceRepository requestServiceRepository) {
        this.assignMechanicsRepository = assignMechanicsRepository;
        this.mechanicRepository = mechanicRepository;
        this.requestServiceRepository = requestServiceRepository;
    }

    @Override
    public AssignMechanics assignRequestToMechanic(Long requestId, Long mechanicId) {
        ServiceRequest serviceRequest = requestServiceRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service with this id does not exist"));

        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(()-> new RuntimeException("Mechanic with this id does not exist"));

        AssignMechanics assignRequests = new AssignMechanics();

        assignRequests.setService(serviceRequest);
        assignRequests.setMechanic(mechanic);
        assignRequests.setStatus(AssignmentStatus.ASSINGED);
        assignRequests.setAssignedAt(LocalDateTime.now());
        assignRequests.setUpdatedAt(LocalDateTime.now());
        return assignMechanicsRepository.save(assignRequests);
    }

    @Override
    public AssignMechanics updateAssignmentStatus(Long assignmentId, AssignmentStatus status) {
        AssignMechanics assignRequests = assignMechanicsRepository.findById(assignmentId)
                .orElseThrow(()-> new RuntimeException("Request with this id does not exist"));

        assignRequests.setStatus(AssignmentStatus.ACCEPTED);
        assignRequests.setUpdatedAt(LocalDateTime.now());
        return assignMechanicsRepository.save(assignRequests);
    }

    @Override
    public List<AssignMechanics> getAssignmentsByMechanic(Long mechanicId) {
        return assignMechanicsRepository.findByMechanicId(mechanicId);
    }

    @Override
    public List<AssignMechanics> getAssignmentByRequest(Long requestId) {
        return assignMechanicsRepository.findByService_Id(requestId);
    }

    @Override
    public List<AssignMechanics> getAllAssignments() {
        return assignMechanicsRepository.findAll();
    }
}
