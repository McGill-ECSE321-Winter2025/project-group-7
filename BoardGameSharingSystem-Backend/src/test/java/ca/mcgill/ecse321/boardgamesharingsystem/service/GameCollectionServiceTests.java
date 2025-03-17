package ca.mcgill.ecse321.boardgamesharingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameRequestDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class GameCollectionServiceTests {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameCopyRepository gameCopyRepository;
    @InjectMocks
    private GameCollectionService gameCollectionService;

    private static final String VALID_TITLE = "MONOPOLY";
    private static final int VALID_MIN_NUM_PLAYERS = 2;
    private static final int VALID_MAX_NUM_PLAYERS = 7;
    private static final String VALID_PICTURE_URL = "monopoly.png";
    private static final String VALID_DESCRIPTION = "Monopoly is a game released by Hasbro in 1960 ";

    private static final String VALID_TITLE_2 = "UNO";
    private static final int VALID_MIN_NUM_PLAYERS_2 = 2;
    private static final int VALID_MAX_NUM_PLAYERS_2 = 4;
    private static final String VALID_PICTURE_URL_2 = "uno.jpeg";
    private static final String VALID_DESCRIPTION_2 = "UNO is a fun game to be played with friends";
    private Game game;
    private GameCopy gameCopy;
    private static final int GT_MAX_MIN_NUM_PLAYERS = 8;

    private static final String UPDATED_TITLE = "MONOPOLY PREMIUM";
    private static final int UPDATED_MIN_NUM_PLAYERS = 3;
    private static final int UPDATED_MAX_NUM_PLAYERS = 4;
    private static final String UPDATED_PICTURE_URL = "monopoly_premium.png";
    private static final String UPDATED_DESCRIPTION = "Monopoly is a game released by Hasbro in 1960 and fun for kids of all ages!"; 

    @BeforeEach
    void setup(){
        game = new Game(VALID_TITLE_2, VALID_MIN_NUM_PLAYERS_2,VALID_MAX_NUM_PLAYERS_2
        ,VALID_PICTURE_URL_2,VALID_DESCRIPTION_2);

        UserAccount user = new UserAccount("Bobby", "bobby@gmail.com", "bobby1234455555");
        GameOwner gameOwner = new GameOwner(user);
        gameCopy = new GameCopy(game, gameOwner);
    }
    @Test
    public void testFindGameById() {
        //Arrange
        when(gameRepository.findGameById(23)).thenReturn(new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS,
                VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION));

        //Act
        Game game = gameCollectionService.findGameById(23);

        //Assert
        assertNotNull(game);
        assertEquals(VALID_TITLE,game.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, game.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, game.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, game.getPictureURL());
        assertEquals(VALID_DESCRIPTION, game.getDescription());
    }

    @Test
    public void testFindGameByIdThatDoesntExist(){

        //Act + Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
        () -> gameCollectionService.findGameById(42));
        
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Could not find a game with id 42", e.getMessage());
    }

    @Test
    public void testFindAllGames(){
        //Arrange
        when(gameRepository.findAll()).thenReturn(Arrays.asList(game));

        //Act
        List<Game> games = gameCollectionService.findAllGames();

        //Assert
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(1, games.size());
        assertEquals(game, games.get(0));
    }

    @Test
    public void testFindAllGamesIfReturnedListNull(){
        //Arrange
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());
        //Act+Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
        ()-> gameCollectionService.findAllGames());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Could not find a list of games", e.getMessage().trim());
    }

    @Test
    public void testFindGameCopiesFromGame(){
        //Arrange
        when(gameRepository.findById(42)).thenReturn(Optional.of(game));
        when(gameCopyRepository.findByGameId(42)).thenReturn(Arrays.asList(gameCopy));

        //Act
        List<GameCopy> gameCopies = gameCollectionService.findGameCopiesFromGame(42);
        
        //Assert
        assertNotNull(gameCopies);
        assertFalse(gameCopies.isEmpty());
        assertEquals(1, gameCopies.size());
        assertEquals(gameCopy, gameCopies.get(0));

    }
    @Test
    public void testFindGameCopiesFromGameValidGameOwner(){
        //Arrange
        when(gameRepository.findById(42)).thenReturn(Optional.of(game));        
        when(gameCopyRepository.findByGameId(42)).thenReturn(Arrays.asList(gameCopy));
        gameCopy.getGameOwner().setUser(null);
        
        //Act
        List<GameCopy> gameCopies = gameCollectionService.findGameCopiesFromGame(42);
        //Assert
        assertNotNull(gameCopies);
        assertTrue(gameCopies.isEmpty());               
    }

    @Test
    public void testFindGameCopiesFromUnknownGame(){
        // Arrange
        when(gameRepository.findById(42)).thenReturn(null);
        //Act+Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
        ()-> gameCollectionService.findGameCopiesFromGame(42));
        
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Could not find the list of game copies for game with id 42 since it does not exist", e.getMessage().trim());
    }

    @Test
    public void testFindGameCopiesFromGameError(){
        //
        when(gameCopyRepository.findByGameId(42)).thenReturn(null);
        //Act+Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
        ()-> gameCollectionService.findGameCopiesFromGame(42));
        
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Could not find the list of game copies for game with id 42", e.getMessage().trim());        
    }

    @Test
    public void testCreateValidGame() {
        //Arrange
        GameRequestDto gameDto = new GameRequestDto(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS,
                VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.save(any(Game.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        //Act
        Game createdGame = gameCollectionService.createGame(gameDto);

        //Assert
        assertNotNull(createdGame);
        assertEquals(VALID_TITLE, createdGame.getTitle());
        assertEquals(VALID_MIN_NUM_PLAYERS, createdGame.getMinNumPlayers());
        assertEquals(VALID_MAX_NUM_PLAYERS, createdGame.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, createdGame.getPictureURL());
        assertEquals(VALID_DESCRIPTION, createdGame.getDescription());

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void testCreateValidGame_MinPlayersEQMaxPlayers() {
        //Arrange
        GameRequestDto gameDto = new GameRequestDto(VALID_TITLE, 2, 2,
                VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.save(any(Game.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        //Act
        Game createdGame = gameCollectionService.createGame(gameDto);

        //Assert
        assertNotNull(createdGame);
        assertEquals(VALID_TITLE, createdGame.getTitle());
        assertEquals(2, createdGame.getMinNumPlayers());
        assertEquals(2, createdGame.getMaxNumPlayers());
        assertEquals(VALID_PICTURE_URL, createdGame.getPictureURL());
        assertEquals(VALID_DESCRIPTION, createdGame.getDescription());

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void testCreateInvalidGame_MinPlayersGTMaxPlayers() {
        //Arrange
        GameRequestDto gameRequestDto = new GameRequestDto(VALID_TITLE, GT_MAX_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS,
                VALID_PICTURE_URL, VALID_DESCRIPTION);

        //Act + Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
                () -> gameCollectionService.createGame(gameRequestDto));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The minNumPlayers 8 is greater than the maxNumPlayers 7", e.getMessage().trim());
    }

     
    @Test 
    public void testUpdateValidGame(){
        //Arrange
        GameRequestDto gameRequestDto = new GameRequestDto(UPDATED_TITLE, UPDATED_MIN_NUM_PLAYERS, UPDATED_MAX_NUM_PLAYERS,
                UPDATED_PICTURE_URL, UPDATED_DESCRIPTION);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act 
        when(gameRepository.findGameById(42)).thenReturn(game);
        Game updatedGame = gameCollectionService.updateGame(42, gameRequestDto);

        //Assert
        assertNotNull(updatedGame);
        assertEquals(UPDATED_TITLE, updatedGame.getTitle());
        assertEquals(UPDATED_MIN_NUM_PLAYERS, updatedGame.getMinNumPlayers());
        assertEquals(UPDATED_MAX_NUM_PLAYERS, updatedGame.getMaxNumPlayers());
        assertEquals(UPDATED_PICTURE_URL, updatedGame.getPictureURL());
        assertEquals(UPDATED_DESCRIPTION, updatedGame.getDescription());       
    }   

    @Test 
    public void testUpdateValidGame_MinPlayersEQMaxPlayers(){
        //Arrange
        GameRequestDto gameRequestDto = new GameRequestDto(UPDATED_TITLE, 3, 3,
                UPDATED_PICTURE_URL, UPDATED_DESCRIPTION);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        //Act 
        when(gameRepository.findGameById(42)).thenReturn(game);
        Game updatedGame = gameCollectionService.updateGame(42, gameRequestDto);
 
        //Assert
        assertNotNull(updatedGame);
        assertEquals(UPDATED_TITLE, updatedGame.getTitle());
        assertEquals(3, updatedGame.getMinNumPlayers());
        assertEquals(3, updatedGame.getMaxNumPlayers());
        assertEquals(UPDATED_PICTURE_URL, updatedGame.getPictureURL());
        assertEquals(UPDATED_DESCRIPTION, updatedGame.getDescription());       
    }   
 
    @Test 
    public void testUpdateGame_MinPlayersGTMaxPlayers(){
        //Arrange
        GameRequestDto gameRequestDto = new GameRequestDto(VALID_TITLE_2, 4, 3,
                VALID_PICTURE_URL_2, VALID_DESCRIPTION_2);
        when(gameRepository.findGameById(42)).thenReturn(game);
 
        //Act + Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
                () -> gameCollectionService.updateGame(42, gameRequestDto));
 
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The minNumPlayers 4 is greater than the maxNumPlayers 3", e.getMessage().trim());
    } 

    @Test 
    public void testUpdateGameWithIdThatDoesntExist(){
        //Arrange
        GameRequestDto gameRequestDto = new GameRequestDto(VALID_TITLE_2, 2, 3,
                VALID_PICTURE_URL_2, UPDATED_DESCRIPTION);
 
        //Act + Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
                () -> gameCollectionService.updateGame(42, gameRequestDto));
 
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Could not update game because a game with id 42 does not exist", e.getMessage().trim());
    } 
}
