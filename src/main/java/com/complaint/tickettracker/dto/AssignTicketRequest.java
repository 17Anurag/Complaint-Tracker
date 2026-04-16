package com.complaint.tickettracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignTicketRequest {

    @NotBlank(message = "Assignee name is required")
    private String assignedTo;
}
