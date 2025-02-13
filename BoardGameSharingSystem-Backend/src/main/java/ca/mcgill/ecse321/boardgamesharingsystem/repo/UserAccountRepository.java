package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
    public UserAccount findUserAccountById(int id);
}
