package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;

public interface GameRepository extends CrudRepository<Game,Integer> {
    public Game findGameById(int id);
    public List<Game> findAll();
}
