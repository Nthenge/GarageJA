package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
import com.eclectics.Garage.model.AssignMechanicStatus;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

public interface AssignMechanicService {
    AssignMechanicsResponseDTO assignRequestToMechanic(Long requestId, Long mechanicId);
    AssignMechanicsResponseDTO updateAssignmentStatus(Long assignmentId, AssignMechanicStatus status);
    List<AssignMechanicsResponseDTO> filterAssignMechanics(
            AssignMechanicStatus status,
            LocalDate assignedDate,
            Long requestId,
            Long mechanicId
    );
}

