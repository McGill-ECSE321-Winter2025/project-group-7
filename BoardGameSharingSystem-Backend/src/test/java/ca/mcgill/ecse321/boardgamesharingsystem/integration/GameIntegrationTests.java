package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameRequestDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private GameRepository gameRepo;

    private static final String VALID_TITLE = "Connect4";
    private static final int VALID_MIN_NUM_PLAYERS = 2;
    private static final int VALID_MAX_NUM_PLAYERS = 2;
    private static final String VALID_PICTURE_URL = "https://toysrus.com/Connect4.jpg";
    private static final String VALID_DESCRIPTION = "Connect4 is a fast turn-based game, perfect to play with friends or family!";
    private static final String UPDATED_VALID_TITLE = "Connect4 3D";
    private static final int UPDATED_VALID_MIN_NUM_PLAYERS = 1;
    private static final int UPDATED_VALID_MAX_NUM_PLAYERS = 4;
    private static final String UPDATED_VALID_PICTURE_URL = "https://toysrus.com/Connect4-3D.jpg";
    private static final String UPDATED_VALID_DESCRIPTION = "A twist to the classic Connect4 game that can now be played with up to 4 players!";   

    private int testGameId; //id of Connect4

    @BeforeAll
    public void setup() {
        gameRepo.deleteAll();
    }

    @Test
    @Order(2)
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
        this.testGameId = createdGame.getId(); //id of the created game
    }

    @Test
    @Order(3)
    public void testCreateInvalidGame_MinPlayerGTMaxPlayer(){
        //Arrange
        GameRequestDto request = new GameRequestDto(VALID_TITLE, 8, 4, VALID_PICTURE_URL, VALID_DESCRIPTION);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity("/games", request, ErrorDto.class);
    
        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The minNumPlayers 8 is greater than the maxNumPlayers 4", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(4)
    public void testCreateInvalidGame_EmptyTitle(){
        //Arrange
        GameRequestDto request = new GameRequestDto(null, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity("/games", request, ErrorDto.class);
    
        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The game must have a title.", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(5)
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

    @Test
    @Order(6)
    public void testFindGameThatDoesntExist(){
        //Arrange
        String url = "/games/" + 400;

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.getForEntity(url, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Could not find a game with id 400", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(7)
    public void testUpdateGame_MinPlayersGTMaxPlayers(){
        //Arrange
        String url = "/games/" + this.testGameId;
        GameRequestDto request = new GameRequestDto(VALID_TITLE, 10, 4, VALID_PICTURE_URL, VALID_DESCRIPTION);
        HttpEntity<GameRequestDto> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The minNumPlayers 10 is greater than the maxNumPlayers 4", response.getBody().getErrors().get(0));
        //Make sure that the game info remained the same as before
        ResponseEntity<GameResponseDto> afterFailedUpdateResponse = restTemplate.getForEntity(url, GameResponseDto.class);
        assertNotNull(afterFailedUpdateResponse);
        assertEquals(HttpStatus.OK, afterFailedUpdateResponse.getStatusCode());
        GameResponseDto game = afterFailedUpdateResponse.getBody();
        assertNotNull(game);
        assertEquals(testGameId, game.getId());
        assertEquals(VALID_TITLE, game.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, game.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, game.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, game.getPictureURL());
        assertEquals(VALID_DESCRIPTION, game.getDescription());
    }

    @Test
    @Order(8)
    public void testUpdateGame_NegativeMinPlayer(){
        //Arrange
        String url = "/games/" + this.testGameId;
        GameRequestDto request = new GameRequestDto(VALID_TITLE, -2, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        HttpEntity<GameRequestDto> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The game must have a positive minNumPlayers.", response.getBody().getErrors().get(0));
        //Make sure that the game info remained the same as before
        ResponseEntity<GameResponseDto> afterFailedUpdateResponse = restTemplate.getForEntity(url, GameResponseDto.class);
        assertNotNull(afterFailedUpdateResponse);
        assertEquals(HttpStatus.OK, afterFailedUpdateResponse.getStatusCode());
        GameResponseDto game = afterFailedUpdateResponse.getBody();
        assertNotNull(game);
        assertEquals(testGameId, game.getId());
        assertEquals(VALID_TITLE, game.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, game.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, game.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, game.getPictureURL());
        assertEquals(VALID_DESCRIPTION, game.getDescription());
    }

    @Test
    @Order(9)
    public void testUpdateGame_EmptyPictureURL(){
        //Arrange
        String url = "/games/" + this.testGameId;
        GameRequestDto request = new GameRequestDto(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, null, VALID_DESCRIPTION);
        HttpEntity<GameRequestDto> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The game must have a pictureURL.", response.getBody().getErrors().get(0));
        //Make sure that the game info remained the same as before
        ResponseEntity<GameResponseDto> afterFailedUpdateResponse = restTemplate.getForEntity(url, GameResponseDto.class);
        assertNotNull(afterFailedUpdateResponse);
        assertEquals(HttpStatus.OK, afterFailedUpdateResponse.getStatusCode());
        GameResponseDto game = afterFailedUpdateResponse.getBody();
        assertNotNull(game);
        assertEquals(testGameId, game.getId());
        assertEquals(VALID_TITLE, game.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, game.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, game.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, game.getPictureURL());
        assertEquals(VALID_DESCRIPTION, game.getDescription());
    }

    @Test
    @Order(10)
    public void testUpdateValidGame(){
        //Arrange
        String url = "/games/" + this.testGameId;
        GameRequestDto request = new GameRequestDto(UPDATED_VALID_TITLE, UPDATED_VALID_MIN_NUM_PLAYERS, UPDATED_VALID_MAX_NUM_PLAYERS, UPDATED_VALID_PICTURE_URL, UPDATED_VALID_DESCRIPTION);
        HttpEntity<GameRequestDto> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        //Act
        ResponseEntity<GameResponseDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, GameResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        GameResponseDto updatedGame = response.getBody();
        assertNotNull(updatedGame);
        assertEquals(testGameId, updatedGame.getId());
        assertEquals(UPDATED_VALID_TITLE, updatedGame.getTitle());
        assertEquals(UPDATED_VALID_MIN_NUM_PLAYERS, updatedGame.getMinNumPlayers());
        assertEquals(UPDATED_VALID_MAX_NUM_PLAYERS, updatedGame.getMaxNumPlayers());
        assertEquals(UPDATED_VALID_PICTURE_URL, updatedGame.getPictureURL());
        assertEquals(UPDATED_VALID_DESCRIPTION, updatedGame.getDescription());
    }

    @Test
    @Order(11)
    public void testFindAllGames() {
        // Arrange (add another game (Pokemon) besides Connect4 3D --> 2 games in db)
        GameRequestDto request = new GameRequestDto("Pokemon", 2, 2, "https://pokemon.jpeg", "Gotta Catch 'em all!");
        
        //Act
        ResponseEntity<GameResponseDto> responseForCreate = restTemplate.postForEntity("/games", request, GameResponseDto.class);
        ResponseEntity<GameResponseDto[]> responseForFindAll = restTemplate.getForEntity("/games", GameResponseDto[].class);

        //Assert
        assertNotNull(responseForFindAll);
        assertEquals(HttpStatus.OK, responseForFindAll.getStatusCode());
        assertNotNull(responseForFindAll.getBody());
        GameResponseDto[] games = responseForFindAll.getBody();
        assertTrue(games.length == 2, "Expected 2 games in the database."); //Check 2 games in db
        assertEquals(testGameId, games[0].getId()); //Check it contains Connect4 3D w/ the right ID
        assertEquals("Connect4 3D", games[0].getTitle());
        GameResponseDto pokemonGame = responseForCreate.getBody();
        int pokemonId = pokemonGame.getId(); 
        assertEquals(pokemonId, games[1].getId()); //Check it contains Pokemon w/ the right ID
        assertEquals("Pokemon", games[1].getTitle());
    }

    @Test
    @Order(1)
    public void testFindAllGamesIfReturnedListNull() {
        //Act (nothing to assert -- looking for games when no game in system)
        ResponseEntity<ErrorDto> response = restTemplate.getForEntity("/games", ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Could not find a list of games", response.getBody().getErrors().get(0));
    }
}
