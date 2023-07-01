package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

    private static final int COST = 1;
    private static final Long ACCOUNT_ID = 1L;
    private static final int NUMBER_OF_SEATS = 1;

    @Mock
    private TicketPurchaseRequest ticketPurchaseRequest;
    @Mock
    private TicketPaymentServiceImpl ticketPaymentService;
    @Mock
    private SeatReservationService seatReservationService;
    @InjectMocks
    private TicketServiceImpl underTest;

    @Test
    void purchaseTickets() {
        when(ticketPurchaseRequest.getAccountId()).thenReturn(ACCOUNT_ID);
        when(ticketPurchaseRequest.numberOfSeats()).thenReturn(NUMBER_OF_SEATS);
        when(ticketPurchaseRequest.cost()).thenReturn(COST);

        underTest.purchaseTickets(ticketPurchaseRequest);

        verify(seatReservationService).reserveSeat(ACCOUNT_ID, NUMBER_OF_SEATS);
        verify(ticketPaymentService).makePayment(ACCOUNT_ID, COST);
    }
}
