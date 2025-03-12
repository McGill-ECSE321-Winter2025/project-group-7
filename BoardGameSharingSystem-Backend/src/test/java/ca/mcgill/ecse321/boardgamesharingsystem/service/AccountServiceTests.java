package ca.mcgill.ecse321.boardgamesharingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void testValidLogin() {
        //Arrange 
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        AuthRequest request = new AuthRequest();
        request.setUsername(NAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        //Act
        UserAccount loggedInUser = accountService.findUserAccountById(42);

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
        when(gameOwnerRepository.findById(42)).thenReturn(Optional.of(owner));

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
        when(gameOwnerRepository.findById(42)).thenReturn(Optional.empty());

        // Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> accountService.toggleUserToPlayer(42));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User with id 42 is already a player", exception.getMessage());
    }

}
