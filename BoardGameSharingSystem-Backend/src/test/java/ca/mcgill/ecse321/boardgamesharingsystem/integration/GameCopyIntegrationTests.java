package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameCopyIntegrationTests {

    @Autowired
    private TestRestTemplate client;
    @Autowired
    private UserAccountRepository userAccountRepo;
    @Autowired
    private GameOwnerRepository gameOwnerRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;

    private UserAccount ownerUser;
    private GameOwner gameOwner;
    private Game testGame;
    private int gameCopyId;

    @BeforeAll
    public void setup() {
        userAccountRepo.deleteAll();
        gameOwnerRepo.deleteAll();
        gameRepo.deleteAll();
        gameCopyRepo.deleteAll();

        ownerUser = userAccountRepo.save(new UserAccount("Test Owner", "owner@test.com", "pass123"));
        gameOwner = gameOwnerRepo.save(new GameOwner(ownerUser));
        testGame = gameRepo.save(new Game("Catan", 3, 4, "catan.com", "A strategy board game."));
    }

    @AfterAll
    public void cleanup() {
        gameCopyRepo.deleteAll();
        gameOwnerRepo.deleteAll();
        userAccountRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidGameCopy() {
        String url = String.format("/gameCopies/%d/%d", gameOwner.getId(), testGame.getId());
        ResponseEntity<GameCopyResponseDto> response = client.exchange(url, HttpMethod.POST, null, GameCopyResponseDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        gameCopyId = response.getBody().getId();
    }

    @Test
    @Order(2)
    public void testCreateGameCopyWithInvalidGameOwner() {
        String url = String.format("/gameCopies/%d/%d", 999, testGame.getId());
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.POST, null, ErrorDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(3)
    public void testCreateGameCopyWithInvalidGame() {
        String url = String.format("/gameCopies/%d/%d", gameOwner.getId(), 999);
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.POST, null, ErrorDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void testGetGameCopiesForOwner() {
        String url = String.format("/gameCopies/forOwner?gameOwnerId=%d", gameOwner.getId());
        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(url, GameCopyResponseDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    @Order(5)
    public void testGetGameCopiesForInvalidOwner() {
        String url = "/gameCopies/forOwner?gameOwnerId=999";
        ResponseEntity<ErrorDto> response = client.getForEntity(url, ErrorDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void testGetGameCopiesForGame() {
        String url = String.format("/gameCopies/forGame?gameId=%d", testGame.getId());
        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(url, GameCopyResponseDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    @Order(7)
    public void testGetGameCopiesForInvalidGame() {
        String url = "/gameCopies/forGame?gameId=999";
        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(url, GameCopyResponseDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    @Test
    @Order(8)
    public void testDeleteGameCopySuccessfully() {
        // Create a second game
        Game secondGame = gameRepo.save(new Game("Codenames", 2, 8, "codenames.com", "A spy game."));

        // Add a second game copy for this owner
        String createUrl = String.format("/gameCopies/%d/%d", gameOwner.getId(), secondGame.getId());
        ResponseEntity<GameCopyResponseDto> createResponse = client.exchange(
                createUrl,
                HttpMethod.POST,
                null,
                GameCopyResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // delete the first game copy
        String deleteUrl = String.format("/gameCopies/%d", gameCopyId);
        ResponseEntity<GameCopyResponseDto> response = client.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                null,
                GameCopyResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto deletedCopy = response.getBody();
        assertNotNull(deletedCopy);
        assertEquals(gameCopyId, deletedCopy.getId());
        assertEquals(testGame.getId(), deletedCopy.getGameId());
        assertEquals(gameOwner.getId(), deletedCopy.getOwnerId());
    }




    @Test
    @Order(9)
    public void testDeleteNonExistentGameCopy() {
        String url = "/gameCopies/999";
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(10)
    public void testCannotDeleteLastGameCopy() {
        // Ensure there is only one game copy
        gameCopyRepo.deleteAll();

        String createUrl = String.format("/gameCopies/%d/%d", gameOwner.getId(), testGame.getId());
        ResponseEntity<GameCopyResponseDto> createResponse = client.exchange(
                createUrl,
                HttpMethod.POST,
                null,
                GameCopyResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        gameCopyId = createResponse.getBody().getId(); // Store ID for deletion

        // Attempt to delete the last remaining GameCopy
        String deleteUrl = String.format("/gameCopies/%d", gameCopyId);
        ResponseEntity<ErrorDto> response = client.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                null,
                ErrorDto.class
        );

        // Expect BAD_REQUEST (400) because this is the last copy
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains(
                String.format("Cannot remove game copy with id %d since game owner with id %d has one game copy remaining",
                        gameCopyId, gameOwner.getId())
        ));
    }
}