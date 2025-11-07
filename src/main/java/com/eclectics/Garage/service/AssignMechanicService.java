package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
import com.eclectics.Garage.model.AssignMechanicStatus;

import java.util.List;

public interface AssignMechanicService {
    AssignMechanicsResponseDTO assignRequestToMechanic(Long requestId, Long mechanicId);
    AssignMechanicsResponseDTO updateAssignmentStatus(Long assignmentId, AssignMechanicStatus status);
    List<AssignMechanicsResponseDTO> getAssignmentsByMechanic(Long mechanicId);
    List<AssignMechanicsResponseDTO> getAssignmentByRequest(Long requestId);
    List<AssignMechanicsResponseDTO> getAllAssignments();
}

