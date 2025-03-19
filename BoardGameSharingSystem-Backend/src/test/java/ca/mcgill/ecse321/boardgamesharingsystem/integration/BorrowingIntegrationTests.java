package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.BorrowRequestResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.*;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class BorrowingIntegrationTests {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private GameOwnerRepository gameOwnerRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameCopyRepository gameCopyRepository;
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    private static final LocalDate START_DATE = LocalDate.parse("2025-01-01");
    private static final LocalDate END_DATE = LocalDate.parse("2025-01-10");
    private int borrowRequestId;
    private int gameCopyId;

    @AfterAll
    public void cleanup() {
        borrowRequestRepository.deleteAll();
        gameCopyRepository.deleteAll();
        gameRepository.deleteAll();
        gameOwnerRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    private BorrowRequestResponseDto createTestBorrowRequest() {
        UserAccount borrower = new UserAccount("yeonjun", "txt@bighit.com", "password");
        borrower = userAccountRepository.save(borrower);

        UserAccount owner = new UserAccount("soobin", "txt@bighit.com", "password");
        owner = userAccountRepository.save(owner);

        GameOwner gameOwner = new GameOwner(owner);
        gameOwner = gameOwnerRepository.save(gameOwner);

        Game game = new Game("Splendor", 2, 4, "splendor.com", "Nice game");
        game = gameRepository.save(game);

        GameCopy gameCopy = new GameCopy(game, gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);
        gameCopyId = gameCopy.getId();

        BorrowRequest borrowRequest = new BorrowRequest(START_DATE, END_DATE, borrower, gameCopy);
        borrowRequest = borrowRequestRepository.save(borrowRequest);
        borrowRequestId = borrowRequest.getId();
        
        return new BorrowRequestResponseDto(borrowRequest);
    }

    @Test
    @Order(1)
    public void testCreateBorrowingRequest_Success() {
        //Arrange
        BorrowRequestResponseDto requestDto = createTestBorrowRequest();
    
        //Act
        ResponseEntity<BorrowRequestResponseDto> response = client.postForEntity(
            "/borrowrequests?gameCopyId=" + gameCopyId + "&borrowerId=" + requestDto.getBorrowerId(),
            requestDto, BorrowRequestResponseDto.class);
        
        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(requestDto.getStartDate(), response.getBody().getStartDate());
        assertEquals(requestDto.getEndDate(), response.getBody().getEndDate());
        assertEquals(requestDto.getBorrowerId(), response.getBody().getBorrowerId());
        assertEquals(requestDto.getBorrowerId(), response.getBody().getBorrowerId());
        assertEquals(BorrowRequest.RequestStatus.Pending, response.getBody().getRequestStatus());
    }

    @Test
    @Order(2)
    public void testCreateBorrowingRequest_InvalidDates() {
        //Arrange
        BorrowRequestResponseDto requestDto = createTestBorrowRequest();
        requestDto.setStartDate(null);

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity(
            "/borrowrequests?gameCopyId=" + gameCopyId + "&borrowerId=" + requestDto.getBorrowerId(),
            requestDto, ErrorDto.class);
        
        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertTrue((response.getBody().getErrors().contains("Dates cannot be null.")));
    }

    @Test
    @Order(3)
    public void testFindPendingBorrowingRequests_Success() {
        // Act
        ResponseEntity<BorrowRequestResponseDto[]> response = client.exchange(
            "/borrowrequests/" + gameCopyId + "/pending",
            HttpMethod.GET, null, BorrowRequestResponseDto[].class
        );
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        BorrowRequestResponseDto[] borrowRequests = response.getBody();
        assertNotNull(borrowRequests);
        assertTrue(borrowRequests.length == 1);

        List<BorrowRequestResponseDto> borrowRequestList = Arrays.asList(borrowRequests);
        assertNotNull(borrowRequestList);
        
        borrowRequestList.forEach(request -> assertEquals(BorrowRequest.RequestStatus.Pending, request.getRequestStatus()));

        List<Integer> requestIdList = new ArrayList<>();
        borrowRequestList.forEach(request -> requestIdList.add(request.getId()));
    }
    

    @Test
    @Order(4)
    public void testFindAcceptedBorrowingRequests_NotFound() {
        //Arrange
        int nonExistentGameCopyId = 999;
        String url = String.format("/borrowrequests/%d/accepted", nonExistentGameCopyId);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            url, HttpMethod.GET, null, ErrorDto.class
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertIterableEquals(
            List.of(String.format("Game copy not found.", nonExistentGameCopyId)),
            response.getBody().getErrors()
        );
    }
    
    @Test
    @Order(5)
    public void testAcceptPendingBorrowingRequest_NotFound() {
        //Arrange
        int nonExistentBorrowingId = 999;
        String url = String.format("/borrowrequests/%d/accept",nonExistentBorrowingId);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.PUT, null, ErrorDto.class);
        
        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertIterableEquals(
            List.of(String.format("Borrowing request not found.", nonExistentBorrowingId)),
            response.getBody().getErrors()
        );
    }

    @Test
    @Order(6)
    public void testAcceptPendingBorrowingRequest_Success() {
        //Arrange
        String url = "/borrowrequests/" + borrowRequestId + "/accept";

        //Act
        ResponseEntity<BorrowRequestResponseDto> response = client.exchange(url, HttpMethod.PUT, null, BorrowRequestResponseDto.class);
        
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(BorrowRequest.RequestStatus.Accepted, response.getBody().getRequestStatus());
    }

    @Test
    @Order(7)
    public void testFindPendingBorrowingRequests_NotFound() {
        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity(
            "/borrowrequests/999/pending", ErrorDto.class);
        
        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertTrue((response.getBody().getErrors().contains("Game copy not found.")));
    }
    
    
    @Test
    @Order(8)
    public void testFindAcceptedBorrowingRequests_Success() {
        //Arrange
        LocalDate startDate = LocalDate.parse("2025-01-01");
        LocalDate endDate = LocalDate.parse("2025-01-10");

        UserAccount borrower = new UserAccount("yeonjun", "txt@bighit.com", "password");
        borrower = userAccountRepository.save(borrower);

        UserAccount owner = new UserAccount("soobin", "txt@bighit.com", "password");
        owner = userAccountRepository.save(owner);

        GameOwner gameOwner = new GameOwner(owner);
        gameOwner = gameOwnerRepository.save(gameOwner);

        Game game = new Game("Splendor", 2, 4, "splendor.com", "Nice game");
        game = gameRepository.save(game);

        GameCopy gameCopy = new GameCopy(game, gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);

        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, borrower, gameCopy);
        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted); 
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        String url = String.format("/borrowrequests/%d/accepted", gameCopy.getId());

        //Act
        ResponseEntity<BorrowRequestResponseDto[]> response = client.exchange(
            url, HttpMethod.GET, null, BorrowRequestResponseDto[].class
        );

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(borrowRequest.getId(), response.getBody()[0].getId());
        assertEquals(BorrowRequest.RequestStatus.Accepted, response.getBody()[0].getRequestStatus());
    }       

        @Test
        @Order(9)
        public void testDeclinePendingBorrowingRequest_Success() {
            //Arrange
            LocalDate startDate = LocalDate.parse("2025-01-01");
            LocalDate endDate = LocalDate.parse("2025-01-10");

            UserAccount borrower = new UserAccount("yeonjun", "txt@bighit.com", "password");
            borrower = userAccountRepository.save(borrower);

            UserAccount owner = new UserAccount("soobin", "txt@bighit.com", "password");
            owner = userAccountRepository.save(owner);

            GameOwner gameOwner = new GameOwner(owner);
            gameOwner = gameOwnerRepository.save(gameOwner);

            Game game = new Game("Splendor", 2, 4, "splendor.com", "Nice game");
            game = gameRepository.save(game);

            GameCopy gameCopy = new GameCopy(game, gameOwner);
            gameCopy = gameCopyRepository.save(gameCopy);

            BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, borrower, gameCopy);
            borrowRequest = borrowRequestRepository.save(borrowRequest);

            String url = "/borrowrequests/" + borrowRequest.getId();

            //Act
            ResponseEntity<BorrowRequestResponseDto> response = client.exchange(url, HttpMethod.DELETE, null, BorrowRequestResponseDto.class);
            
            //Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response);
            assertNotNull(response.getBody());
        }

        @Test
        @Order(10)
        public void testDeclinePendingBorrowingRequest_NotFound() {
            //Arrange
            String url = "/borrowrequests/999";

            //Act
            ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);
            
            //Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
            assertTrue((response.getBody().getErrors().contains("Borrowing request not found.")));
        }
}
