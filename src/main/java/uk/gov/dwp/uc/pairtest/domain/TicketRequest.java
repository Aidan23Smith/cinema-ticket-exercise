package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Objects;

/**
 * Should be an Immutable Object
 */
public class TicketRequest {

    private final int noOfTickets;
    private final Type type;
    static final String NEGATIVE_QUANTITY_OF_TICKETS_ERROR = "Cannot have negative quantity of tickets.";
    static final String NULL_TYPE_ERROR = "Ticket type cannot be null.";

    public TicketRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public void validateRequest() {
        if (noOfTickets < 0) {
            throw new InvalidPurchaseException(NEGATIVE_QUANTITY_OF_TICKETS_ERROR);
        }

        if (Objects.isNull(type)) {
            throw new InvalidPurchaseException(NULL_TYPE_ERROR);
        }
    }

    public int getRequestCost() {
        return noOfTickets * type.getCost();
    }

    public enum Type {
        ADULT(20),
        CHILD(10),
        INFANT(0);

        private final int cost;

        Type(int cost) {
            this.cost = cost;
        }

        int getCost() {
            return cost;
        }
    }

}
