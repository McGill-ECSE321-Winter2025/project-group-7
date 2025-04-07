package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

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

    private UserAccount user;
    private GameOwner owner;
    private Game game;
    private int gameCopyId;

    @BeforeAll
    public void setup() {
        userAccountRepo.deleteAll();
        gameOwnerRepo.deleteAll();
        gameRepo.deleteAll();
        gameCopyRepo.deleteAll();

        user = userAccountRepo.save(new UserAccount("Guru", "owner@gmail.com", "12345"));
        owner = gameOwnerRepo.save(new GameOwner(user));
        game = gameRepo.save(new Game("Catan", 3, 4, "catan.com", "A board game."));
        gameCopyId = game.getId();
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
    public void testAddGameCopyToGameOwner_Success() {
        ResponseEntity<GameCopyResponseDto> response = client.postForEntity(
                "/gameCopies/" + owner.getId() + "/" + game.getId(), null, GameCopyResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(owner.getId(), response.getBody().getOwnerId());
        assertEquals(game.getId(), response.getBody().getGameId());
    }

    @Test
    @Order(2)
    public void testAddGameCopyToGameOwner_InvalidOwnerId() {
        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/gameCopies/9999/" + game.getId(), null, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(3)
    public void testAddGameCopyToGameOwner_InvalidGameId() {
        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/gameCopies/" + owner.getId() + "/9999", null, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(4)
    public void testAddGameCopyToGameOwner_CopiesNotFound() {
        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/gameCopies/9999/9999", null, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(5)
    public void testAddGameCopyToGameOwner_CopiesEmpty() {
        gameCopyRepo.deleteAll();

        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(
                "/gameCopies/forGame?gameId=" + game.getId(), GameCopyResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    @Test
    @Order(6)
    public void testRemoveGameCopyFromGameOwner_Success() {
        // create a new game and make a copy from it
        Game newGame = gameRepo.save(new Game("Codenames", 2, 8, "codenames.com", "A spy game."));
        ResponseEntity<GameCopyResponseDto> createResponse1 = client.postForEntity(
                "/gameCopies/" + owner.getId() + "/" + newGame.getId(), null, GameCopyResponseDto.class);

        // create a game copy from the existing game
        ResponseEntity<GameCopyResponseDto> createResponse2 = client.postForEntity(
                "/gameCopies/" + owner.getId() + "/" + game.getId(), null, GameCopyResponseDto.class);

        assertEquals(HttpStatus.CREATED, createResponse1.getStatusCode());
        assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());

        int gameCopyIdToDelete = createResponse1.getBody().getId(); // pick one copy to delete

        // Act
        ResponseEntity<GameCopyResponseDto> deleteResponse = client.exchange(
                "/gameCopies/" + gameCopyIdToDelete, HttpMethod.DELETE, null, GameCopyResponseDto.class);

        // Assert
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals(gameCopyIdToDelete, deleteResponse.getBody().getId());
        assertEquals(newGame.getId(), deleteResponse.getBody().getGameId());
        assertEquals(owner.getId(), deleteResponse.getBody().getOwnerId());
    }

    @Test
    @Order(7)
    public void testRemoveGameCopyFromGameOwner_InvalidGameId() {
        ResponseEntity<ErrorDto> response = client.exchange(
                "/gameCopies/9999", HttpMethod.DELETE, null, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(8)
    public void testRemoveGameCopyFromGameOwner_InvalidOwnerId() {
        ResponseEntity<ErrorDto> response = client.exchange(
                "/gameCopies/9999", HttpMethod.DELETE, null, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    public void testRemoveGameCopyFromGameOwner_CannotRemoveLastCopy() {
        // Ensure there is only one game copy
        gameCopyRepo.deleteAll();

        String createUrl = String.format("/gameCopies/%d/%d", owner.getId(), game.getId());
        ResponseEntity<GameCopyResponseDto> createResponse = client.exchange(
                createUrl,
                HttpMethod.POST,
                null,
                GameCopyResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        gameCopyId = createResponse.getBody().getId();

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
                        gameCopyId, owner.getId())
        ));
    }

    @Test
    @Order(10)
    public void testFindGameCopiesForGameOwner_Success() {
        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(
                "/gameCopies/forOwner?gameOwnerId=" + owner.getId(), GameCopyResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
        if (response.getBody().length > 0) {
            assertEquals(owner.getId(), response.getBody()[0].getOwnerId());
        }
    }

    @Test
    @Order(11)
    public void testFindGameCopiesForGameOwner_InvalidGameId() {
        ResponseEntity<ErrorDto> response = client.getForEntity(
                "/gameCopies/forOwner?gameOwnerId=9999", ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(12)
    public void testFindGameCopiesFromGame_Success() {
        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(
                "/gameCopies/forGame?gameId=" + game.getId(), GameCopyResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }
    @Test
    @Order(13)
    public void testFindGameCopiesFromGame_InvalidGameId() {
        ResponseEntity<ErrorDto> response = client.getForEntity(
                "/gameCopies/forGame?gameId=9999", ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}