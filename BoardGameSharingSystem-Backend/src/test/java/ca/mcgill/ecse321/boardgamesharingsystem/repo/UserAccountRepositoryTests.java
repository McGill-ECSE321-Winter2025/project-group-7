package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

@SpringBootTest
public class UserAccountRepositoryTests {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    public void clearDatabase(){
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadUserAccount() {
        //Arrange
        UserAccount mila = new UserAccount("mila","mila@gmail.com","1234");
        mila = userAccountRepository.save(mila);

        //Act
        UserAccount milaFromDb = userAccountRepository.findUserAccountById(mila.getId());

        //Assert
        assertNotNull(milaFromDb);
        assertEquals(mila.getId(), milaFromDb.getId());
        assertEquals(mila.getName(), milaFromDb.getName());
        assertEquals(mila.getEmail(), milaFromDb.getEmail());
        assertEquals(mila.getPassword(), milaFromDb.getPassword());

    }
}
