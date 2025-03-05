package ca.mcgill.ecse321.boardgamesharingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.UserAccountCreationDTO;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@Service
@Validated
public class AccountService {
    @Autowired
    private UserAccountRepository userRepo;

    @Transactional
    public UserAccount findUserAccountById(int UserAccountId) {
        UserAccount userAccount = userRepo.findUserAccountById(UserAccountId);

        if (userAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No userAccount has id %d", UserAccountId));
        }

        return userAccount;
    }

    // no @Valid in param because jakarta.validation.Valid import isnt working
    @Transactional
    public UserAccount createUserAccount( UserAccountCreationDTO userAccountToCreate) {
        
        UserAccount userAccount = new UserAccount(
            userAccountToCreate.getName(), 
            userAccountToCreate.getEmail(),
            userAccountToCreate.getPassword());

        return userRepo.save(userAccount);
    }

    @Transactional
    public UserAccount deleteUserAccount(int UserAccountId){
        return null;
    }

    // public UserAccount login(???) {
    //     return null;
    // }

    public boolean toggleUserToPlayer(int UserAccountId) {
        return false;
    }

    public boolean toggleUserToGameOwner (int UserAccountId) {
        return false;
    }
}
