package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
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
    public AssignMechanicsResponseDTO assignRequest(@RequestParam Long requestId,
                                         @RequestParam Long mechanicId) {
        return assignMechanicService.assignRequestToMechanic(requestId, mechanicId);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN','MECHANIC')")
    @PutMapping("/status/{assignmentId}")
    public AssignMechanicsResponseDTO updateStatus(@PathVariable Long assignmentId,
                                       @RequestParam AssignMechanicStatus status) {
        return assignMechanicService.updateAssignmentStatus(assignmentId, status);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN,'MECHANIC')")
    @GetMapping("/mechanic/{mechanicId}")
    public List<AssignMechanicsResponseDTO> getAssignmentsByMechanic(@PathVariable Long mechanicId) {
        return assignMechanicService.getAssignmentsByMechanic(mechanicId);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN,'MECHANIC')")
    @GetMapping("/request/{requestId}")
    public List<AssignMechanicsResponseDTO> getAssignmentByRequest(@PathVariable Long requestId) {
        return assignMechanicService.getAssignmentByRequest(requestId);
    }

    @PreAuthorize("hasAnyAuthority('GARAGE_ADMIN', 'SYSTEM_ADMIN)")
    @GetMapping
    public List<AssignMechanicsResponseDTO> getAllAssignments() {
        return assignMechanicService.getAllAssignments();
    }
}

