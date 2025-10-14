package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.AssignMechanics;
import com.eclectics.Garage.model.AssignMechanicStatus;
import com.eclectics.Garage.service.AssignMechanicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignMechanicsController {

    private final AssignMechanicService assignMechanicService;

    public AssignMechanicsController(AssignMechanicService assignMechanicService) {
        this.assignMechanicService = assignMechanicService;
    }

    @PostMapping
    public AssignMechanics assignRequest(@RequestParam Long requestId,
                                         @RequestParam Long mechanicId) {
        return assignMechanicService.assignRequestToMechanic(requestId, mechanicId);
    }

    @PutMapping("/{assignmentId}/status")
    public AssignMechanics updateStatus(@PathVariable Long assignmentId,
                                       @RequestParam AssignMechanicStatus status) {
        return assignMechanicService.updateAssignmentStatus(assignmentId, status);
    }

    @GetMapping("/mechanic/{mechanicId}")
    public List<AssignMechanics> getAssignmentsByMechanic(@PathVariable Long mechanicId) {
        return assignMechanicService.getAssignmentsByMechanic(mechanicId);
    }

    @GetMapping("/request/{requestId}")
    public List<AssignMechanics> getAssignmentByRequest(@PathVariable Long requestId) {
        return assignMechanicService.getAssignmentByRequest(requestId);
    }

    @GetMapping
    public List<AssignMechanics> getAllAssignments() {
        return assignMechanicService.getAllAssignments();
    }
}

