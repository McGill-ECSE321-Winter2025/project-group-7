package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.AuthRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameOwnerResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.UserAccountResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class UserIntegrationTests {
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

    private static final String VALID_NAME = "Mila";
    private static final String VALID_EMAIL = "mila@gmail.com";
    private static final String VALID_PASSWORD = "bunnies123"; 
    private int userId;

    private UserAccount user;
    
    @BeforeEach
    public void setup() {
         
    }

    @AfterEach
    public void cleanup(){
        gameCopyRepository.deleteAll();
        gameOwnerRepository.deleteAll();     
        gameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateValidUserAccount() 
    {
        //Arrange
        AuthRequest userRequest = new AuthRequest();
        userRequest.setUsername(VALID_NAME);
        userRequest.setEmail(VALID_EMAIL);
        userRequest.setPassword(VALID_PASSWORD);

        //Act
        ResponseEntity<UserAccountResponseDto> response = client.postForEntity(
            "/users",
            userRequest,
            UserAccountResponseDto.class
            );

        //Assert
        assertNotNull(response);
        userId = response.getBody().getId();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        UserAccountResponseDto createdUserDto = response.getBody();
        assertEquals(VALID_NAME, createdUserDto.getName());
        assertEquals(VALID_EMAIL, createdUserDto.getEmail());
        assertEquals(VALID_PASSWORD, createdUserDto.getPassword());
    }

    @Test
    @Order(1)
    public void testCreateDuplicateUsername() 
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user); 

        AuthRequest userRequest = new AuthRequest();
        userRequest.setUsername(VALID_NAME);
        userRequest.setEmail(VALID_EMAIL);
        userRequest.setPassword(VALID_PASSWORD);

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity(
            "/users",
            userRequest,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertTrue((response.getBody().getErrors().contains(String.format("Username %s is already taken", user.getName()))));
    }


    @Test
    @Order(2)
    public void testGetUserAccountByValidId()
    {
        //Arrange 
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user); 
        userId = user.getId();
        String url = String.format("/users/%d", this.userId);

        //Act
        ResponseEntity<UserAccountResponseDto> response = client.getForEntity(
            url, 
            UserAccountResponseDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserAccountResponseDto foundUserDto = response.getBody();
        assertEquals(this.userId, foundUserDto.getId());
        assertEquals(VALID_NAME, foundUserDto.getName());
        assertEquals(VALID_EMAIL, foundUserDto.getEmail());
        assertEquals(VALID_PASSWORD, foundUserDto.getPassword());
    }

    @Test
    @Order(3)
    public void testGetInvalidUserAccount() 
    {
        //Arrange
        String url = String.format("/users/%d", this.userId);

        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity(
            url,
            ErrorDto.class
            );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertTrue((response.getBody().getErrors().contains(String.format("No userAccount found with id %d", userId))));
    }

    @Test
    @Order(4)
    public void testDeleteValidUserAccount() 
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();
        String url = String.format("/users/%d", this.userId);

        //Act
        ResponseEntity<UserAccountResponseDto> response = client.exchange(
            url, 
            HttpMethod.DELETE,
            null,
            UserAccountResponseDto.class
            );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserAccountResponseDto deletedUserDto = response.getBody();
        assertEquals(this.userId, deletedUserDto.getId());
        assertEquals(VALID_NAME, deletedUserDto.getName());
        assertEquals(VALID_EMAIL, deletedUserDto.getEmail());
        assertEquals(VALID_PASSWORD, deletedUserDto.getPassword());
    }

    @Test
    @Order(5)
    public void testDeleteInvalidUserAccount() 
    {
        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/users/" + "-1",
            HttpMethod.DELETE,
            null,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains("No userAccount found with id -1"));
    }

    @Test
    @Order(6)
    public void testValidLogin()
    {
        //Arrange 
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername(VALID_NAME);
        loginRequest.setEmail(VALID_EMAIL);
        loginRequest.setPassword(VALID_PASSWORD);

        //Act
        ResponseEntity<UserAccountResponseDto> response = client.postForEntity(
            "/users/login",
            loginRequest,
            UserAccountResponseDto.class
            );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserAccountResponseDto loggedInUserDto = response.getBody();
        assertEquals(user.getId(), loggedInUserDto.getId());
        assertEquals(VALID_NAME, loggedInUserDto.getName());
        assertEquals(VALID_EMAIL, loggedInUserDto.getEmail());
        assertEquals(VALID_PASSWORD, loggedInUserDto.getPassword());
    }

    @Test
    @Order(7)
    public void testLoginWithIncorrectPassword()
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername(VALID_NAME);
        loginRequest.setEmail(VALID_EMAIL);
        loginRequest.setPassword("beavers123");

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity(
            "/users/login",
            loginRequest,
            ErrorDto.class
            );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertEquals("Incorrect Password", error.getErrors().getFirst());
    }

    @Test
    @Order(8)
    public void testValidToggleToPlayer() 
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        GameOwner gameOwner = new GameOwner(user);
        gameOwnerRepository.save(gameOwner);

        String url = String.format("/users/%d/toPlayer", this.userId);

        //Act
        ResponseEntity<UserAccountResponseDto> response = client.exchange(
            url, 
            HttpMethod.PUT,
            null,
            UserAccountResponseDto.class
            );
            
        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserAccountResponseDto toggledUserDto = response.getBody();
        assertEquals(VALID_NAME, toggledUserDto.getName());
        assertEquals(VALID_EMAIL, toggledUserDto.getEmail());
        assertEquals(VALID_PASSWORD, toggledUserDto.getPassword());
        assertEquals(userId, toggledUserDto.getId());
        GameOwner toggledGameOwner = gameOwnerRepository.findGameOwnerById(userId);
        assertNull(toggledGameOwner.getUser());
    }

    @Test
    @Order(10)
    public void testToggleAlreadyPlayerToPlayer()
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        GameOwner gameOwner = new GameOwner(user);

        gameOwner.setUser(null);
        gameOwnerRepository.save(gameOwner);

        String url = String.format("/users/%d/toPlayer", userId);
        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            url,
            HttpMethod.PUT,
            null,
            ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("User with id %d is already a player", userId)));
    }

    @Test
    @Order(11)
    public void testValidToggleToGameOwner() 
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        GameOwner gameOwner = new GameOwner(user);

        gameOwner.setUser(null);
        gameOwnerRepository.save(gameOwner);

        Game chess = new Game("chess",2,2,"Example/url","its chess");
        gameRepository.save(chess);
        GameCopy chessCopy = new GameCopy(chess,gameOwner);
        gameCopyRepository.save(chessCopy);

        String url = String.format("/users/%d/toGameOwner", userId);

        //Act
        ResponseEntity<GameOwnerResponseDto> response = client.exchange(
            url, 
            HttpMethod.PUT,
            null,
            GameOwnerResponseDto.class
            );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameOwnerResponseDto toggledGameOwnerDto = response.getBody();
        assertEquals(gameOwner.getId(), toggledGameOwnerDto.getId());
        assertEquals(VALID_NAME, toggledGameOwnerDto.getUserName());
        assertEquals(VALID_EMAIL, toggledGameOwnerDto.getUserEmail());
        assertEquals(gameOwner.getId(), toggledGameOwnerDto.getId());
        assertTrue(toggledGameOwnerDto.getIsGameOwner());
    }

    @Test
    @Order(12)
    public void testToggleToGameOwnerWithNoAssociatedGames()
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        GameOwner gameOwner = new GameOwner(user);

        gameOwner.setUser(null);
        gameOwnerRepository.save(gameOwner);

        String url = String.format("/users/%d/toGameOwner", userId);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            url,
            HttpMethod.PUT,
            null,
            ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertEquals("gameOwner has no associated games", error.getErrors().getFirst());
    }

    @Test
    @Order(13)
    public void testToggleAlreadyGameOwnerToGameOwner() 
    {
        //Arrange
        user = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepository.save(user);
        userId = user.getId();

        GameOwner gameOwner = new GameOwner(user);

        gameOwner.setUser(user);
        gameOwnerRepository.save(gameOwner);

        Game chess = new Game("chess",2,2,"Example/url","its chess");
        gameRepository.save(chess);
        GameCopy chessCopy = new GameCopy(chess,gameOwner);
        gameCopyRepository.save(chessCopy);

        String url = String.format("/users/%d/toGameOwner", userId);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            url,
            HttpMethod.PUT,
            null,
            ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("GameOwner already associated with userAccount with id %d",
            userId)));
    }
}
