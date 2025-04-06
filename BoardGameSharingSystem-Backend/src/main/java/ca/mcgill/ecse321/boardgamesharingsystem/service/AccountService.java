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

/**
 * This service class implements functionalities related to the UserAccount,
 * including creating, login, deleting, and toggling between Player and GameOwner, as well
 * as retrieving UserAccounts.
 */
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
     * @param userAccountId the ID of the requested UserAccount
     * @return the UserAccount if found
     */
    @Transactional
    public UserAccount findUserAccountById(int userAccountId) {
        UserAccount user = userRepo.findUserAccountById(userAccountId);

        if (user == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("No userAccount found with id %d", userAccountId));
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
     * @param userAccountId the ID of the UserAccount to be deleted
     * @return the UserAccount if deleted
     */
    @Transactional
    public UserAccount deleteUserAccount(int userAccountId){
        UserAccount user = userRepo
            .findById(userAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with id %d", userAccountId)));

        userRepo.delete(user);
        return user;
    }

    
    /** 
     * Searches the database for a UserAccount, updates it with given attributes, and returns it
     * @param userAccountId the ID of the UserAccount to update
     * @param request a request with the attributes to update to
     * @return the updated UserAccount
     */
    @Transactional
    public UserAccount updateUserAccount (int userAccountId, AuthRequest request){
        UserAccount userToUpdate = findUserAccountById(userAccountId);
        validateUserAccountParameters(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        
        UserAccount existingUser = userRepo.findUserAccountByName(request.getUsername());
        if (existingUser != null && existingUser.getId() != userAccountId) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format(
                "Username %s is already taken", request.getUsername()));
        }
        userToUpdate.setName(request.getUsername());
        userToUpdate.setEmail(request.getEmail());
        userToUpdate.setPassword(request.getPassword());
        return userRepo.save(userToUpdate);
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

        if (!user.getEmail().equals(request.getEmail())) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format("No userAccount found with email %s", request.getEmail()));
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format("Incorrect Password"));
        }

        return user;
    }

    
    /** 
     * Toggles a User from GameOwner to Player
     * @param userAccountId the ID of the UserAccount to toggle
     * @return the UserAccount if toggled
     */
    public UserAccount toggleUserToPlayer(int userAccountId) {
        UserAccount user = userRepo
            .findById(userAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with id %d", userAccountId)));
        
        if (gameOwnerRepo.findGameOwnerById(userAccountId).getUser() != null) {
            GameOwner gameOwner = gameOwnerRepo.findGameOwnerById(userAccountId);
            gameOwner.setUser(null);
            gameOwnerRepo.save(gameOwner);
        }
        else {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format(
                "User with id %d is already a player", userAccountId));
        }

        return user;
    }
    
    /** 
     * Toggles a User from Player to GameOwner
     * @param userAccountId the ID of the UserAccount to toggle
     * @return the GameOwner if toggled
     */
    public GameOwner toggleUserToGameOwner (int userAccountId) {
        UserAccount user = userRepo
            .findById(userAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No userAccount found with id %d", userAccountId)));
        
        GameOwner gameOwner = gameOwnerRepo
            .findById(userAccountId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "No gameOwner found with id %d", userAccountId)));
        
        List<GameCopy> games = gameCopyRepo.findByOwnerId(userAccountId);

        if (games.isEmpty()) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format(
                "gameOwner has no associated games"));
        }

        if (gameOwner.getUser() == null) {
            gameOwner.setUser(user);
            gameOwner = gameOwnerRepo.save(gameOwner);
            return gameOwner;
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
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
        }
        if (cleanEmail == null) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }
        if (cleanPassword == null) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Password cannot be empty"); 
        }
    }
}
