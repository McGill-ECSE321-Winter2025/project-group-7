package ca.mcgill.ecse321.boardgamesharingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
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
     * Finds all GameCopies associated with a specific Game.
     * @param gameId the ID of the Game
     * @return a list of GameCopies
     */
    @Transactional(readOnly = true)
    public List<GameCopy> findGameCopiesFromGame(int gameId) {
        return gameCopyRepo.findByGameId(gameId);
    }

    /**
     * Create a GameOwner from an existing UserAccount
     */
    @Transactional
    public GameOwner createGameOwner(int userId) {
        // Retrieve the UserAccount using the provided userId
        UserAccount user = userAccountRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserAccount not found"));

        // Check if the user is already a GameOwner
        if (gameOwnerRepo.findGameOwnerById(userId) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a GameOwner");
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
        return gameOwnerRepo.findGameOwnerById(userId);
    }

    /**
     * Add a GameCopy to a GameOwner
     */
    @Transactional
    public GameCopy addGameCopyToGameOwner(int gameOwnerId, int gameId) {
        // Find the GameOwner
        GameOwner gameOwner = gameOwnerRepo.findGameOwnerById(gameOwnerId);
        if (gameOwner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameOwner not found");
        }

        // Find the Game
        Game game = gameRepo.findGameById(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if the GameCopy already exists
        List<GameCopy> existingCopies = gameCopyRepo.findByGameIdAndOwnerId(gameId, gameOwnerId);
        if (!existingCopies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GameCopy already exists for this owner");
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
                .orElseThrow(() -> new IllegalArgumentException("GameCopy not found with ID: " + gameCopyId));

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameOwner not found");
        }

        List<GameCopy> copies = gameCopyRepo.findByOwnerId(gameOwnerId);
        if (copies == null || copies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No GameCopies found for this owner");
        }

        return copies;
    }
}
