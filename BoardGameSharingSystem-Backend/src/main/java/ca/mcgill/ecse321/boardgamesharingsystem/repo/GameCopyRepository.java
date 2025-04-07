package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;

public interface GameCopyRepository extends CrudRepository<GameCopy, Integer> {

    public GameCopy findGameCopyById(int id);
    public List<GameCopy> findByGameId(int gameId);
    public List<GameCopy> findByOwnerId(int ownerId);
    public List<GameCopy> findByGameIdAndOwnerId(int gameId, int ownerId);
    public void deleteByOwnerId(int ownerId);
}
