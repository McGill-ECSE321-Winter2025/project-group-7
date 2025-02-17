package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;

public interface GameCopyRepository extends CrudRepository<GameCopy, Integer>{
    public GameCopy findGameCopyById(int id);
}
