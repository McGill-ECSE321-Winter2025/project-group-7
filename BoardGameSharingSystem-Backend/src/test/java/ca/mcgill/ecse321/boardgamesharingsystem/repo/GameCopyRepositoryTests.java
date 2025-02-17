package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class GameCopyRepositoryTests {

    @Autowired
    private GameCopyRepository gameCopyRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    public void clearDatabase() {
        gameCopyRepository.deleteAll();
        gameOwnerRepository.deleteAll();
        gameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testSaveAndRetrieveGameCopy() {
        UserAccount currentUser = new UserAccount("bojack", "bojack@gmail.com", "password");
        currentUser = userAccountRepository.save(currentUser);
        
        GameOwner gameOwner = new GameOwner(currentUser);
        gameOwner = gameOwnerRepository.save(gameOwner);

        Game game = new Game("Catan", 3, 6, "catan.jpg", "Strategy game");
        game = gameRepository.save(game);

        GameCopy gameCopy = new GameCopy(game, gameOwner);
        gameCopy = gameCopyRepository.save(gameCopy);

        GameCopy retrievedGameCopy = gameCopyRepository.findGameCopyById(gameCopy.getId());

        assertNotNull(retrievedGameCopy);
        assertEquals(gameCopy.getId(), retrievedGameCopy.getId());
        assertEquals(gameOwner.getId(), retrievedGameCopy.getGameOwner().getId());
        assertEquals(game.getId(), retrievedGameCopy.getGame().getId());
    }
}
