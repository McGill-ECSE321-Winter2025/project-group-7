package ca.mcgill.ecse321.boardgamesharingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.AuthRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

@Service
@Validated
public class AccountService {
    @Autowired
    private UserAccountRepository userRepo;
    @Autowired
    private GameOwnerRepository gameOwnerRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;
    
    /** 
     * Searches the database for a UserAccount given an ID and returns it if found
     * @param UserAccountId the ID of the requested UserAccount
     * @return the UserAccount if found
     */
    @Transactional
    public UserAccount findUserAccountById(int UserAccountId) {
        UserAccount user = userRepo.findUserAccountById(UserAccountId);

        if (user == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("No userAccount found with id %d", UserAccountId));
        }

        return user;
    }

    /** 
     * Creates a new UserAccount given a request in the form of a DTO that has a name, email, and password
     * @param request the AuthRequest for creation
     * @return the UserAccount if created
     */
    @Transactional
    public UserAccount createUserAccount(AuthRequest request) {
        validateUserAccountParameters(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        if (userRepo.findUserAccountByName(request.getUsername()) != null) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format(
                "Username %s is already taken", request.getUsername()));
        }

        UserAccount newUser = new UserAccount(            
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        return userRepo.save(newUser);
    }

    /** 
     * Deletes a UserAccount given a valid ID
     * @param UserAccountId the ID of the UserAccount to be deleted
     * @return the UserAccount if deleted
     */
    @Transactional
    public UserAccount deleteUserAccount(int UserAccountId){
        UserAccount user = userRepo
            .findById(UserAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with id %d", UserAccountId)));

        userRepo.delete(user);
        return user;
    }

    
    /** 
     * Verifies the details entered during login 
     * @param request the AuthRequest for login
     * @return the UserAccount if valid
     */
    @Transactional
    public UserAccount login(AuthRequest request) {
        validateUserAccountParameters(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        
        UserAccount user = userRepo.findUserAccountByName(request.getUsername());
        if (user == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with username %s", request.getUsername()));
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format("Incorrect Password"));
        }

        return user;
    }

    
    /** 
     * Toggles a User from GameOwner to Player
     * @param UserAccountId the ID of the UserAccount to toggle
     * @return the UserAccount if toggled
     */
    public UserAccount toggleUserToPlayer(int UserAccountId) {
        UserAccount user = userRepo
            .findById(UserAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with id %d", UserAccountId)));
        
        GameOwner gameOwner = gameOwnerRepo
            .findById(UserAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format(
                "User with id %d is already a player", UserAccountId)));

        gameOwner.setUser(null);

        return user;
    }
    
    /** 
     * Toggles a User from Player to GameOwner
     * @param UserAccountId the ID of the UserAccount to toggle
     * @return the UserAccount if toggled
     */
    public UserAccount toggleUserToGameOwner (int UserAccountId) {
        UserAccount user = userRepo
            .findById(UserAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with id %d", UserAccountId)));
        
        GameOwner gameOwner = gameOwnerRepo
            .findById(UserAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No gameOwner found with id %d", UserAccountId)));
        
        List<GameCopy> games = gameCopyRepo.findByOwnerId(UserAccountId);

        if (games.isEmpty()) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "gameOwner has no associated games"));
        }

        if (!gameOwner.getUser().equals(user)) {
            gameOwner.setUser(user);
            return user;
        }
        else {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format("GameOwner already associated with userAccount with id %d",
            user.getId()));
        }
        
    }
    
    /** 
     * Validates the username, email and password to ensure they are not null
     * @param username the username to validate
     * @param email the email to validate
     * @param password the password to validate
     */
    private void validateUserAccountParameters(String username, String email, String password) {
        String cleanUsername = StringUtils.trimToNull(username);
        String cleanEmail = StringUtils.trimToNull(email);
        String cleanPassword = StringUtils.trimToNull(password);

        if (cleanUsername == null) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (cleanEmail == null) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (cleanPassword == null) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
