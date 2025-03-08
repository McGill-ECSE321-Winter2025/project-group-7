package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;

@SpringBootTest
public class RequestAnswerRepositoryTests {
    @Autowired
    private RequestAnswerRepository requestAnswerRepository;

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameCopyRepository gameCopyRepository;
    
    @Autowired
    private GameOwnerRepository gameOwnerRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    
    @AfterEach
    public void clearDatabase() {
        requestAnswerRepository.deleteAll();
        borrowRequestRepository.deleteAll();  
        gameCopyRepository.deleteAll();
        gameOwnerRepository.deleteAll();        
        gameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadRequestAnswer(){
        //Arrange
        UserAccount borrower = new UserAccount("Hredhi", "hredhi@gmail.com", "1234");
        borrower = userAccountRepository.save(borrower);

        Game game = new Game("Overcooked", 1, 4, "pic.com", "fun");
        game = gameRepository.save(game);

        UserAccount user = new UserAccount("Mila", "mila@gmail.com", "4321");
        user = userAccountRepository.save(user);
        GameOwner gameOwner = new GameOwner(user);
        gameOwner = gameOwnerRepository.save(gameOwner);

        GameCopy gameCopy = new GameCopy(game, gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);
        Date startDateBorrow = Date.valueOf("2025-12-14");
        Date endDateBorrow = Date.valueOf("2026-01-14");
        BorrowRequest borrowRequest = new BorrowRequest(startDateBorrow, endDateBorrow, borrower, gameCopy);
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        Date dropOffDate = Date.valueOf("2026-02-14");
        Time dropOffTime = Time.valueOf("10:10:02");
        RequestAnswer requestAns = new RequestAnswer(dropOffDate, dropOffTime, "Montreal", borrowRequest, "snigdha@gmail.com");
        requestAns = requestAnswerRepository.save(requestAns);
        
        //Act
        RequestAnswer requestAnsFromDb = requestAnswerRepository.findRequestAnswerById(requestAns.getId());

        //Assert
        assertNotNull(requestAnsFromDb);
        assertEquals(requestAns.getId(), requestAnsFromDb.getId());
        assertEquals(requestAns.getRequest().getId(), requestAnsFromDb.getRequest().getId());
        assertEquals(requestAns.getContactEmail(), requestAnsFromDb.getContactEmail());
        assertEquals(requestAns.getDropOffDate(), requestAnsFromDb.getDropOffDate());
        assertEquals(requestAns.getDropOffTime(), requestAnsFromDb.getDropOffTime());
        assertEquals(requestAns.getLocation(), requestAnsFromDb.getLocation());
    }

    @Test
    public void testReadRequestAnswerbyRequestId(){
        //Arrange
        UserAccount borrower = new UserAccount("Hredhi", "hredhi@gmail.com", "1234");
        borrower = userAccountRepository.save(borrower);

        Game game = new Game("Overcooked", 1, 4, "pic.com", "fun");
        game = gameRepository.save(game);

        UserAccount user = new UserAccount("Mila", "mila@gmail.com", "4321");
        user = userAccountRepository.save(user);
        GameOwner gameOwner = new GameOwner(user);
        gameOwner = gameOwnerRepository.save(gameOwner);

        GameCopy gameCopy = new GameCopy(game, gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);
        Date startDateBorrow = Date.valueOf("2025-12-14");
        Date endDateBorrow = Date.valueOf("2026-01-14");
        BorrowRequest borrowRequest = new BorrowRequest(startDateBorrow, endDateBorrow, borrower, gameCopy);
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        Date dropOffDate = Date.valueOf("2026-02-14");
        Time dropOffTime = Time.valueOf("10:10:02");
        RequestAnswer requestAns = new RequestAnswer(dropOffDate, dropOffTime, "Montreal", borrowRequest, "snigdha@gmail.com");
        requestAns = requestAnswerRepository.save(requestAns);
        
        //Act
        RequestAnswer requestAnsFromDb = requestAnswerRepository.findRequestAnswerByRequestId(borrowRequest.getId());

        //Assert
        assertNotNull(requestAnsFromDb);
        assertEquals(requestAns.getId(), requestAnsFromDb.getId());
        assertEquals(requestAns.getRequest().getId(), requestAnsFromDb.getRequest().getId());
        assertEquals(requestAns.getContactEmail(), requestAnsFromDb.getContactEmail());
        assertEquals(requestAns.getDropOffDate(), requestAnsFromDb.getDropOffDate());
        assertEquals(requestAns.getDropOffTime(), requestAnsFromDb.getDropOffTime());
        assertEquals(requestAns.getLocation(), requestAnsFromDb.getLocation());
        
    }
}
