package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.AssignMechanics;
import com.eclectics.Garage.model.AssignMechanicStatus;
import com.eclectics.Garage.service.AssignMechanicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignMechanicsController {

    private final AssignMechanicService assignMechanicService;

    public AssignMechanicsController(AssignMechanicService assignMechanicService) {
        this.assignMechanicService = assignMechanicService;
    }

    @PreAuthorize("hasRole('GARAGE_ADMIN')")
    @PostMapping
    public AssignMechanics assignRequest(@RequestParam Long requestId,
                                         @RequestParam Long mechanicId) {
        return assignMechanicService.assignRequestToMechanic(requestId, mechanicId);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN','MECHANIC')")
    @PutMapping("/{assignmentId}/status")
    public AssignMechanics updateStatus(@PathVariable Long assignmentId,
                                       @RequestParam AssignMechanicStatus status) {
        return assignMechanicService.updateAssignmentStatus(assignmentId, status);
    }

    @PreAuthorize("hasRole('GARAGE_ADMIN')")
    @GetMapping("/mechanic/{mechanicId}")
    public List<AssignMechanics> getAssignmentsByMechanic(@PathVariable Long mechanicId) {
        return assignMechanicService.getAssignmentsByMechanic(mechanicId);
    }

    @PreAuthorize("hasRole('GARAGE_ADMIN')")
    @GetMapping("/request/{requestId}")
    public List<AssignMechanics> getAssignmentByRequest(@PathVariable Long requestId) {
        return assignMechanicService.getAssignmentByRequest(requestId);
    }

    @PreAuthorize("hasRole('GARAGE_ADMIN')")
    @GetMapping
    public List<AssignMechanics> getAllAssignments() {
        return assignMechanicService.getAllAssignments();
    }
}

