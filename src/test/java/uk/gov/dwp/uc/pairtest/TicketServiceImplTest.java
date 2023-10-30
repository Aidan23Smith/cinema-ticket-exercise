package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest;

import java.util.Optional;

import thirdparty.discount.Discount;
import thirdparty.discount.DiscountService;
import thirdparty.discount.exception.InvalidDiscountCodeException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

    private static final int DISCOUNTED_COST = 1;
    private static final int COST = 2;
    private static final Long ACCOUNT_ID = 1L;
    private static final int NUMBER_OF_SEATS = 1;
    private static final Optional<String> DISCOUNT_CODE = Optional.of("DISCOUNT");
    private static final Discount DISCOUNT = new Discount(0.2);
    private static final Discount NO_DISCOUNT = new Discount(0);


    @Mock
    private TicketPurchaseRequest ticketPurchaseRequest;
    @Mock
    private TicketPaymentService ticketPaymentService;
    @Mock
    private SeatReservationService seatReservationService;
    @Mock
    private DiscountService discountService;
    @InjectMocks
    private TicketServiceImpl underTest;
    @Test
    void purchaseTickets() {
        when(ticketPurchaseRequest.getAccountId()).thenReturn(ACCOUNT_ID);
        when(ticketPurchaseRequest.numberOfSeats()).thenReturn(NUMBER_OF_SEATS);
        when(discountService.getDiscountPercentage(ACCOUNT_ID, DISCOUNT_CODE.orElse(null))).thenReturn(DISCOUNT);
        when(ticketPurchaseRequest.cost(DISCOUNT)).thenReturn(DISCOUNTED_COST);

        underTest.purchaseTickets(ticketPurchaseRequest, DISCOUNT_CODE);

        verify(seatReservationService).reserveSeat(ACCOUNT_ID, NUMBER_OF_SEATS);
        verify(ticketPaymentService).makePayment(ACCOUNT_ID, DISCOUNTED_COST);
    }

    @Test
    void purchaseTickets_invalidDiscountCode() {
        when(ticketPurchaseRequest.getAccountId()).thenReturn(ACCOUNT_ID);
        when(ticketPurchaseRequest.numberOfSeats()).thenReturn(NUMBER_OF_SEATS);
        when(discountService.getDiscountPercentage(ACCOUNT_ID, DISCOUNT_CODE.orElse(null))).thenThrow(new InvalidDiscountCodeException("Invalid"));
        when(ticketPurchaseRequest.cost()).thenReturn(COST);

        underTest.purchaseTickets(ticketPurchaseRequest, DISCOUNT_CODE);

        verify(seatReservationService).reserveSeat(ACCOUNT_ID, NUMBER_OF_SEATS);
        verify(ticketPaymentService).makePayment(ACCOUNT_ID, COST);
    }

    @Test
    void purchaseTickets_emptyDiscountCode() {
        when(ticketPurchaseRequest.getAccountId()).thenReturn(ACCOUNT_ID);
        when(ticketPurchaseRequest.numberOfSeats()).thenReturn(NUMBER_OF_SEATS);
        when(discountService.getDiscountPercentage(ACCOUNT_ID, null)).thenThrow(new InvalidDiscountCodeException("Invalid"));
        when(ticketPurchaseRequest.cost()).thenReturn(COST);

        underTest.purchaseTickets(ticketPurchaseRequest, Optional.empty());

        verify(seatReservationService).reserveSeat(ACCOUNT_ID, NUMBER_OF_SEATS);
        verify(ticketPaymentService).makePayment(ACCOUNT_ID, COST);
    }
}
