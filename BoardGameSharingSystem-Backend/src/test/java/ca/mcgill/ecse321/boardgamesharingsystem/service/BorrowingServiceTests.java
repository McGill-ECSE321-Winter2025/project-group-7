package ca.mcgill.ecse321.boardgamesharingsystem.service;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest.RequestStatus;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RequestAnswerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
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

    @InjectMocks
    private BorrowingService borrowingService;

    private static final Date START_DATE = Date.valueOf("2025-01-01");
    private static final Date END_DATE = Date.valueOf("2025-01-10");
    private static final Date DROP_OFF_DATE = Date.valueOf("2025-01-02");
    private static final Time DROP_OFF_TIME = Time.valueOf("12:00:00");

    private static final String CONTACT_EMAIL = "yeonjun@txt.com";
    private static final String LOCATION = "Library";

    /**
     * Tests that a valid borrowing request is created correctly.
     */
    @Test
    public void testCreateBorrowingRequest() {
        // Arrange
        UserAccount borrower = new UserAccount("John", "john@test.com", "password");
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");
        // 模型要求 GameCopy 使用构造方法(Game, GameOwner)
        Game game = new Game("Chess", 2, 2, "chess.com", "Chess game description");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);

        when(userAccountRepository.findById(1)).thenReturn(Optional.of(borrower));
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(gameCopy));
        when(borrowRequestRepository.save(any(BorrowRequest.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        BorrowRequest request = borrowingService.createBorrowingRequest(2, 1, START_DATE, END_DATE);

        // Assert
        assertNotNull(request);
        assertEquals(START_DATE, request.getStartDate());
        assertEquals(END_DATE, request.getEndDate());
        assertEquals(borrower, request.getBorrower());
        assertEquals(gameCopy, request.getGameCopy());
        assertEquals(RequestStatus.Pending, request.getRequestStatus());
    }

    /**
     * Tests that creating a borrowing request with null dates throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestWithNullDates() {
        // Arrange
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(new UserAccount("John", "john@test.com", "password")));
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> borrowingService.createBorrowingRequest(2, 1, null, END_DATE));
        assertEquals("Dates cannot be null.", ex.getMessage());
    }

    /**
     * Tests that creating a borrowing request with end date not after start date throws an exception.
     */
    @Test
    public void testCreateBorrowingRequestWithInvalidDates() {
        // Arrange
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(new UserAccount("John", "john@test.com", "password")));
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"), new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> borrowingService.createBorrowingRequest(2, 1, END_DATE, START_DATE));
        assertEquals("End date must be after start date.", ex.getMessage());
    }

    /**
     * Tests that findPendingBorrowingRequests returns pending requests.
     */
    @Test
    public void testFindPendingBorrowingRequests() {
        // Arrange
        // 创建 Game, GameOwner, GameCopy
        Game game = new Game("Chess", 2, 2, "chess.com", "Chess game description");
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(gameCopy));

        List<BorrowRequest> requests = new ArrayList<>();
        BorrowRequest pendingRequest = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("Alice", "alice@test.com", "alicePass"), gameCopy);
        // Status is automatically set to Pending in constructor
        requests.add(pendingRequest);
        when(borrowRequestRepository.findByGameCopyId(2)).thenReturn(requests);

        // Act
        List<BorrowRequest> result = borrowingService.findPendingBorrowingRequests(2);

        // Assert
        assertEquals(1, result.size());
        assertEquals(RequestStatus.Pending, result.get(0).getRequestStatus());
    }

    /**
     * Tests that findAcceptedBorrowingRequests returns accepted requests.
     */
    @Test
    public void testFindAcceptedBorrowingRequests() {
        // Arrange
        // 初始化 GameCopy 同上
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

        // Act
        List<BorrowRequest> result = borrowingService.findAcceptedBorrowingRequests(2);

        // Assert
        assertEquals(1, result.size());
        assertEquals(RequestStatus.Accepted, result.get(0).getRequestStatus());
    }

    /**
     * Tests that a pending borrowing request is accepted correctly.
     */
    @Test
    public void testAcceptBorrowingRequest() {
        // Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        when(borrowRequestRepository.save(any(BorrowRequest.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        BorrowRequest result = borrowingService.acceptPendingBorrowingRequest(1);

        // Assert
        assertEquals(RequestStatus.Accepted, result.getRequestStatus());
    }

    /**
     * Tests that a pending borrowing request is declined correctly.
     */
    @Test
    public void testDeclinePendingBorrowingRequest() {
        // Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        // Ensure request is Pending by default
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));

        // Act
        BorrowRequest declined = borrowingService.declinePendingBorrowingRequest(1);

        // Assert
        assertNotNull(declined);
        verify(borrowRequestRepository, times(1)).delete(request);
    }

    /**
     * Tests that creating a request answer works as expected.
     */
    @Test
    public void testCreateRequestAnswer() {
        // Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        when(requestAnswerRepository.save(any(RequestAnswer.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        RequestAnswer answer = borrowingService.createRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, LOCATION, CONTACT_EMAIL);

        // Assert
        assertNotNull(answer);
        assertEquals(DROP_OFF_DATE, answer.getDropOffDate());
        assertEquals(DROP_OFF_TIME, answer.getDropOffTime());
        assertEquals(LOCATION, answer.getLocation());
        assertEquals(CONTACT_EMAIL, answer.getContactEmail());
        assertEquals(request, answer.getRequest());
    }

    /**
     * Tests that updating a request answer works correctly.
     */
    @Test
    public void testUpdateRequestAnswer() {
        // Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION,
                new BorrowRequest(START_DATE, END_DATE,
                        new UserAccount("John", "john@test.com", "password"),
                        new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                                new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))), CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer));
        when(requestAnswerRepository.save(any(RequestAnswer.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        RequestAnswer updated = borrowingService.updateRequestAnswer(1, DROP_OFF_DATE, DROP_OFF_TIME, LOCATION);

        // Assert
        assertEquals(DROP_OFF_DATE, updated.getDropOffDate());
        assertEquals(DROP_OFF_TIME, updated.getDropOffTime());
        assertEquals(LOCATION, updated.getLocation());
    }

    /**
     * Tests that deleting a request answer works correctly.
     */
    @Test
    public void testDeleteRequestAnswer() {
        // Arrange
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION,
                new BorrowRequest(START_DATE, END_DATE,
                        new UserAccount("John", "john@test.com", "password"),
                        new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                                new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass")))), CONTACT_EMAIL);
        when(requestAnswerRepository.findById(1)).thenReturn(Optional.of(answer));

        // Act
        borrowingService.deleteRequestAnswer(1);

        // Assert
        verify(requestAnswerRepository, times(1)).delete(answer);
    }

    /**
     * Tests that finding a request answer for an accepted borrowing request works correctly.
     */
    @Test
    public void testFindRequestAnswerForAcceptedBorrowingRequest() {
        // Arrange
        BorrowRequest request = new BorrowRequest(START_DATE, END_DATE,
                new UserAccount("John", "john@test.com", "password"),
                new GameCopy(new Game("Chess", 2, 2, "chess.com", "desc"),
                        new GameOwner(new UserAccount("Owner", "owner@test.com", "ownerPass"))));
        request.setRequestStatus(RequestStatus.Accepted);
        when(borrowRequestRepository.findById(1)).thenReturn(Optional.of(request));
        RequestAnswer answer = new RequestAnswer(DROP_OFF_DATE, DROP_OFF_TIME, LOCATION, request, CONTACT_EMAIL);
        when(requestAnswerRepository.findRequestAnswerByRequestId(1)).thenReturn(answer);

        // Act
        RequestAnswer foundAnswer = borrowingService.findRequestAnswerForBorrowingRequest(1);

        // Assert
        assertNotNull(foundAnswer);
    }


    /**
     * Tests that finding pending borrowing requests returns only those with status Pending.
     */
    @Test
    public void testFindPendingBorrowingRequestsWhenEmpty() {
        // Arrange
        Game game = new Game("Chess", 2, 2, "chess.com", "desc");
        UserAccount owner = new UserAccount("Owner", "owner@test.com", "ownerPass");
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        when(gameCopyRepository.findById(2)).thenReturn(Optional.of(gameCopy));
        List<BorrowRequest> requests = new ArrayList<>();
        // 模拟没有待处理的请求
        when(borrowRequestRepository.findByGameCopyId(2)).thenReturn(requests);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> borrowingService.findPendingBorrowingRequests(2));
        assertEquals("No pending borrowing requests found for this game copy.", ex.getMessage());
    }
}
