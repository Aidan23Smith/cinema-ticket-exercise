package uk.gov.dwp.uc.pairtest.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest.INVALID_ACCOUNT_ID_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest.NO_ADULTS_WERE_PRESENT_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest.NO_TICKETS_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest.NO_TICKET_REQUEST_RECEIVED;
import static uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest.TOO_MANY_INFANTS_TO_ADULTS_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest.TOO_MANY_TICKETS_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.NEGATIVE_QUANTITY_OF_TICKETS_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.NULL_TYPE_ERROR;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.Type.CHILD;
import static uk.gov.dwp.uc.pairtest.domain.TicketRequest.Type.INFANT;

public class TicketPurchaseRequestTest {

    private static final Long ACCOUNT_ID = 1L;

    private static final TicketRequest[] TICKET_REQUEST = new TicketRequest[0];
    private static final TicketRequest[] LONG_VALID_TICKET_REQUESTS = new TicketRequest[]{
        new TicketRequest(INFANT, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(CHILD, 1),
        new TicketRequest(ADULT, 1)
    };

    @Test
    void getAccountId() {
        TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, TICKET_REQUEST);

        assertThat(underTest.getAccountId(), is(ACCOUNT_ID));
    }

    @Nested
    class ValidateAccountID {

        private static final TicketRequest[] TICKET_REQUEST = new TicketRequest[]{new TicketRequest(ADULT, 1)};

        @ParameterizedTest(name = "Valid accountId {0}")
        @ValueSource(longs = {1L, Long.MAX_VALUE})
        void validId(long accountId) {
            TicketPurchaseRequest underTest = new TicketPurchaseRequest(accountId, TICKET_REQUEST);
            assertDoesNotThrow(underTest::validate);
        }

        @ParameterizedTest(name = "Invalid accountId {0}")
        @ValueSource(longs = {0L, -1L, Long.MIN_VALUE})
        void invalidId(Long accountId) {
            String expectedException = String.format(INVALID_ACCOUNT_ID_ERROR, accountId);
            TicketPurchaseRequest underTest = new TicketPurchaseRequest(accountId, TICKET_REQUEST);

            InvalidPurchaseException actualException = assertThrows(InvalidPurchaseException.class,
                                                                    underTest::validate);

            assertThat(actualException.getMessage(), is(expectedException));
        }
    }

    @Nested
    class ValidateTicketRequests {

        @ParameterizedTest(name = "Valid ticket requests with {0} adults, {1} children, and {2} infants")
        @CsvSource({"1, 0, 0",
                    "1, 19, 0",
                    "6, 0, 12",
                    "20, 0, 0"})
        void validTicketRequests(int numberOfAdults, int numberOfChildren, int numberOfInfants) {
            TicketRequest[] ticketRequests = createTicketRequests(numberOfAdults,
                                                                  numberOfChildren,
                                                                  numberOfInfants);

            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, ticketRequests);

            assertDoesNotThrow(underTest::validate);
        }

        @Test
        void validTicketRequests_extreme() {
            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, LONG_VALID_TICKET_REQUESTS);

            assertDoesNotThrow(underTest::validate);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("invalidRequests")
        void invalidTicketRequest(String testName, TicketRequest[] ticketRequests, String expectedException) {
            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, ticketRequests);

            InvalidPurchaseException actualException = assertThrows(InvalidPurchaseException.class,
                                                                    underTest::validate);

            assertThat(actualException.getMessage(), is(expectedException));
        }

        private static Stream<Arguments> invalidRequests() {
            return Stream.of(
                Arguments.of("21 separate requests",
                             new TicketRequest[]{
                                 new TicketRequest(INFANT, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(CHILD, 1),
                                 new TicketRequest(ADULT, 1)
                             }, String.format(TOO_MANY_TICKETS_ERROR, 21)),
                Arguments.of("Only infants",
                             createTicketRequests(0, 0, 1),
                             NO_ADULTS_WERE_PRESENT_ERROR),
                Arguments.of("Only children",
                             createTicketRequests(0, 1, 0),
                             NO_ADULTS_WERE_PRESENT_ERROR),
                Arguments.of("Only children and infants",
                             createTicketRequests(0, 1, 1),
                             NO_ADULTS_WERE_PRESENT_ERROR),
                Arguments.of("Negative quantity of seats",
                             createTicketRequests(-1, 1, 1),
                             NEGATIVE_QUANTITY_OF_TICKETS_ERROR),
                Arguments.of("30 seats split into requests",
                             createTicketRequests(10, 10, 10),
                             String.format(TOO_MANY_TICKETS_ERROR, 30)),
                Arguments.of("30 seats split in one requests",
                             new TicketRequest[]{
                                 new TicketRequest(ADULT, 30)
                             },
                             String.format(TOO_MANY_TICKETS_ERROR, 30)),
                Arguments.of("Only zero requests",
                             createTicketRequests(0, 0, 0),
                             NO_TICKETS_ERROR),
                Arguments.of("Empty requests",
                             new TicketRequest[0],
                             NO_TICKET_REQUEST_RECEIVED),
                Arguments.of("Null requests",
                             null,
                             NO_TICKET_REQUEST_RECEIVED),
                Arguments.of("Null request type",
                             new TicketRequest[]{
                                 new TicketRequest(null, 0)
                             },
                             NULL_TYPE_ERROR),
                Arguments.of("More than double number of infants to adults",
                             createTicketRequests(1, 0, 3),
                             TOO_MANY_INFANTS_TO_ADULTS_ERROR)
            );
        }
    }

    @Nested
    class Cost {

        @ParameterizedTest(name = "Test cost calculation with {0} adults, {1} children, and {2} infants")
        @CsvSource({"1, 0, 0, 20",
                    "0, 1, 0, 10",
                    "0, 0, 1, 0",
                    "1, 1, 1, 30",
                    "20, 0, 0, 400",
                    "19, 0, 1, 380"})
        void testCalculateCost(int numberOfAdults, int numberOfChildren, int numberOfInfants, int expectedCost) {
            TicketRequest[] ticketRequests = createTicketRequests(numberOfAdults,
                                                                  numberOfChildren,
                                                                  numberOfInfants);

            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, ticketRequests);

            assertThat(underTest.cost(), is(expectedCost));
        }

        @Test
        void testCalculateCost_LongRequest() {
            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, LONG_VALID_TICKET_REQUESTS);

            int expectedCost = 18 * CHILD.getCost() + ADULT.getCost();

            assertThat(underTest.cost(), is(expectedCost));
        }
    }

    @Nested
    class NumberOfSeats {

        @ParameterizedTest(name = "Test seat calculation with {0} adults, {1} children, and {2} infants")
        @CsvSource({"1, 0, 0, 1",
                    "0, 1, 0, 1",
                    "0, 0, 1, 0",
                    "1, 1, 1, 2",
                    "20, 0, 0, 20",
                    "19, 0, 1, 19",
                    "1, 0, 19, 1"})
        void testNumberOfSeats(int numberOfAdults, int numberOfChildren, int numberOfInfants, int expectedNumberOfSeats) {
            TicketRequest[] ticketRequests = createTicketRequests(numberOfAdults,
                                                                  numberOfChildren,
                                                                  numberOfInfants);

            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, ticketRequests);

            assertThat(underTest.numberOfSeats(), is(expectedNumberOfSeats));
        }

        @Test
        void testNumberOfSeats_LongRequest() {
            TicketPurchaseRequest underTest = new TicketPurchaseRequest(ACCOUNT_ID, LONG_VALID_TICKET_REQUESTS);

            assertThat(underTest.numberOfSeats(), is(19));
        }
    }

    private static TicketRequest[] createTicketRequests(int numberOfAdults,
                                                        int numberOfChildren,
                                                        int numberOfInfants) {
        return new TicketRequest[]{
            new TicketRequest(ADULT, numberOfAdults),
            new TicketRequest(CHILD, numberOfChildren),
            new TicketRequest(INFANT, numberOfInfants)
        };
    }
}
