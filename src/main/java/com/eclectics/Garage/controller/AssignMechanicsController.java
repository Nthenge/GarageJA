package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
import com.eclectics.Garage.model.AssignMechanics;
import com.eclectics.Garage.model.AssignMechanicStatus;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.AssignMechanicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasAnyAuthorityRole('SYSTEM_ADMIN','GARAGE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> assignRequest(@RequestParam Long requestId,
                                                @RequestParam Long mechanicId) {
        AssignMechanicsResponseDTO assignRequest = assignMechanicService.assignRequestToMechanic(requestId, mechanicId);
        return ResponseHandler.generateResponse("Assign Request", HttpStatus.CREATED, assignRequest);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','MECHANIC')")
    @PutMapping("/status/{assignmentId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long assignmentId,
                                               @RequestParam AssignMechanicStatus status) {
        AssignMechanicsResponseDTO updateStatus = assignMechanicService.updateAssignmentStatus(assignmentId, status);
        return ResponseHandler.generateResponse("Update Request Status", HttpStatus.CREATED, updateStatus);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN,'MECHANIC')")
    @GetMapping("/mechanic/{mechanicId}")
    public ResponseEntity<Object>getAssignmentsByMechanic(@PathVariable Long mechanicId) {
        List<AssignMechanicsResponseDTO> updateStatus = assignMechanicService.getAssignmentsByMechanic(mechanicId);
        return ResponseHandler.generateResponse("Get mechanic assingments", HttpStatus.OK, updateStatus);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN,'MECHANIC')")
    @GetMapping("/request/{requestId}")
    public ResponseEntity<Object>getAssignmentByRequest(@PathVariable Long requestId) {
        List<AssignMechanicsResponseDTO> updateStatus = assignMechanicService.getAssignmentByRequest(requestId);
        return ResponseHandler.generateResponse("Get assignments by requests", HttpStatus.OK, updateStatus);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN', 'SYSTEM_ADMIN)")
    @GetMapping
    public ResponseEntity<Object>getAllAssignments() {
        List<AssignMechanicsResponseDTO> allAssignments = assignMechanicService.getAllAssignments();
        return ResponseHandler.generateResponse("All assingments", HttpStatus.OK, allAssignments);
    }
}

