# Cinema Tickets Application

## Description
This is a DWP job application example project that creates a service for a cinema ticketing system.
The [TicketService](./src/main/java/uk/gov/dwp/uc/pairtest/TicketService.java) can be called with [TicketRequests](./src/main/java/uk/gov/dwp/uc/pairtest/domain/TicketRequest.java) and an accountId, and calls to further services are made to reserve the correct number of seats and make the payment.
Each [TicketRequest](./src/main/java/uk/gov/dwp/uc/pairtest/domain/TicketRequest.java) will contain the type of ticket as well as how many are required.

## Contributor:
[Aidan Smith](https://github.com/aidan23smith)

## Assumptions:

### `noOfTickets` in `TicketRequest`:
* As it is primitive, `noOfTicket` cannot be null
* It **is** valid to have zero tickets in a `TicketRequest`
* It **is not** valid to have negative `noOfTicket`
* The `noOfTicket` will not exceed the limit of `int`

### `accountId` in `TicketPurchaseRequest`:
* As it is primitive, `accountId` cannot be null
* The `accountId` will not exceed the limit of `long`

### `TicketPurchaseRequest`:
* It **is not** valid to have zero tickets in **every** `TicketRequest` in `TicketPurchaseRequest`
* As infants are a ticket type, they count towards the ticket total
* 'Child and Infant tickets cannot be purchased without purchasing an Adult ticket.' does not imply a 1:1 relationship between children or infants and adults.
* As 'will be sitting on an Adult's lap', there cannot be more than two infants per adult due to running out of space on their laps.

## Reasoning:
I have put as much logic in the domain to increase readability in the `TicketService`.

I have done internal validation as opposed to using javax validation so that I can fully test and control the error messaging for this exercise.
