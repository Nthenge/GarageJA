package com.eclectics.Garage.service;

import com.eclectics.Garage.model.AssignMechanics;
import com.eclectics.Garage.model.AssignMechanicStatus;

import java.util.List;

public interface AssignMechanicService {
    AssignMechanics assignRequestToMechanic(Long requestId, Long mechanicId);
    AssignMechanics updateAssignmentStatus(Long assignmentId, AssignMechanicStatus status);
    List<AssignMechanics> getAssignmentsByMechanic(Long mechanicId);
    List<AssignMechanics> getAssignmentByRequest(Long requestId);
    List<AssignMechanics> getAllAssignments();
}

