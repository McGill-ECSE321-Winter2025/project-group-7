package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;

public interface GameOwnerRepository extends CrudRepository<GameOwner, Integer>{
    public GameOwner findGameOwnerById(int id);
    
}
