package com.eclectics.Garage.controller;

import com.eclectics.Garage.dto.AssignMechanicsResponseDTO;
import com.eclectics.Garage.model.AssignMechanics;
import com.eclectics.Garage.model.AssignMechanicStatus;
import com.eclectics.Garage.response.ResponseHandler;
import com.eclectics.Garage.service.AssignMechanicService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/assign")
public class AssignMechanicsController {

    private final AssignMechanicService assignMechanicService;

    public AssignMechanicsController(AssignMechanicService assignMechanicService) {
        this.assignMechanicService = assignMechanicService;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','MECHANIC')")
    @GetMapping("/search")
    public ResponseEntity<Object> filterAssignments(
            @RequestParam(required = false) AssignMechanicStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate assignedDate,
            @RequestParam(required = false) Long requestId,
            @RequestParam(required = false) Long mechanicId
    ) {
        List<AssignMechanicsResponseDTO> assignments = assignMechanicService
                .filterAssignMechanics(status, assignedDate, requestId, mechanicId);

        return ResponseHandler.generateResponse(
                "Filtered assignments retrieved successfully",
                HttpStatus.OK,
                assignments
        );
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> assignRequest(@RequestParam Long requestId,
                                                @RequestParam Long mechanicId) {
        AssignMechanicsResponseDTO assignRequest = assignMechanicService.assignRequestToMechanic(requestId, mechanicId);
        return ResponseHandler.generateResponse("Assign Request", HttpStatus.CREATED, assignRequest);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','GARAGE_ADMIN','MECHANIC')")
    @PutMapping("/update/{assignmentId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long assignmentId,
                                               @RequestParam AssignMechanicStatus status) {
        AssignMechanicsResponseDTO updateStatus = assignMechanicService.updateAssignmentStatus(assignmentId, status);
        return ResponseHandler.generateResponse("Update Request Status", HttpStatus.CREATED, updateStatus);
    }
}

