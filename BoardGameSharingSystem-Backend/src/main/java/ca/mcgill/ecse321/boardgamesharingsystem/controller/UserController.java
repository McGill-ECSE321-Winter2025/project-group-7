package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
public class UserController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountResponseDto createUserAccount(@RequestBody AuthRequest userRequest)
    {
        return new UserAccountResponseDto(accountService.createUserAccount(userRequest));
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto findUserAccount(@PathVariable int id)
    {
        return new UserAccountResponseDto(accountService.findUserAccountById(id));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto deleteUserAccount(@PathVariable int id)
    {
        return new UserAccountResponseDto(accountService.deleteUserAccount(id));
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto login(@RequestBody AuthRequest userRequest)
    {
        return new UserAccountResponseDto(accountService.login(userRequest));
    }

    @PutMapping("/users/{id}/toPlayer")
    @ResponseStatus(HttpStatus.OK)
    public UserAccountResponseDto toggleUserToPlayer(@PathVariable int id) 
    {
        return new UserAccountResponseDto(accountService.toggleUserToPlayer(id));
    }

    @PutMapping("/users/{id}/toGameOwner")
    @ResponseStatus(HttpStatus.OK)
    public GameOwnerResponseDto toggleUserToGameOwner(@PathVariable int id)
    {
        return new GameOwnerResponseDto(accountService.toggleUserToGameOwner(id));
    }
}
