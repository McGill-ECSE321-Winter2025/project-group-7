package ca.mcgill.ecse321.boardgamesharingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

import java.util.List;

@Service
public class GameOwningService {

    @Autowired
    private GameOwnerRepository gameOwnerRepo;

    @Autowired
    private UserAccountRepository userAccountRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    /**
     * Create a GameOwner from an existing UserAccount
     */
    @Transactional
    public GameOwner createGameOwner(int userId) {
        // Retrieve the UserAccount using the provided userId
        UserAccount user = userAccountRepo.findById(userId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
                 String.format("Could not create gameOwner since user with id %d does not exist",userId)));

        // Check if the user is already a GameOwner
        if (gameOwnerRepo.findGameOwnerById(userId) != null) {
            throw new BoardGameSharingSystemException(
            HttpStatus.BAD_REQUEST,
             String.format(
                "The user with id %d already has a game owner with the same id created",
                userId));
        }         
        // Create a new GameOwner and persist it
        GameOwner gameOwner = new GameOwner(user);
        return gameOwnerRepo.save(gameOwner);
    }

    /**
     * Find a GameOwner by ID
     */
    @Transactional
    public GameOwner findGameOwner(int userId) {
        GameOwner gameOwner = gameOwnerRepo.findGameOwnerById(userId);
        if (gameOwner==null){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
            String.format("Could not find gameOwner with id %d",userId) );
        }
        return gameOwner;
    }

    /**
     * Add a GameCopy to a GameOwner
     */
    @Transactional
    public GameCopy addGameCopyToGameOwner(int gameOwnerId, int gameId) {
        // Find the GameOwner
        GameOwner gameOwner = gameOwnerRepo.findGameOwnerById(gameOwnerId);
        if (gameOwner == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
            String.format("Could not add game copy to game owner because gameOwner with id %d does not exist",
            gameOwnerId));
        }

        // Find the Game
        Game game = gameRepo.findGameById(gameId);
        if (game == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
            String.format("Could not add game copy to game owner because game with id %d does not exist",
            gameId));
        }

        // Check if the GameCopy already exists
        List<GameCopy> existingCopies = gameCopyRepo.findByGameIdAndOwnerId(gameId, gameOwnerId);
        if (existingCopies== null){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
            String.format("Could not find the list of game copies for game with id %d and gameOwner with id %d",
            gameId,
            gameOwnerId));
        }
        if (!existingCopies.isEmpty()) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST,
             String.format("Could not add game copy for game owner with id %d and game with id %d because it already exists",
              gameOwnerId, 
              gameId));
        }

        // Create and save a new GameCopy
        GameCopy gameCopy = new GameCopy(game, gameOwner);
        return gameCopyRepo.save(gameCopy);
    }

    /**
     * Remove a GameCopy from a GameOwner
     */
    @Transactional
    public GameCopy removeGameCopyFromGameOwner(int gameCopyId) {
        GameCopy gameCopy = gameCopyRepo.findById(gameCopyId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
                 String.format("Could not find game copy with id %d", gameCopyId)));

        GameOwner gameOwner = gameCopy.getGameOwner();
        if (gameOwner == null){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
            String.format("Could not find game owner when trying to remove game copy with id %d",
            gameCopyId));
        }         
        List<GameCopy> gameCopies = gameCopyRepo.findByOwnerId(gameOwner.getId());
        if (gameCopies==null){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, 
            String.format("Could not find gameCopies from game owner with id %d",
            gameOwner.getId()));
        }
        if (gameCopies.size()<=1){
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, 
            String.format("Cannot remove game copy with id %d since game owner with id %d has one game copy remaining",
            gameCopyId, 
            gameOwner.getId()));
        }         
        gameCopyRepo.delete(gameCopy);
        return gameCopy;
    }

    /**
     * Find all GameCopies owned by a GameOwner
     */
    @Transactional
    public List<GameCopy> findGameCopiesForGameOwner(int gameOwnerId) {
        GameOwner gameOwner = gameOwnerRepo.findGameOwnerById(gameOwnerId);
        if (gameOwner == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
             String.format("Could not find game copies since gameOwner with id %d does not exist", 
             gameOwnerId));
        }

        List<GameCopy> copies = gameCopyRepo.findByOwnerId(gameOwnerId);
        if (copies == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, 
            String.format("Could not find game copies for game owner with id %d", gameOwnerId));
        }

        return copies;
    }
}
