package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

@SpringBootTest
public class GameOwnerRepositoryTests {
    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    public void clearDatabase(){
        gameOwnerRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadGameOwner()
    {
        //Arrange
        String name = "bojack";
        String email = "bojack@gmail.com";
        String password = "password";
        UserAccount currentUser = new UserAccount(name, email, password);
        userAccountRepository.save(currentUser);

        GameOwner gameOwner = new GameOwner(currentUser);
        gameOwnerRepository.save(gameOwner);

        //Act
        GameOwner gameOwnerFromDb = gameOwnerRepository.findGameOwnerById(gameOwner.getId());

        //Assert
        assertNotNull(gameOwnerFromDb);
        assertEquals(gameOwner.getId(), gameOwnerFromDb.getId());
        assertNotNull(gameOwnerFromDb.getUser());
        assertEquals(gameOwnerFromDb.getUser().getId(), currentUser.getId());
    }
}
