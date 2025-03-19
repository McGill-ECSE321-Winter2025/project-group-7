package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.*;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.*;
import ca.mcgill.ecse321.boardgamesharingsystem.model.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class RequestAnswerIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    @Autowired
    private RequestAnswerRepository requestAnswerRepo;

    @Autowired
    private UserAccountRepository userRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameOwnerRepository gameOwnerRepo;

    private int borrowRequestId;

    @AfterAll
    public void cleanup() {
        requestAnswerRepo.deleteAll();
        borrowRequestRepo.deleteAll();
        gameCopyRepo.deleteAll();
        gameOwnerRepo.deleteAll();
        gameRepo.deleteAll();
        userRepo.deleteAll();
    }

    private BorrowRequestResponseDto createTestBorrowRequest() {
        UserAccount borrower = userRepo.save(new UserAccount("Daniel", "daniel@gmail.com", "12345"));
        UserAccount owner = userRepo.save(new UserAccount("Alice", "alice@gmail.com", "10086"));
        Game game = gameRepo.save(new Game("Chess", 2, 4, "chess.com", "classic game"));
        GameOwner gameOwner = gameOwnerRepo.save(new GameOwner(owner));
        GameCopy gameCopy = gameCopyRepo.save(new GameCopy(game, gameOwner));

        BorrowRequest borrowRequest = borrowRequestRepo.save(new BorrowRequest(
                LocalDate.parse("2025-02-01"),
                LocalDate.parse("2025-02-10"), borrower, gameCopy));
        borrowRequestId = borrowRequest.getId();

        return new BorrowRequestResponseDto(borrowRequest);
    }


    @Test
    @Order(1)
    public void testCreateValidRequestAnswer() {
        // Arrange
        BorrowRequestResponseDto borrowRequest = createTestBorrowRequest();

        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequest.getId(), LocalDate.parse("2025-02-04"), Time.valueOf("13:45:00"),
                "Library", "daniel@gmail.com"
        );

        // Act
        ResponseEntity<RequestAnswerResponseDto> response = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequest.getId(),
                requestAnswerDto,
                RequestAnswerResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        RequestAnswerResponseDto requestAnswer = response.getBody();
        assertEquals(borrowRequest.getId(), requestAnswer.getRequestId());
        assertEquals(Time.valueOf("13:45:00"), requestAnswer.getDropOffTime());
        assertEquals(LocalDate.parse("2025-02-04"), requestAnswer.getDropOffDate());
        assertEquals("Library", requestAnswer.getLocation());
        assertEquals("daniel@gmail.com", requestAnswer.getContactEmail());
    }



    @Test
    @Order(2)
    public void testCreateRequestAnswerWithInvalidBorrowRequest() {
        // Arrange - Create a RequestAnswerDto with a non-existent borrow request ID
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, 9999, LocalDate.parse("2025-02-04"), Time.valueOf("13:45:00"),
                "Library", "daniel@gmail.com"
        );

        // Act - Call the API with an invalid borrowing request ID
        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/requestAnswer?borrowingRequestId=9999",
                requestAnswerDto,
                ErrorDto.class
        );

        // Assert - Ensure the response status is NOT_FOUND (404)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Borrowing request not found."));
    }


    @Test
    @Order(3)
    public void testCreateRequestAnswerWithMissingFields() {
        // Arrange - Create a RequestAnswerDto with missing fields
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequestId, null, null, "", ""
        );

        // Act - Call the API with missing fields
        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequestId,
                requestAnswerDto,
                ErrorDto.class
        );

        // Assert - Ensure the response status is BAD_REQUEST (400)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors().contains("Drop-off date, time, location, and contact email must be valid."));
    }


    @Test
    @Order(4)
    public void testUpdateValidRequestAnswer() {
        // Arrange - Create a valid borrow request first
        BorrowRequestResponseDto borrowRequest = createTestBorrowRequest();

        // Arrange - Create a valid request answer first
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequest.getId(), LocalDate.parse("2025-02-04"),
                Time.valueOf("13:45:00"), "Library", "daniel@gmail.com"
        );

        ResponseEntity<RequestAnswerResponseDto> createResponse = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequest.getId(),
                requestAnswerDto,
                RequestAnswerResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        // Extract the created requestAnswerId for updating
        int requestAnswerId = createResponse.getBody().getId();

        // Arrange - Prepare an updated RequestAnswerDto
        RequestAnswerDto updatedDto = new RequestAnswerDto(
                requestAnswerId, borrowRequest.getId(), LocalDate.parse("2025-02-06"),
                Time.valueOf("14:30:00"), "Cafe", "daniel@gmail.com"
        );

        // Act - Send PUT request to update request answer
        ResponseEntity<RequestAnswerResponseDto> updateResponse = client.exchange(
                "/requestAnswer/" + requestAnswerId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedDto),
                RequestAnswerResponseDto.class
        );

        // Assert - Ensure the response status is OK (200)
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());

        // Verify the update was successful
        assertEquals("Cafe", updateResponse.getBody().getLocation());
        assertEquals(LocalDate.parse("2025-02-06"), updateResponse.getBody().getDropOffDate());
        assertEquals(Time.valueOf("14:30:00"), updateResponse.getBody().getDropOffTime());
        assertEquals("daniel@gmail.com", updateResponse.getBody().getContactEmail());
    }

    @Test
    @Order(5)
    public void testUpdateNonExistentRequestAnswer() {
        // Arrange - Create an updated RequestAnswerDto for a non-existent requestAnswerId
        RequestAnswerDto updatedDto = new RequestAnswerDto(
                9999, borrowRequestId, LocalDate.parse("2025-02-06"),
                Time.valueOf("14:30:00"), "Cafe", "daniel@gmail.com"
        );

        // Act - Try updating a non-existent request answer
        ResponseEntity<ErrorDto> response = client.exchange(
                "/requestAnswer/9999",
                HttpMethod.PUT,
                new HttpEntity<>(updatedDto),
                ErrorDto.class
        );

        // Assert - Expect NOT FOUND (404) error
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Request answer not found."));
    }

    @Test
    @Order(6)
    public void testUpdateRequestAnswerWithInvalidValues() {
        // Arrange - Create a new valid request answer first
        BorrowRequestResponseDto borrowRequest = createTestBorrowRequest();

        RequestAnswerDto validRequestAnswerDto = new RequestAnswerDto(
                0, borrowRequest.getId(), LocalDate.parse("2025-02-04"),
                Time.valueOf("13:45:00"), "Library", "daniel@gmail.com"
        );

        ResponseEntity<RequestAnswerResponseDto> createResponse = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequest.getId(),
                validRequestAnswerDto,
                RequestAnswerResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        int createdRequestAnswerId = createResponse.getBody().getId(); // Retrieve new ID

        // Act - Attempt to update with invalid values
        RequestAnswerDto invalidDto = new RequestAnswerDto(
                createdRequestAnswerId, borrowRequest.getId(), null, null, "", ""
        );

        ResponseEntity<ErrorDto> response = client.exchange(
                "/requestAnswer/" + createdRequestAnswerId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidDto),
                ErrorDto.class
        );

        // Assert - Expect BAD REQUEST (400) error
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Drop-off date, time, and location must be valid."));
    }

    @Test
    @Order(7)
    public void testDeleteRequestAnswer() {
        // Arrange - First create a request answer to delete
        BorrowRequestResponseDto borrowRequest = createTestBorrowRequest();

        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequest.getId(), LocalDate.parse("2025-02-04"),
                Time.valueOf("13:45:00"), "Library", "daniel@gmail.com"
        );

        ResponseEntity<RequestAnswerResponseDto> createResponse = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequest.getId(),
                requestAnswerDto,
                RequestAnswerResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        int createdRequestAnswerId = createResponse.getBody().getId(); // Retrieve the new ID

        // Act - Delete the request answer
        ResponseEntity<Void> response = client.exchange(
                "/requestAnswer/" + createdRequestAnswerId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert deletion was successful
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Act - Try deleting again to confirm it was removed
        ResponseEntity<ErrorDto> secondDeleteResponse = client.exchange(
                "/requestAnswer/" + createdRequestAnswerId,
                HttpMethod.DELETE,
                null,
                ErrorDto.class
        );

        // Assert - The request answer should no longer exist
        assertEquals(HttpStatus.NOT_FOUND, secondDeleteResponse.getStatusCode());
        assertNotNull(secondDeleteResponse.getBody());
        assertTrue(secondDeleteResponse.getBody().getErrors().contains("Request answer not found."));
    }


    @Test
    @Order(8)
    public void testDeleteNonExistentRequestAnswer() {
        // Act - Attempt to delete a non-existent request answer
        ResponseEntity<ErrorDto> response = client.exchange(
                "/requestAnswer/9999",
                HttpMethod.DELETE,
                null,
                ErrorDto.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Request answer not found."));
    }


    @Test
    @Order(9)
    public void testFindRequestAnswerForValidBorrowingRequest() {
        // Arrange - Create a borrow request first
        BorrowRequestResponseDto borrowRequest = createTestBorrowRequest();

        // Ensure borrow request was created successfully
        assertNotNull(borrowRequest);
        assertTrue(borrowRequest.getId() > 0, "Borrow Request ID should be greater than 0");

        // ✅ Manually update the request status to Accepted
        borrowRequestRepo.findById(borrowRequest.getId()).ifPresent(request -> {
            request.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
            borrowRequestRepo.save(request);
        });

        // ✅ Create a request answer DTO
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequest.getId(), LocalDate.parse("2025-02-04"),
                Time.valueOf("13:45:00"), "Library", "alice@example.com"
        );

        // Act - Send a request to create the RequestAnswer
        ResponseEntity<RequestAnswerResponseDto> createResponse = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequest.getId(),
                requestAnswerDto,
                RequestAnswerResponseDto.class
        );

        // Assert - Ensure creation was successful
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        int createdRequestAnswerId = createResponse.getBody().getId();

        // Act - Retrieve the request answer
        ResponseEntity<RequestAnswerResponseDto> response = client.getForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequest.getId(),
                RequestAnswerResponseDto.class
        );

        // Assert - Ensure retrieval was successful and data is correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdRequestAnswerId, response.getBody().getId());
        assertEquals("Library", response.getBody().getLocation());
        assertEquals(LocalDate.parse("2025-02-04"), response.getBody().getDropOffDate());
        assertEquals(Time.valueOf("13:45:00"), response.getBody().getDropOffTime());
        assertEquals("alice@example.com", response.getBody().getContactEmail());
    }


    @Test
    @Order(10)
    public void testFindRequestAnswerForNonExistentBorrowingRequest() {
        // Act - Try retrieving a request answer for a non-existent borrowing request
        ResponseEntity<ErrorDto> response = client.getForEntity(
                "/requestAnswer?borrowingRequestId=9999",
                ErrorDto.class
        );

        // Assert - Expect NOT FOUND (404) response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Borrowing request not found."));
    }

    @Test
    @Order(11)
    public void testFindRequestAnswerWhenNoneExistsForValidBorrowingRequest() {
        // Arrange - Create a new borrow request
        BorrowRequestResponseDto newBorrowRequest = createTestBorrowRequest();

        // Mark new borrow request as Accepted
        ResponseEntity<BorrowRequestResponseDto> acceptResponse = client.exchange(
                "/borrowrequests/" + newBorrowRequest.getId() + "/accept",
                HttpMethod.PUT, null, BorrowRequestResponseDto.class
        );

        assertEquals(HttpStatus.OK, acceptResponse.getStatusCode());

        // Act - Try retrieving a request answer when none exists
        ResponseEntity<ErrorDto> response = client.getForEntity(
                "/requestAnswer?borrowingRequestId=" + newBorrowRequest.getId(),
                ErrorDto.class
        );

        // Assert - Expect NOT FOUND (404) because no request answer exists
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("No request answer found for this borrowing request."));
    }

}
