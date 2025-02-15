package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;

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
        userAccountRepository.deleteAll();
        gameCopyRepository.deleteAll();
    }

    @Test
    public void testCreateBorrowRequest() {
        //Arrange
        Date startdate = Date.valueOf("2025-02-14");
        Date endDate = Date.valueOf("2025-02-20");

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
}
