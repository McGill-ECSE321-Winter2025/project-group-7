package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.*;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.*;
import ca.mcgill.ecse321.boardgamesharingsystem.model.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
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
    private int requestAnswerId;

    private static final Date dropOffDate = Date.valueOf("2025-01-03");
    private static final Time dropOffTime = Time.valueOf("15:30:00");
    private static final String dropOffLocation = "Library";
    private static final String contactEmail = "alice@example.com";

    @BeforeAll
    public void setup() {
        UserAccount user = userRepo.save(new UserAccount("Alice", "alice@example.com", "password123"));

        Game game = gameRepo.save(new Game("Chess", 2, 4, "http://example.com/chess.jpg", "A classic strategy game"));

        GameOwner gameOwner = gameOwnerRepo.save(new GameOwner(user));

        GameCopy gameCopy = gameCopyRepo.save(new GameCopy(game, gameOwner));

        BorrowRequest borrowRequest = new BorrowRequest(
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                user,
                gameCopy);
        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        borrowRequestRepo.save(borrowRequest);

        borrowRequestId = borrowRequest.getId();
    }

    @AfterAll
    public void cleanup() {
        requestAnswerRepo.deleteAll();
        borrowRequestRepo.deleteAll();
        gameCopyRepo.deleteAll();
        gameOwnerRepo.deleteAll();
        gameRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidRequestAnswer() {
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequestId, dropOffDate, dropOffTime, dropOffLocation, contactEmail);

        ResponseEntity<RequestAnswerResponseDto> response = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequestId,
                requestAnswerDto,
                RequestAnswerResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        requestAnswerId = response.getBody().getId();
    }

    @Test
    @Order(2)
    public void testCreateRequestAnswerWithInvalidBorrowRequest() {
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, 9999, dropOffDate, dropOffTime, dropOffLocation, contactEmail);

        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/requestAnswer?borrowingRequestId=9999",
                requestAnswerDto,
                ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(3)
    public void testCreateRequestAnswerWithMissingFields() {
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequestId, null, null, "", "");

        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequestId,
                requestAnswerDto,
                ErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void testUpdateValidRequestAnswer() {
        RequestAnswerDto updatedDto = new RequestAnswerDto(
                requestAnswerId, borrowRequestId, dropOffDate, dropOffTime, "Cafe", contactEmail);

        ResponseEntity<RequestAnswerResponseDto> response = client.exchange(
                "/requestAnswer/" + requestAnswerId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedDto),
                RequestAnswerResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cafe", response.getBody().getLocation());
    }

    @Test
    @Order(5)
    public void testUpdateNonExistentRequestAnswer() {
        RequestAnswerDto updatedDto = new RequestAnswerDto(
                9999, borrowRequestId, dropOffDate, dropOffTime, "Cafe", contactEmail);

        ResponseEntity<ErrorDto> response = client.exchange(
                "/requestAnswer/9999",
                HttpMethod.PUT,
                new HttpEntity<>(updatedDto),
                ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void testUpdateRequestAnswerWithInvalidValues() {
        RequestAnswerDto invalidDto = new RequestAnswerDto(
                requestAnswerId, borrowRequestId, null, null, "", "");

        ResponseEntity<ErrorDto> response = client.exchange(
                "/requestAnswer/" + requestAnswerId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidDto),
                ErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(7)
    public void testDeleteRequestAnswer() {
        ResponseEntity<Void> response = client.exchange(
                "/requestAnswer/" + requestAnswerId,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(8)
    public void testDeleteNonExistentRequestAnswer() {
        ResponseEntity<ErrorDto> response = client.exchange(
                "/requestAnswer/9999",
                HttpMethod.DELETE,
                null,
                ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(9)
    public void testFindRequestAnswerForValidBorrowingRequest() {
        // create a valid request answer
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto(
                0, borrowRequestId, new Date(System.currentTimeMillis()), new Time(System.currentTimeMillis()), "Library", "alice@example.com");

        ResponseEntity<RequestAnswerResponseDto> createResponse = client.postForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequestId,
                requestAnswerDto,
                RequestAnswerResponseDto.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        int createdRequestAnswerId = createResponse.getBody().getId();

        // retrieving
        ResponseEntity<RequestAnswerResponseDto> response = client.getForEntity(
                "/requestAnswer?borrowingRequestId=" + borrowRequestId,
                RequestAnswerResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdRequestAnswerId, response.getBody().getId());
    }


    @Test
    @Order(10)
    public void testFindRequestAnswerForNonExistentBorrowingRequest() {
        ResponseEntity<ErrorDto> response = client.getForEntity(
                "/requestAnswer?borrowingRequestId=9999",
                ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(11)
    public void testFindRequestAnswerWhenNoneExistsForValidBorrowingRequest() {
        // create a new borrowing request
        BorrowRequest newBorrowRequest = borrowRequestRepo.save(new BorrowRequest(
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                userRepo.save(new UserAccount("Bob", "bob@example.com", "pass123")),
                gameCopyRepo.findAll().iterator().next()
        ));

        // mark new borrow request as Accepted
        newBorrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        borrowRequestRepo.save(newBorrowRequest);

        // retrieve a request answer which should not exist
        ResponseEntity<ErrorDto> response = client.getForEntity(
                "/requestAnswer?borrowingRequestId=" + newBorrowRequest.getId(),
                ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
