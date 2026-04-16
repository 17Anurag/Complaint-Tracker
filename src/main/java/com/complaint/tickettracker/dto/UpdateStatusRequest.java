package com.complaint.tickettracker.dto;

import com.complaint.tickettracker.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private TicketStatus status;
}
