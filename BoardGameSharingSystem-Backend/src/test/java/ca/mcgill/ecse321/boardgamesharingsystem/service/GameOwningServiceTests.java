package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
public class GameOwningServiceTests {

    @Mock
    private GameOwnerRepository gameOwnerRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameCopyRepository gameCopyRepository;
    @Mock
    private BorrowRequestRepository borrowRequestRepository;

    @InjectMocks
    private GameOwningService gameOwningService;

    @Test
    public void testCreateGameOwnerSuccessfully() {
        UserAccount user = new UserAccount("test", "test@gmail.com", "123");
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(user));
        when(gameOwnerRepository.save(any(GameOwner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameOwner result = gameOwningService.createGameOwner(1);

        assertNotNull(result);
        assertEquals(user, result.getUser());
    }
    @Test
    public void testCreateGameOwnerUserNotFound() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> gameOwningService.createGameOwner(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testCreatGameOwnerAlreadyPresent(){
        UserAccount testUser = new UserAccount("test", "test@outlook.com", "hello12345678");
        GameOwner gameOwner = new GameOwner(testUser);
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(testUser));
        when(gameOwnerRepository.findGameOwnerById(42)).thenReturn(gameOwner);

        //Act+Asert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class,
         ()-> gameOwningService.createGameOwner(42));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()); 
        assertEquals(
            "The user with id 42 already has a game owner with the same id created",
             e.getMessage());
    }

    @Test
    public void testFindGameOwnerSuccessfully() {
        GameOwner owner = new GameOwner(new UserAccount("test", "test@gmail.com", "123"));
        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(owner);

        GameOwner result = gameOwningService.findGameOwner(1);

        assertNotNull(result);
        assertEquals(owner, result);
    }

    @Test
    public void testFindGameOwnerNotFound() {
        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(null);

        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> gameOwningService.findGameOwner(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testAddGameCopyToGameOwner_GameCopyListNull() {
        GameOwner owner = new GameOwner(new UserAccount("owner", "owner@test.com", "pass"));
        Game game = new Game("Chess", 2, 4, "url", "desc");

        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(owner);
        when(gameRepository.findGameById(2)).thenReturn(game);
        when(gameCopyRepository.findByGameIdAndOwnerId(2, 1)).thenReturn(null); // simulate null return

        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.addGameCopyToGameOwner(1, 2));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testAddGameCopyToGameOwner_GameCopyAlreadyExists() {
        GameOwner owner = new GameOwner(new UserAccount("owner", "owner@test.com", "pass"));
        Game game = new Game("Chess", 2, 4, "url", "desc");
        GameCopy existingCopy = new GameCopy(game, owner);

        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(owner);
        when(gameRepository.findGameById(2)).thenReturn(game);
        when(gameCopyRepository.findByGameIdAndOwnerId(2, 1)).thenReturn(List.of(existingCopy)); // already exists

        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.addGameCopyToGameOwner(1, 2));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testAddGameCopyToGameOwner_GameOwnerNotFound() {
        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(null);

        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.addGameCopyToGameOwner(1, 1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testAddGameCopyToGameOwner_GameNotFound() {
        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(new GameOwner(new UserAccount("a", "a@email.com", "pass")));
        when(gameRepository.findGameById(1)).thenReturn(null);

        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.addGameCopyToGameOwner(1, 1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testAddGameCopyToGameOwnerSuccessfully() {
        GameOwner owner = new GameOwner(new UserAccount("test", "test@gmail.com", "123"));
        Game game = new Game("Chess", 2, 4, "url", "desc");

        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(owner);
        when(gameRepository.findGameById(2)).thenReturn(game);
        when(gameCopyRepository.findByGameIdAndOwnerId(2, 1)).thenReturn(List.of());
        when(gameCopyRepository.save(any(GameCopy.class))).thenAnswer(inv -> inv.getArgument(0));

        GameCopy result = gameOwningService.addGameCopyToGameOwner(1, 2);

        assertNotNull(result);
        assertEquals(owner, result.getGameOwner());
        assertEquals(game, result.getGame());
    }

    @Test
    public void testRemoveGameCopyFromGameOwnerSuccessfully() {
        GameOwner owner = new GameOwner(new UserAccount("test", "test@gmail.com", "123"));
        Game game = new Game("Chess", 2, 4, "url", "desc");
        GameCopy copy = new GameCopy(game, owner);
        List<GameCopy> copies = List.of(copy, new GameCopy(game, owner)); // Multiple copies

        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(copy));
        when(gameCopyRepository.findByOwnerId(owner.getId())).thenReturn(copies);

        GameCopy result = gameOwningService.removeGameCopyFromGameOwner(1);

        assertNotNull(result);
        verify(gameCopyRepository, times(1)).delete(copy);
    }

    @Test
    public void testRemoveGameCopyOnlyOneRemaining() {
        GameOwner owner = new GameOwner(new UserAccount("test", "test@gmail.com", "123"));
        Game game = new Game("Chess", 2, 4, "url", "desc");
        GameCopy copy = new GameCopy(game, owner);

        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(copy));
        when(gameCopyRepository.findByOwnerId(owner.getId())).thenReturn(List.of(copy)); // Only one copy

        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> gameOwningService.removeGameCopyFromGameOwner(1));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testRemoveGameCopyFromGameOwner_GameOwnerNotFound() {
        GameCopy gameCopy = mock(GameCopy.class);
        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(gameCopy));
        when(gameCopy.getGameOwner()).thenReturn(null); // GameOwner is null

        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.removeGameCopyFromGameOwner(1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testRemoveGameCopyFromGameOwner_GameCopyNotFound() {
        when(gameCopyRepository.findById(1)).thenReturn(Optional.empty()); // Simulate game copy not found

        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.removeGameCopyFromGameOwner(1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Could not find game copy with id 1", exception.getMessage()); // Check exception message
    }

    @Test
    public void testRemoveGameCopyFromGameOwner_GameCopiesListIsNull() {
        Game game = new Game("Chess", 2, 4, "url", "desc");
        UserAccount user = new UserAccount("test", "test@gmail.com", "123");
        GameOwner owner = new GameOwner(user);
        GameCopy copy = new GameCopy(game, owner);

        when(gameCopyRepository.findById(1)).thenReturn(Optional.of(copy));
        when(gameCopyRepository.findByOwnerId(owner.getId())).thenReturn(null); // Mocking null!

        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.removeGameCopyFromGameOwner(1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Could not find gameCopies from game owner with id " + owner.getId(), exception.getMessage());
    }

    @Test
    public void testFindGameCopiesForGameOwnerSuccessfully() {
        GameOwner owner = new GameOwner(new UserAccount("test", "test@gmail.com", "123"));
        Game game = new Game("Chess", 2, 4, "url", "desc");
        List<GameCopy> copies = List.of(new GameCopy(game, owner));

        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(owner);
        when(gameCopyRepository.findByOwnerId(1)).thenReturn(copies);

        List<GameCopy> result = gameOwningService.findGameCopiesForGameOwner(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindGameCopiesForGameOwnerNotFound() {
        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(null);

        BoardGameSharingSystemException ex = assertThrows(BoardGameSharingSystemException.class, () -> gameOwningService.findGameCopiesForGameOwner(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testFindGameCopiesForGameOwner_GameCopiesNull() {
        GameOwner owner = new GameOwner(new UserAccount("a", "a@email.com", "pass"));
        when(gameOwnerRepository.findGameOwnerById(1)).thenReturn(owner);
        when(gameCopyRepository.findByOwnerId(1)).thenReturn(null);

        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,
                () -> gameOwningService.findGameCopiesForGameOwner(1));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
