package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

@SpringBootTest
public class BorrowRequestRepositoryTests {
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private GameCopyRepository gameCopyRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    @AfterEach
    private void clearDatabase() {
        borrowRequestRepository.deleteAll();
        gameCopyRepository.deleteAll();
        gameOwnerRepository.deleteAll();
        gameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadBorrowRequest() {
        //Arrange
        LocalDate startdate = LocalDate.parse("2025-02-14");
        LocalDate endDate = LocalDate.parse("2025-02-20");

        UserAccount borrower = new UserAccount("yeonjun", "yeonjun@bighit.com", "txt0304");
        UserAccount owner = new UserAccount("soobin", "soobin@bighit.com", "1299");
        borrower = userAccountRepository.save(borrower);
        owner = userAccountRepository.save(owner);

        Game chess = new Game("chess", 2, 2, "chess.com","the chess game consists..." );
        GameOwner gameOwner = new GameOwner(owner);
        GameCopy gameCopy = new GameCopy(chess, gameOwner);
        chess = gameRepository.save(chess);
        gameOwner = gameOwnerRepository.save(gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);

        BorrowRequest borrowRequest = new BorrowRequest(startdate, endDate, borrower, gameCopy);
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        //Act
        BorrowRequest borrowRequestFromDb = borrowRequestRepository.findBorrowRequestById(borrowRequest.getId());

        //Assert
        assertNotNull(borrowRequestFromDb);
        assertEquals(borrowRequest.getId(), borrowRequestFromDb.getId());
        assertEquals(borrowRequest.getStartDate(), borrowRequestFromDb.getStartDate());
        assertEquals(borrowRequest.getEndDate(), borrowRequestFromDb.getEndDate());
        assertNotNull(borrowRequestFromDb.getBorrower());
        assertNotNull(borrowRequestFromDb.getGameCopy());
        assertEquals(borrowRequestFromDb.getBorrower().getId(), borrower.getId());
        assertEquals(borrowRequestFromDb.getGameCopy().getId(), gameCopy.getId());
        assertEquals(BorrowRequest.RequestStatus.Pending, borrowRequestFromDb.getRequestStatus());

        borrowRequestFromDb.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        borrowRequestFromDb = borrowRequestRepository.save(borrowRequestFromDb);

        BorrowRequest updatedBorrowRequest = borrowRequestRepository.findBorrowRequestById(borrowRequest.getId());
        assertEquals(BorrowRequest.RequestStatus.Accepted, updatedBorrowRequest.getRequestStatus());
        }


    @Test
    public void testFindByBorrowerId() {
        //Arrange
        LocalDate startDate = LocalDate.parse("2025-03-01");
        LocalDate endDate = LocalDate.parse("2025-03-10");

        UserAccount borrower1 = new UserAccount("alice", "alice@example.com", "password123");
        UserAccount borrower2 = new UserAccount("bob", "bob@example.com", "password123");
        borrower1 = userAccountRepository.save(borrower1);
        borrower2 = userAccountRepository.save(borrower2);


        Game chess = new Game("chess", 2, 2, "chess.com", "description of chess");
        GameOwner gameOwner = new GameOwner(borrower2);
        chess = gameRepository.save(chess);
        gameOwner = gameOwnerRepository.save(gameOwner);
        GameCopy gameCopy = new GameCopy(chess, gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);

        BorrowRequest request1 = new BorrowRequest(startDate, endDate, borrower1, gameCopy);
        BorrowRequest request2 = new BorrowRequest(startDate, endDate, borrower1, gameCopy);
        BorrowRequest request3 = new BorrowRequest(startDate, endDate, borrower2, gameCopy);
        borrowRequestRepository.save(request1);
        borrowRequestRepository.save(request2);
        borrowRequestRepository.save(request3);

        //Act
        List<BorrowRequest> borrower1RequestsFromDb = borrowRequestRepository.findByBorrowerId(borrower1.getId());

        //Assert
        assertNotNull(borrower1RequestsFromDb);
        assertEquals(2, borrower1RequestsFromDb.size());
        final UserAccount finalBorrower = borrower1;
        borrower1RequestsFromDb.forEach(req ->
            assertEquals(finalBorrower.getId(), req.getBorrower().getId())
        );
    }

    @Test
    public void testFindByGameCopyId() {
        //Arrange
        LocalDate startDate = LocalDate.parse("2025-04-01");
        LocalDate endDate = LocalDate.parse("2025-04-10");

        UserAccount borrower = new UserAccount("charlie", "charlie@example.com", "password123");
        borrower = userAccountRepository.save(borrower);

        Game game1 = new Game("game1", 2, 4, "game1.com", "description for game1");
        GameOwner owner1 = new GameOwner(borrower);
        game1 = gameRepository.save(game1);
        owner1 = gameOwnerRepository.save(owner1);
        GameCopy gameCopy1 = new GameCopy(game1, owner1);
        gameCopy1 = gameCopyRepository.save(gameCopy1);

        Game game2 = new Game("game2", 2, 4, "game2.com", "description for game2");
        GameOwner owner2 = new GameOwner(borrower);
        game2 = gameRepository.save(game2);
        owner2 = gameOwnerRepository.save(owner2);
        GameCopy gameCopy2 = new GameCopy(game2, owner2);
        gameCopy2 = gameCopyRepository.save(gameCopy2);

        BorrowRequest request1 = new BorrowRequest(startDate, endDate, borrower, gameCopy1);
        BorrowRequest request2 = new BorrowRequest(startDate, endDate, borrower, gameCopy1);
        BorrowRequest request3 = new BorrowRequest(startDate, endDate, borrower, gameCopy2);
        borrowRequestRepository.save(request1);
        borrowRequestRepository.save(request2);
        borrowRequestRepository.save(request3);

        //Act
        List<BorrowRequest> gameCopy1RequestsFromDb = borrowRequestRepository.findByGameCopyId(gameCopy1.getId());

        //Assert
        assertNotNull(gameCopy1RequestsFromDb);
        assertEquals(2, gameCopy1RequestsFromDb.size());
        final GameCopy finalGameCopy = gameCopy1;
        gameCopy1RequestsFromDb.forEach(req ->
            assertEquals(finalGameCopy.getId(), req.getGameCopy().getId())
        );
    }
}
