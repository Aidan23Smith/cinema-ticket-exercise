package uk.gov.dwp.uc.pairtest.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.NEGATIVE_QUANTITY_OF_TICKETS_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.NULL_TYPE_ERROR;

@ExtendWith(MockitoExtension.class)
public class TicketRequestTest {

    private static final TicketRequest.Type TYPE = TicketRequest.Type.ADULT;
    private static final int NUMBER_OF_TICKETS = 3;

    @Test
    void getNoOfTickets() {
        TicketRequest underTest = new TicketRequest(TYPE, NUMBER_OF_TICKETS);
        assertThat(underTest.getNoOfTickets(), is(NUMBER_OF_TICKETS));
    }

    @Test
    void getTicketType() {
        TicketRequest underTest = new TicketRequest(TYPE, NUMBER_OF_TICKETS);
        assertThat(underTest.getTicketType(), is(TYPE));
    }

    @Nested
    class ValidateRequest {

        @ParameterizedTest(name = "Valid input of {0} tickets")
        @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
        void validQuantityAndType(int numberOfTickets) {
            TicketRequest underTest = new TicketRequest(TYPE, numberOfTickets);
            assertDoesNotThrow(underTest::validateRequest);
        }

        @ParameterizedTest(name = "Invalid input of {0} tickets")
        @ValueSource(ints = {-1, Integer.MIN_VALUE})
        void invalidQuantity(int numberOfTickets) {
            TicketRequest underTest = new TicketRequest(TYPE, numberOfTickets);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                                                              underTest::validateRequest);

            assertThat(exception.getMessage(), is(NEGATIVE_QUANTITY_OF_TICKETS_ERROR));
        }

        @ParameterizedTest(name = "Valid input of {0} tickets")
        @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
        void validQuantity(int numberOfTickets) {
            TicketRequest underTest = new TicketRequest(TYPE, numberOfTickets);
            assertDoesNotThrow(underTest::validateRequest);
        }

        @Test
        void nullType() {
            TicketRequest underTest = new TicketRequest(null, NUMBER_OF_TICKETS);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                                                              underTest::validateRequest);

            assertThat(exception.getMessage(), is(NULL_TYPE_ERROR));
        }
    }

    @ParameterizedTest(name = "Cost with {0} {1}")
    @CsvSource({"0, ADULT, 0",
                "0, CHILD, 0",
                "0, INFANT, 0",
                "1, ADULT, 20",
                "1, CHILD, 10",
                "1, INFANT, 0",
                "2, ADULT, 40",
                "2, CHILD, 20",
                "2, INFANT, 0",})
    void getRequestCost(int numberOfTickets, TicketRequest.Type type, int expectedCost) {
        TicketRequest underTest = new TicketRequest(type, numberOfTickets);

        assertThat(underTest.getRequestCost(), is(expectedCost));
    }
}
