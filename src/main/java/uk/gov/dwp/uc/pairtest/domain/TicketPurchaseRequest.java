package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.Type.INFANT;

/**
 * Should be an Immutable Object
 */
public class TicketPurchaseRequest {

    private final long accountId;
    private final TicketRequest[] ticketRequests;
    private final String discountCode; 

    static final String INVALID_ACCOUNT_ID_ERROR = "Invalid accountId: %d.";
    static final String TOO_MANY_TICKETS_ERROR = "Too many tickets were requested: %d.";
    static final String NO_TICKETS_ERROR = "No tickets were requested.";
    static final String NO_ADULTS_WERE_PRESENT_ERROR = "No adults were present in request.";
    static final String TOO_MANY_INFANTS_TO_ADULTS_ERROR = "Too many infants compared to adults.";
    static final String NO_TICKET_REQUEST_RECEIVED = "No ticket request received.";

    public TicketPurchaseRequest(long accountId, TicketRequest[] ticketRequests, String discountCode) {
        this.accountId = accountId;
        this.ticketRequests = ticketRequests;
        this.discountCode = discountCode;
    }

    public long getAccountId() {
        return accountId;
    }
    
    public Optional<String> getDiscountCode() {
        return Optional.ofNullable(discountCode);
    }

    public void validate() {
        if (accountId < 1L) {
            throw new InvalidPurchaseException(String.format(INVALID_ACCOUNT_ID_ERROR, accountId));
        }

        if (Objects.isNull(ticketRequests) || ticketRequests.length == 0) {
            throw new InvalidPurchaseException(NO_TICKET_REQUEST_RECEIVED);
        }

        Arrays.stream(ticketRequests).forEach(TicketRequest::validateRequest);

        int numberOfTickets = numberOfTickets();

        if (numberOfTickets > 20) {
            throw new InvalidPurchaseException(String.format(TOO_MANY_TICKETS_ERROR, numberOfTickets));
        } else if (numberOfTickets == 0) {
            throw new InvalidPurchaseException(NO_TICKETS_ERROR);
        }

        int numberOfAdults = numberOfTicketsForType(ADULT);
        if (numberOfAdults == 0) {
            throw new InvalidPurchaseException(NO_ADULTS_WERE_PRESENT_ERROR);
        } else if (2 * numberOfAdults < numberOfTicketsForType(INFANT)) {
            throw new InvalidPurchaseException(TOO_MANY_INFANTS_TO_ADULTS_ERROR);
        }
    }

    public int numberOfSeats() {
        return Arrays.stream(ticketRequests)
            .filter(request -> !request.getTicketType().equals(INFANT))
            .mapToInt(TicketRequest::getNoOfTickets)
            .sum();
    }

    public int cost() {
        return cost(0);
    }

    public int cost(double discount) {

        return (int) (Arrays.stream(ticketRequests)
                          .map(TicketRequest::getRequestCost)
                          .reduce(0, Integer::sum)
                      * (1 - discount));
    }

    private int numberOfTicketsForType(TicketRequest.Type type) {
        return Arrays.stream(ticketRequests)
            .filter(request -> request.getTicketType().equals(type))
            .map(TicketRequest::getNoOfTickets)
            .reduce(0, Integer::sum);
    }

    private int numberOfTickets() {
        return EnumSet.allOf(TicketRequest.Type.class).stream()
            .map(this::numberOfTicketsForType)
            .reduce(0, Integer::sum);
    }
}
