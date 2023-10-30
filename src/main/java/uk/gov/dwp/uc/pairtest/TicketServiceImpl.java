package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import thirdparty.discount.Discount;
import thirdparty.discount.DiscountService;
import thirdparty.discount.exception.InvalidDiscountCodeException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;


public class TicketServiceImpl implements TicketService {

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private final DiscountService discountService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService,
                             DiscountService discountService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
        this.discountService = discountService;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(TicketPurchaseRequest ticketPurchaseRequest) throws InvalidPurchaseException {
        ticketPurchaseRequest.validate();
        seatReservationService.reserveSeat(ticketPurchaseRequest.getAccountId(), ticketPurchaseRequest.numberOfSeats());
        
        try {
            Discount discount = discountService.getDiscountPercentage(ticketPurchaseRequest.getAccountId(),
                                                                      ticketPurchaseRequest.getDiscountCode().orElse(null));
            ticketPaymentService.makePayment(ticketPurchaseRequest.getAccountId(), ticketPurchaseRequest.cost(discount.percentage()));
        } catch (InvalidDiscountCodeException exception) {
            ticketPaymentService.makePayment(ticketPurchaseRequest.getAccountId(), ticketPurchaseRequest.cost());
        }
    }
}
