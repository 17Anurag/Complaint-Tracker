package com.complaint.tickettracker.exception;

import com.complaint.tickettracker.enums.TicketStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to) {
        super("Invalid status transition from " + from + " to " + to
                + ". Allowed flow: OPEN → IN_PROGRESS → RESOLVED");
    }
}
