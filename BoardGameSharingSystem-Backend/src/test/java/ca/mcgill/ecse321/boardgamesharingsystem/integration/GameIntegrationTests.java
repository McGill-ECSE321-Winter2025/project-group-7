package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameRequestDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameResponseDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String VALID_TITLE = "Scrabble";
    private static final int VALID_MIN_NUM_PLAYERS = 2;
    private static final int VALID_MAX_NUM_PLAYERS = 4;
    private static final String VALID_PICTURE_URL = "https://toysrus.com/Scrabble.jpg";
    private static final String VALID_DESCRIPTION = "Scrabble is a fun and easy word game for people of all ages to enjoy!";
    private int testGameId;
    @Test
    @Order(1)
    public void testCreateValidGame(){
        //Arrange
        GameRequestDto request = new GameRequestDto(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);

        //Act
        ResponseEntity<GameResponseDto> response = restTemplate.postForEntity("/games", request, GameResponseDto.class);
    
        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        GameResponseDto createdGame = response.getBody();
        assertNotNull(createdGame);
        assertEquals(VALID_TITLE, createdGame.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, createdGame.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, createdGame.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, createdGame.getPictureURL());
        assertEquals(VALID_DESCRIPTION, createdGame.getDescription());
        assertNotNull(createdGame.getId());
        assertTrue(createdGame.getId() > 0, "Response should have a positive ID.");
        this.testGameId = createdGame.getId();
    }

    @Test
    @Order(2)
    public void testFindValidGameById(){
        //Arrange
        String url = "/games/" + this.testGameId;

        //Act
        ResponseEntity<GameResponseDto> response = restTemplate.getForEntity(url, GameResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        assertEquals(testGameId, game.getId());
        assertEquals(VALID_TITLE, game.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, game.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, game.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, game.getPictureURL());
        assertEquals(VALID_DESCRIPTION, game.getDescription());
    }
}
