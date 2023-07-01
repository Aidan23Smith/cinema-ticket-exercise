package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;


public class TicketServiceImpl implements TicketService {

    private final TicketPaymentServiceImpl ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentServiceImpl ticketPaymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(TicketPurchaseRequest ticketPurchaseRequest) throws InvalidPurchaseException {
        ticketPurchaseRequest.validate();
        seatReservationService.reserveSeat(ticketPurchaseRequest.getAccountId(), ticketPurchaseRequest.numberOfSeats());
        ticketPaymentService.makePayment(ticketPurchaseRequest.getAccountId(), ticketPurchaseRequest.cost());
    }
}
