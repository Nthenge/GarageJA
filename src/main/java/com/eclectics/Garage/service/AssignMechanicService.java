package com.eclectics.Garage.service;

import com.eclectics.Garage.model.AssignMechanics;
import com.eclectics.Garage.model.AssignmentStatus;

import java.util.List;

public interface AssignMechanicService {
    AssignMechanics assignRequestToMechanic(Long requestId, Long mechanicId);
    AssignMechanics updateAssignmentStatus(Long assignmentId, AssignmentStatus status);
    List<AssignMechanics> getAssignmentsByMechanic(Long mechanicId);
    List<AssignMechanics> getAssignmentByRequest(Long requestId);
    List<AssignMechanics> getAllAssignments();
}

