package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.AuthRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameOwnerResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.UserAccountResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.service.AccountService;

@CrossOrigin(origins="http://localhost:8090")
@RestController
public class UserController {
    @Autowired
    private AccountService accountService;

    /** 
     * Creates a new UserAccount
     * @param userRequest contains the username, email and password of the UserAccount
     * @return the created UserAccount
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountResponseDto createUserAccount(@RequestBody AuthRequest userRequest)
    {
        return new UserAccountResponseDto(accountService.createUserAccount(userRequest));
    }

    /** 
     * Retrieves an existing UserAccount
     * @param id the ID of the UserAccount to be retrieved
     * @return the UserAccount retrieved
     */
    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto findUserAccount(@PathVariable("id") int id)
    {
        return new UserAccountResponseDto(accountService.findUserAccountById(id));
    }

    /** 
     * Deletes an existing UserAccount
     * @param id the ID of the UserAccount to be deleted
     * @return the deleted UserAccount
     */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto deleteUserAccount(@PathVariable("id") int id)
    {
        return new UserAccountResponseDto(accountService.deleteUserAccount(id));
    }

    /** 
     * Verifies an existing UserAccount
     * @param userRequest contains the username, email and password of the UserAccount
     * @return the verified UserAccount
     */
    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto login(@RequestBody AuthRequest userRequest)
    {
        return new UserAccountResponseDto(accountService.login(userRequest));
    }
    
    /** 
     * Toggles a UserAccount from GameOwner to Player
     * @param id the ID of the UserAccount to be toggled
     * @return the toggled UserAccount
     */
    @PutMapping("/users/{id}/toPlayer")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto toggleUserToPlayer(@PathVariable("id") int id) 
    {
        return new UserAccountResponseDto(accountService.toggleUserToPlayer(id));
    }

    /** 
     * Toggles a UserAccount from Player to GameOwner
     * @param id the ID of the UserAccount to be toggled
     * @return the newly toggled GameOwner
     */
    @PutMapping("/users/{id}/toGameOwner")
    @ResponseStatus(HttpStatus.OK)
    public GameOwnerResponseDto toggleUserToGameOwner(@PathVariable("id") int id)
    {
        return new GameOwnerResponseDto(accountService.toggleUserToGameOwner(id));
    }
}
