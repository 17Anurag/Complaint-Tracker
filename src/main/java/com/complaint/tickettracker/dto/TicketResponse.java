package com.complaint.tickettracker.dto;

import com.complaint.tickettracker.enums.TicketCategory;
import com.complaint.tickettracker.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketCategory category;
    private TicketStatus status;
    private String assignedTo;
    private String raisedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
