package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameOwnerResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameOwnerIntegrationTests {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    private static final String VALID_NAME = "Bob";
    private static final String VALID_EMAIL = "bob@gmail.com";
    private static final String VALID_PASSWORD = "bobthebuilder123"; 

    private UserAccount testUser;
    private int testGameOwnerId;

    @AfterAll
    public void cleanup(){
        gameOwnerRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateValidGameOwner(){
        //Arrange
        testUser= new UserAccount(VALID_NAME,VALID_EMAIL,VALID_PASSWORD);
        userAccountRepository.save(testUser);
        int testUserId= testUser.getId();
        String url = String.format("/gameowners/%d", testUserId);
        //Act
        ResponseEntity<GameOwnerResponseDto> response = client.exchange(
            url, 
            HttpMethod.PUT,
            null, 
            GameOwnerResponseDto.class);
        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        GameOwnerResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testUserId, responseBody.getId());
        testGameOwnerId= responseBody.getId();
        assertTrue(responseBody.getIsGameOwner());
        assertEquals(testUser.getEmail(), responseBody.getUserEmail());
        assertEquals(testUserId, responseBody.getUserId());
        assertEquals(testUser.getName(), responseBody.getUserName());         

    }
    @Test
    @Order(1)
    public void testCreateGameOwnerWithUnknownUser(){
        //Arrange
        String url = String.format("/gameowners/%d", -1);
        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            url, 
            HttpMethod.PUT,
            null, 
        ErrorDto.class);
        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
            List.of(String.format("Could not create gameOwner since user with id %d does not exist",-1)), 
            response.getBody().getErrors());


    }
    @Test
    @Order(2)
    public void testCreateDuplicateGameOwnerForSameUser(){
        //Arrange
        int testUserId = testUser.getId();
        String url = String.format("/gameowners/%d", testUserId);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            url, 
            HttpMethod.PUT,
            null, 
            ErrorDto.class);
        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
            List.of(String.format("The user with id %d already has a game owner with the same id created",testUserId)) 
            ,response.getBody().getErrors());

    }
    @Test
    @Order(3)
    public void testFindValidGameOwner(){
        //Arrange
        String url = String.format("/gameowners/%d",testGameOwnerId );
        //Act
        ResponseEntity<GameOwnerResponseDto> response = client.getForEntity(
            url,
            GameOwnerResponseDto.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getId(), response.getBody().getId());
        assertEquals(testUser.getId(), response.getBody().getUserId());
        assertEquals(testUser.getEmail(), response.getBody().getUserEmail());
        assertEquals(testUser.getName(), response.getBody().getUserName());
        assertTrue(response.getBody().getIsGameOwner());

    }
    @Test
    @Order(4)
    public void testFindValidGameOwnerForPlayer(){
        //Arrange
        GameOwner gameOwner = gameOwnerRepository.findGameOwnerById(testGameOwnerId);
        gameOwner.setUser(null);
        gameOwnerRepository.save(gameOwner);
        String url = String.format("/gameowners/%d",testGameOwnerId );
        //Act
        ResponseEntity<GameOwnerResponseDto> response = client.getForEntity(
            url,
            GameOwnerResponseDto.class);
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getId(), response.getBody().getId());
        assertEquals(-1, response.getBody().getUserId());
        assertEquals("", response.getBody().getUserEmail());
        assertEquals("", response.getBody().getUserName());
        assertFalse(response.getBody().getIsGameOwner());          

    }
    @Test
    @Order(5)
    public void testFindGameOwnerThatDoesntExist(){
        //Arrange
        String url = String.format("/gameowners/%d",500);
        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity(
            url,
        ErrorDto.class);
        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
            List.of(String.format("Could not find gameOwner with id %d",500)) 
            ,response.getBody().getErrors());
    } 

}
