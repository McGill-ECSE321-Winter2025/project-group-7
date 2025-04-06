package ca.mcgill.ecse321.boardgamesharingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.AuthRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class AccountServiceTests {
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private GameOwnerRepository gameOwnerRepository;
    @Mock
    private GameCopyRepository gameCopyRepository;
    @InjectMocks
    private AccountService accountService;
    
    private static final String NAME = "Mila";
    private static final String EMAIL = "mila@bunnymail.com";
    private static final String PASSWORD = "bunnies123"; 
    
    @Test
    public void testFindUserAccountByValidId() {
        //Arrange 
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
    
        //Act
        UserAccount userAccount = accountService.findUserAccountById(42);
    
        //Assert
        assertNotNull(userAccount);
        assertEquals(NAME, userAccount.getName());
        assertEquals(EMAIL, userAccount.getEmail());
        assertEquals(PASSWORD, userAccount.getPassword());
    }
    
    @Test
    public void testFindUserAccountThatDoesntExist() {
        //Arrange + Act + Arrange
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.findUserAccountById(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No userAccount found with id 42", exception.getMessage());
    }

    @Test
    public void createValidUserAccount() {
        //Arrange
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        AuthRequest request = new AuthRequest();
        request.setUsername(NAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        //Act
        UserAccount createdUserAccount = accountService.createUserAccount(request);

        //Assert
        assertNotNull(createdUserAccount);
        assertEquals(NAME, createdUserAccount.getName());
        assertEquals(EMAIL, createdUserAccount.getEmail());
        assertEquals(PASSWORD, createdUserAccount.getPassword());
        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    public void createUserAccountWithExistingUserName() {
        //Arrange
        when(userAccountRepository.findUserAccountByName(NAME)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        AuthRequest request = new AuthRequest();
        request.setUsername(NAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,() -> accountService.createUserAccount(request));
        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
        assertEquals("Username Mila is already taken", exception.getMessage());
    }

    @Test
    public void testDeleteUserAccountWithValidId() {
        //Arrange
        UserAccount userToDelete = new UserAccount(NAME, EMAIL, PASSWORD);
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(userToDelete));

        //Act
        UserAccount deletedUser = accountService.deleteUserAccount(42);

        //Assert
        assertNotNull(deletedUser);
        assertEquals(NAME, deletedUser.getName());
        assertEquals(EMAIL, deletedUser.getEmail());
        assertEquals(PASSWORD, deletedUser.getPassword());
        verify(userAccountRepository, times(1)).delete(userToDelete);
    }

    @Test
    public void testDeleteUserAccountThatDoesntExist() {
        //Arrange + Act + Arrange
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.deleteUserAccount(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No userAccount found with id 42", exception.getMessage());
    }

    @Test
    public void testUpdateValidUserAccount()
    {
        //Arrange
        UserAccount userToUpdate = new UserAccount(NAME, EMAIL, PASSWORD);
        when(userAccountRepository.findUserAccountById(42)).thenReturn(userToUpdate);
        String newName = "Mitski";
        String newEmail = "mitski@mail.com";
        String newPassword = "TsukiNoHime";

        AuthRequest request = new AuthRequest();
        request.setUsername(newName);
        request.setEmail(newEmail);
        request.setPassword(newPassword);
        
        when(userAccountRepository.findUserAccountByName("Mitski")).thenReturn(null);
        when(userAccountRepository.save(userToUpdate)).thenReturn(userToUpdate);
        
        //Act
        UserAccount updatedUserAccount = accountService.updateUserAccount(42, request);

        //Assert
        assertNotNull(updatedUserAccount);
        assertEquals(newName, updatedUserAccount.getName());
        assertEquals(newEmail, updatedUserAccount.getEmail());
        assertEquals(newPassword, updatedUserAccount.getPassword());
    }
    
    @Test
    public void testValidLogin() {
        //Arrange 
        when(userAccountRepository.findUserAccountByName(NAME)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        AuthRequest request = new AuthRequest();
        request.setUsername(NAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        //Act
        UserAccount loggedInUser = accountService.login(request);

        //Assert
        assertNotNull(loggedInUser);
        assertEquals(NAME, loggedInUser.getName());
        assertEquals(EMAIL, loggedInUser.getEmail());
        assertEquals(PASSWORD, loggedInUser.getPassword());
    }

    @Test
    public void testLoginWithNonExistentUser() {
        //Arrange 
        AuthRequest request = new AuthRequest();
        request.setUsername(NAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.login(request));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No userAccount found with username Mila", exception.getMessage());
    }

    @Test
    public void testLoginWithIncorrectPassword() {
        //Arrange
        when(userAccountRepository.findUserAccountByName(NAME)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        AuthRequest request = new AuthRequest();
        request.setUsername(NAME);
        request.setEmail(EMAIL);
        request.setPassword("wrongPassword");

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.login(request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Incorrect Password", exception.getMessage());
    }

    @Test
    public void testSuccessfulToggleToPlayer() {
        // Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(user));
        GameOwner owner = new GameOwner(user);
        when(gameOwnerRepository.findGameOwnerById(42)).thenReturn(owner);

        // Act
        UserAccount toggledAccount = accountService.toggleUserToPlayer(42);

        // Assert
        assertNotNull(toggledAccount);
        assertEquals(NAME, toggledAccount.getName());
        assertEquals(EMAIL, toggledAccount.getEmail());
        assertEquals(PASSWORD, toggledAccount.getPassword());
        verify(gameOwnerRepository, times(1)).save(argThat(o -> o.getUser() == null));
    }

    @Test 
    public void testToggleToPlayerWithNonexistentUserAccount() {
        // Arrange
        when(userAccountRepository.findById(42)).thenReturn(Optional.empty());

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToPlayer(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No userAccount found with id 42", exception.getMessage());
    }

    @Test
    public void testInvalidToggleToPlayerFromAlreadyPlayer() {
        // Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(user));
        GameOwner gameOwner = new GameOwner(user);
        gameOwner.setUser(null);
        when(gameOwnerRepository.findGameOwnerById(42)).thenReturn(gameOwner);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToPlayer(42));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User with id 42 is already a player", exception.getMessage());
    }

    @Test
    public void testSuccessfulToggleToGameOwner() {
        // Arrange
        UserAccount realUser = new UserAccount(NAME, EMAIL, PASSWORD);

        when(userAccountRepository.findById(42)).thenReturn(Optional.of(realUser));

        GameOwner gameOwner = new GameOwner(realUser);
        gameOwner.setUser(null);
        when(gameOwnerRepository.findById(42)).thenReturn(Optional.of(gameOwner));
        
        Game chess = new Game("chess",2,2,"Example/url","its chess");
        GameCopy chessCopy = new GameCopy(chess,gameOwner);
        List<GameCopy> validGameCopyList = Arrays.asList(chessCopy);
        when(gameCopyRepository.findByOwnerId(42)).thenReturn(validGameCopyList);

        when(gameOwnerRepository.save(any(GameOwner.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        GameOwner newGameOwner = accountService.toggleUserToGameOwner(42);

        // Assert
        assertNotNull(newGameOwner);
        assertEquals(realUser, newGameOwner.getUser());
        verify(gameOwnerRepository, times(1)).save(any(GameOwner.class));
    }

    @Test 
    public void testToggleToGameOwnerWithNonexistentUserAccount() {
        // Arrange
        when(userAccountRepository.findById(42)).thenReturn(Optional.empty());

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToGameOwner(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No userAccount found with id 42", exception.getMessage());
    }

    @Test 
    public void testToggleToGameOwnerWithNonexistentGameOwner() {
        // Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(user));
        when(gameOwnerRepository.findById(42)).thenReturn(Optional.empty());

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToGameOwner(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No gameOwner found with id 42", exception.getMessage());
    }

    @Test 
    public void testToggleToGameOwnerWithNoAssociatedGames() {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(user));

        GameOwner owner = new GameOwner(user);
        when(gameOwnerRepository.findById(42)).thenReturn(Optional.of(owner));

        when(gameCopyRepository.findByOwnerId(42)).thenReturn(Collections.emptyList());
        
        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToGameOwner(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("gameOwner has no associated games", exception.getMessage());
    }

    @Test
    public void testToggleToGameOwnerAlreadyAssociatedToUser() {
        // Arrange
        UserAccount user = spy(new UserAccount(NAME, EMAIL, PASSWORD));
        when(userAccountRepository.findById(42)).thenReturn(Optional.of(user));
        doReturn(42).when(user).getId();
        
        GameOwner owner = new GameOwner(user);
        when(gameOwnerRepository.findById(42)).thenReturn(Optional.of(owner));
        
        Game chess = new Game("chess",2,2,"Example/url","its chess");
        GameCopy chessCopy = new GameCopy(chess,owner);
        List<GameCopy> validGameCopyList = Arrays.asList(chessCopy);
        when(gameCopyRepository.findByOwnerId(42)).thenReturn(validGameCopyList);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToGameOwner(42));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("GameOwner already associated with userAccount with id 42", exception.getMessage());
    }

    @Test
    void testCreateUserAccountWithEmptyUsername() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername("");
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.createUserAccount(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateUserAccountWithEmptyEmail() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail("");
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.createUserAccount(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateUserAccountWithEmptyPassword() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword("");

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.createUserAccount(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Password cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateUserAccountWithNullUsername() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(null);
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.createUserAccount(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateUserAccountWithNullEmail() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail(null);
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.createUserAccount(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateUserAccountWithNullPassword() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword(null);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.createUserAccount(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Password cannot be empty", exception.getMessage());
    }

    @Test
    void testLoginWithEmptyUsername() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername("");
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.login(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    void testLoginWithEmptyEmail() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail("");
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.login(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    void testLoginWithEmptyPassword() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword("");

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.login(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Password cannot be empty", exception.getMessage());
    }

    @Test
    void testLoginWithNullUsername() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(null);
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.login(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    void testLoginWithNullEmail() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail(null);
        invalidRequest.setPassword(PASSWORD);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.login(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    void testLoginWithNullPassword() {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setUsername(NAME);
        invalidRequest.setEmail(EMAIL);
        invalidRequest.setPassword(null);

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->
        accountService.login(invalidRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Password cannot be empty", exception.getMessage());
    }
}
