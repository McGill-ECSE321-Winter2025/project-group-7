package ca.mcgill.ecse321.boardgamesharingsystem.service;

import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest.RequestStatus;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RequestAnswerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class BorrowingServiceTests {

    @Mock
    private BorrowRequestRepository borrowRequestRepository;

    @Mock
    private RequestAnswerRepository requestAnswerRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private GameCopyRepository gameCopyRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameOwnerRepository gameOwnerRepository;

    @InjectMocks
    private BorrowingService borrowingService;

    private static final LocalDate START_DATE = LocalDate.parse("2025-01-01");
    private static final LocalDate END_DATE = LocalDate.parse("2025-01-10");
    private static final LocalDate DROP_OFF_DATE = LocalDate.parse("2025-01-02");
    private static final Time DROP_OFF_TIME = Time.valueOf("12:00:00");

    private static final String CONTACT_EMAIL = "yeonjun@txt.com";
    private static final String LOCATION = "Library";

    /**
     * Tests that a valid borrowing request is created correctly.
     */
    @Test
    public void testCreateBorrowingRequest() {
        //Arrange
        UserAccount borrower = new UserAccount("John", "john@test.com", "password");
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");

        Game game = new Game("Chess", 2, 2, "chess.com", "Chess game description");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);

        when(userAccountRepository.findById(1)).thenReturn(Optional.of(borrower));
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(gameCopy));
        when(borrowRequestRepository.save(any(BorrowRequest.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        //Act
        BorrowRequest request = borrowingService.createBorrowingRequest(2, 1, START_DATE, END_DATE);

        //Assert
        assertNotNull(request);
        assertEquals(START_DATE, request.getStartDate());
        assertEquals(END_DATE, request.getEndDate());
        assertEquals(borrower, request.getBorrower());
        assertEquals(gameCopy, request.getGameCopy());
        assertEquals(RequestStatus.Pending, request.getRequestStatus());
    }

    @Test
    public void testFindAllRequests() {
        //Arrange
        UserAccount borrower1 = new UserAccount("John", "john@test.com", "password");
        UserAccount borrower2 = new UserAccount("Lisa", "lisa@test.com", "123456");
    
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");
        GameOwner gameOwner = new GameOwner(owner);
    
        Game game1 = new Game("Chess", 2, 2, "chess.com", "Classic chess game");
        Game game2 = new Game("Catan", 3, 4, "catan.com", "Famous board game");
    
        GameCopy gameCopy1 = new GameCopy(game1, gameOwner);
        GameCopy gameCopy2 = new GameCopy(game2, gameOwner);
    
        LocalDate startDate1 = LocalDate.of(2025, 4, 1);
        LocalDate endDate1 = LocalDate.of(2025, 4, 10);
    
        LocalDate startDate2 = LocalDate.of(2025, 5, 1);
        LocalDate endDate2 = LocalDate.of(2025, 5, 5);
    
        BorrowRequest request1 = new BorrowRequest(startDate1, endDate1, borrower1, gameCopy1);
        BorrowRequest request2 = new BorrowRequest(startDate2, endDate2, borrower2, gameCopy2);
    
        List<BorrowRequest> mockList = List.of(request1, request2);
    
        when(borrowRequestRepository.findAll()).thenReturn(mockList);
    
        //Act
        List<BorrowRequest> result = borrowingService.findAllRequests();
    
        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    
        assertEquals("John", result.get(0).getBorrower().getName());
        assertEquals("Chess", result.get(0).getGameCopy().getGame().getTitle());
    
        assertEquals("Lisa", result.get(1).getBorrower().getName());
        assertEquals("Catan", result.get(1).getGameCopy().getGame().getTitle());
    }
    

    /**
     * Tests that creating a borrowing request with null dates throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestWithNullDates() {
        //Arrange
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(new UserAccount("John", "john@test.com", "password")));
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))));
        
        //Act & Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.createBorrowingRequest(2, 1, null, END_DATE));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Dates cannot be null.", ex.getMessage());
    }

    /**
     * Tests that creating a borrowing request with end date not after start date throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestWithInvalidDates() {
        //Arrange
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(new UserAccount("John", "john@test.com", "password")));
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))));
        
        //Act & Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.createBorrowingRequest(2, 1, END_DATE, START_DATE));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("End date must be after start date.", ex.getMessage());
    }

    /**
     * Tests that creating a borrowing request with non-existed GameCopy throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestGameCopyNotFound() {
        //Arrange
        when(gameCopyRepository.findById(1)).thenReturn(Optional.empty());

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.createBorrowingRequest(1, 2, START_DATE, END_DATE));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game copy not found.", exception.getMessage());
    }
    
    /**
     * Tests that creating a borrowing request with non-existed Borrower throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestBorrowerNotFound() {
        //Arrange
        Game game = new Game("splendor", 2, 4, "splendor.com", "description");
        UserAccount owner = new UserAccount("mary", "difumer@gmail.com", "hehe");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(gameCopy));
        when(userAccountRepository.findById(2)).thenReturn(Optional.empty());

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.createBorrowingRequest(1, 2, START_DATE, END_DATE));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Borrower not found.", exception.getMessage());
    }

    /**
     * Tests that creating a borrowing request with a null end date throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestEndDateNull() {
        //Arrange
        Game game = new Game("Chess", 2, 2, "chess.com", "desc");
        GameCopy gameCopy = new GameCopy(game, new GameOwner(new UserAccount("owner", "owner@email.com", "pass")));
        UserAccount borrower = new UserAccount("borrower", "borrower@email.com", "pass");
        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(gameCopy));
        when(userAccountRepository.findById(2)).thenReturn(Optional.of(borrower));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.createBorrowingRequest(1, 2, START_DATE, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Dates cannot be null.", exception.getMessage());
    }

    /**
     * Tests that creating a borrowing request with a null start date throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestStartDateNull() {
        //Arrange
        Game game = new Game("Chess", 2, 2, "chess.com", "desc");
        GameCopy gameCopy = new GameCopy(game, new GameOwner(new UserAccount("owner", "owner@email.com", "pass")));
        UserAccount borrower = new UserAccount("borrower", "borrower@email.com", "pass");
        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(gameCopy));
        when(userAccountRepository.findById(2)).thenReturn(Optional.of(borrower));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.createBorrowingRequest(1, 2, null, END_DATE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Dates cannot be null.", exception.getMessage());
    }

    /**
     * Tests that findPendingBorrowingRequests returns pending requests.
     */
    @Test
    public void testFindPendingBorrowingRequests() {
        //Arrange
        Game game = new Game("Chess", 2, 2, "chess.com", "Chess game description");
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(gameCopy));

        List<BorrowRequest> requests = new ArrayList<>();
        BorrowRequest pendingRequest = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("Alice", "alice@test.com", "alicePass"), gameCopy);
        requests.add(pendingRequest);
        when(borrowRequestRepository.findByGameCopyId(2)).thenReturn(requests);

        //Act
        List<BorrowRequest> result = borrowingService.findPendingBorrowingRequests(2);

        //Assert
        assertEquals(1, result.size());
        assertEquals(RequestStatus.Pending, result.get(0).getRequestStatus());
    }

    /**
     * Tests that findPendingBorrowingRequests with non-existed GameCopy throws an exception.
     */
    @Test
    public void testFindPendingBorrowingRequestsGameCopyNotFound() {
        //Arrange
        when(gameCopyRepository.findById(1)).thenReturn(Optional.empty());
        
        //Act & Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.findPendingBorrowingRequests(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Game copy not found.", ex.getMessage());
     }

     /**
     * Tests that findPendingBorrowingRequests with no pending requests found throws an exception.
     */
    @Test
    public void testFindPendingBorrowingRequestsNoPendingRequests() {
        //Arrange
        Game game = new Game("splendor", 2, 4, "splendor.com", "description");
        UserAccount owner = new UserAccount("mary", "difumer@gmail.com", "hehe");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(gameCopy));

        BorrowRequest acceptedRequest = new BorrowRequest(START_DATE, END_DATE, owner, gameCopy);
        acceptedRequest.setRequestStatus(RequestStatus.Accepted);
        List<BorrowRequest> requests = List.of(acceptedRequest);
        when(borrowRequestRepository.findByGameCopyId(1)).thenReturn(requests);

        //Act + Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.findPendingBorrowingRequests(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("No pending borrowing requests found for this game copy.", ex.getMessage());
    }

    /**
     * Tests that findAcceptedBorrowingRequests returns accepted requests.
     */
    @Test
    public void testFindAcceptedBorrowingRequests() {
        //Arrange
        Game game = new Game("Chess", 2, 2, "chess.com", "Chess game description");
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(gameCopy));

        List<BorrowRequest> requests = new ArrayList<>();
        BorrowRequest acceptedRequest = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("Bob", "bob@test.com", "bobPass"), gameCopy);
        acceptedRequest.setRequestStatus(RequestStatus.Accepted);
        requests.add(acceptedRequest);
        when(borrowRequestRepository.findByGameCopyId(2)).thenReturn(requests);

        //Act
        List<BorrowRequest> result = borrowingService.findAcceptedBorrowingRequests(2);

        //Assert
        assertEquals(1, result.size());
        assertEquals(RequestStatus.Accepted, result.get(0).getRequestStatus());
    }

    /**
     * Tests that findAcceptedBorrowingRequests with non-existed GameCopy throws an exception.
     */
    @Test
    public void testFindAcceptedBorrowingRequestsGameCopyNotFound() {
        //Arrange
        when(gameCopyRepository.findById(1)).thenReturn(Optional.empty());

        //Act & Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.findAcceptedBorrowingRequests(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Game copy not found.", ex.getMessage());
    }

    /**
     * Tests that findAcceptedBorrowingRequests with accepted requests found throws an exception.
     */
    @Test
    public void testFindAcceptedBorrowingRequestsNoAcceptedRequests() {
        //Arrange
        Game game = new Game("splendor", 2, 4, "splendor.com", "description");
        UserAccount owner = new UserAccount("mary", "difumer@gmail.com", "hehe");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(gameCopy));
        
        BorrowRequest pendingRequest = new BorrowRequest(START_DATE, END_DATE, owner, gameCopy);
        pendingRequest.setRequestStatus(RequestStatus.Pending);
        List<BorrowRequest> requests = List.of(pendingRequest);
        when(borrowRequestRepository.findByGameCopyId(1)).thenReturn(requests);

        //Act + Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.findAcceptedBorrowingRequests(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("No accepted borrowing requests found for this game copy.", ex.getMessage());
    }

    /**
     * Tests that a pending borrowing request is accepted correctly.
     */
    @Test
    public void testAcceptBorrowingRequest() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        when(borrowRequestRepository.save(any(BorrowRequest.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        //Act
        BorrowRequest result = borrowingService.acceptPendingBorrowingRequest(1);

        //Assert
        assertEquals(RequestStatus.Accepted, result.getRequestStatus());
    }

    /**
     * Tests that no borrowing request is found.
     */
    @Test
    public void testAcceptPendingBorrowingRequestNotFound() {
        //Arrange
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.empty());
        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.acceptPendingBorrowingRequest(1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Borrowing request not found.", exception.getMessage());
    }

    /**
     * Tests that a pending borrowing request is declined correctly.
     */
    @Test
    public void testDeclinePendingBorrowingRequest() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act
        BorrowRequest declined = borrowingService.declinePendingBorrowingRequest(1);

        //Assert
        assertNotNull(declined);
        verify(borrowRequestRepository, times(1)).delete(request);
    }

    @Test
    public void testDeclinePendingBorrowingRequestNotFound() {
        //Arrange
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.empty());

        //Act + Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.declinePendingBorrowingRequest(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("Borrowing request not found.", ex.getMessage());
    }

    @Test
    public void testDeclinePendingBorrowingRequestNotPending() {
        //Arrange
        Game game = new Game("splendor", 2, 4, "splendor.com", "description");
        UserAccount owner = new UserAccount("mary", "difumer@gmail.com", "hehe");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, owner, gameCopy);
        request.setRequestStatus(RequestStatus.Accepted);
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act + Assert
        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.declinePendingBorrowingRequest(1));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Cannot decline a non-pending borrowing request.", ex.getMessage());
    }

    /**
     * Tests that creating a request answer works as expected.
     */
    @Test
    public void testCreateRequestAnswer() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        when(requestAnswerRepository.save(any(RequestAnswer.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        //Act
        RequestAnswer answer = borrowingService.createRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, LOCATION, CONTACT_EMAIL);

        //Assert
        assertNotNull(answer);
        assertEquals(DROP_OFF_DATE, answer.getDropOffDate());
        assertEquals(DROP_OFF_TIME, answer.getDropOffTime());
        assertEquals(LOCATION, answer.getLocation());
        assertEquals(CONTACT_EMAIL, answer.getContactEmail());
        assertEquals(request, answer.getRequest());
    }

    @Test
    public void testCreateRequestAnswerDropOffDateNull() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, new UserAccount("borrower", "borrower@email.com", "pass"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("owner", "owner@email.com", "pass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.createRequestAnswer(1, null, DROP_OFF_TIME, LOCATION, CONTACT_EMAIL));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, location, and contact email must be valid.", exception.getMessage());
    }

    @Test
    public void testCreateRequestAnswerDropOffTimeNull() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, new UserAccount("borrower", "borrower@email.com", "pass"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("owner", "owner@email.com", "pass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.createRequestAnswer(1, DROP_OFF_DATE, null, LOCATION, CONTACT_EMAIL));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, location, and contact email must be valid.", exception.getMessage());
    }

    @Test
    public void testCreateRequestAnswerDropOffLocationBlank() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, new UserAccount("borrower", "borrower@email.com", "pass"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("owner", "owner@email.com", "pass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.createRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, "   ", CONTACT_EMAIL));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, location, and contact email must be valid.", exception.getMessage());
    }

    @Test
    public void testCreateRequestAnswerContactEmailBlank() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, new UserAccount("borrower", "borrower@email.com", "pass"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("owner", "owner@email.com", "pass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.createRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, LOCATION, " "));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, location, and contact email must be valid.", exception.getMessage());
    }

    @Test
    public void testCreateRequestAnswerBorrowingRequestNotFound() {
        //Arrange
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.empty());
        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.createRequestAnswer(1, START_DATE, Time.valueOf("12:00:00"), "Library", "test@email.com"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Borrowing request not found.", exception.getMessage());
    }

    /**
     * Tests that updating a request answer works correctly.
     */
    @Test
    public void testUpdateRequestAnswer() {
        //Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION,
                new BorrowRequest(START_DATE, END_DATE,
                        new UserAccount("John", "john@test.com", "password"),
                        new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                                new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))), CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer));
        when(requestAnswerRepository.save(any(RequestAnswer.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        //Act
        RequestAnswer updated = borrowingService.updateRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, LOCATION);

        //Assert
        assertEquals(DROP_OFF_DATE, updated.getDropOffDate());
        assertEquals(DROP_OFF_TIME, updated.getDropOffTime());
        assertEquals(LOCATION, updated.getLocation());
    }

    @Test
    public void testUpdateRequestAnswerNotFound() {
        //Arrange
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.empty());
        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> borrowingService.updateRequestAnswer(1, START_DATE, Time.valueOf("12:00:00"), "Library"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Request answer not found.", exception.getMessage());
    }

    @Test
    public void testUpdateRequestAnswerDropOffDateNull() {
        //Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION,
                new BorrowRequest(START_DATE, END_DATE, new UserAccount("borrower", "borrower@email.com", "pass"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("owner", "owner@email.com", "pass")))),
                CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.updateRequestAnswer(1, null, DROP_OFF_TIME, LOCATION));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, and location must be valid.", exception.getMessage());
    }

    @Test
    public void testUpdateRequestAnswerDropOffTimeNull() {
        //Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION, 
                new BorrowRequest(START_DATE, END_DATE, 
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))), 
                CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer)); 

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.updateRequestAnswer(1, DROP_OFF_DATE, null, LOCATION));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, and location must be valid.", exception.getMessage());
    }

    @Test
    public void testUpdateRequestAnswerDropOffLocationBlank() {
        //Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION,
                new BorrowRequest(START_DATE, END_DATE, new UserAccount("borrower", "borrower@email.com", "pass"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("owner", "owner@email.com", "pass")))),
                CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer));

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.updateRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, " "));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Drop-off date, time, and location must be valid.", exception.getMessage());
    }

    /**
     * Tests that deleting a request answer works correctly.
     */
    @Test
    public void testDeleteRequestAnswer() {
        //Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION,
                new BorrowRequest(START_DATE, END_DATE,
                        new UserAccount("John", "john@test.com", "password"),
                        new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                                new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))), CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer));

        //Act
        borrowingService.deleteRequestAnswer(1);

        //Assert
        verify(requestAnswerRepository, times(1)).delete(answer);
    }

    @Test
    public void testDeleteRequestAnswerNotFound() {
        //Arrange
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.empty());
    
        //Act & Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
            () -> borrowingService.deleteRequestAnswer(1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Request answer not found.", exception.getMessage());
    }

    /**
     * Tests that finding a request answer for an accepted borrowing request works correctly.
     */
    @Test
    public void testFindRequestAnswerForAcceptedBorrowingRequest() {
        //Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        request.setRequestStatus(RequestStatus.Accepted);
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION, request, CONTACT_EMAIL);
        when(requestAnswerRepository.findRequestAnswerByRequestId(1)).thenReturn(answer);

        //Act
        RequestAnswer foundAnswer = borrowingService.findRequestAnswerForBorrowingRequest(1);

        //Assert
        assertNotNull(foundAnswer);
    }

    @Test
    public void testFindRequestAnswerForBorrowingRequestNotFound() {
        //Arrange
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.empty());

        //Act & Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.findRequestAnswerForBorrowingRequest(1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Borrowing request not found.", exception.getMessage());
    }

    @Test
    public void testFindRequestAnswerForBorrowingRequestNotAccepted() {
        //Arrange
        Game game = new Game("splendor", 2, 4, "splendor.com", "description");
        UserAccount owner = new UserAccount("mary", "difumer@gmail.com", "hehe");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, owner, gameCopy);
        request.setRequestStatus(RequestStatus.Pending);
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        //Act & Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
                () -> borrowingService.findRequestAnswerForBorrowingRequest(1));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot find request answer for a non-accepted borrowing request.", exception.getMessage());
    }

    @Test
    public void testFindRequestAnswerForBorrowingRequestAnswerNotFound() {
        //Arrange
        Game game = new Game("splendor", 2, 4, "splendor.com", "description");
        UserAccount owner = new UserAccount("mary", "difumer@gmail.com", "hehe");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE, owner, gameCopy);
        request.setRequestStatus(RequestStatus.Accepted);
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        when(requestAnswerRepository.findRequestAnswerByRequestId(1)).thenReturn(null);

        //Act & Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, 
        () -> borrowingService.findRequestAnswerForBorrowingRequest(1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No request answer found for this borrowing request.", exception.getMessage());
    }
}
