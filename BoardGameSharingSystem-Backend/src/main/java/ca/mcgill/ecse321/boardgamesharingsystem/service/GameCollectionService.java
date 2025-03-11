package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameRequestDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import jakarta.validation.Valid;

@Service
@Validated
public class GameCollectionService {
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;

    /**
     * 
     * @param gameId the id of the game
     * @return the game with the id
     */
    public Game findGameById(int gameId) {
        Game game = gameRepo.findGameById(gameId);

        if (game == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,
                    String.format("Could not find a game with id %d", gameId));
        }
        return game;

    }

    /**
     * 
     * @return the list of all games in the system
     */
    public List<Game> findAllGames() {
        List<Game> games = gameRepo.findAll();
        if (games == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the list of games");
        }
        return games;
    }

    /**
     * 
     * @param gameId the id of the game
     * @return the list of game copies for the game with the id
     */
    public List<GameCopy> findGameCopiesFromGame(int gameId) {
        List<GameCopy> gameCopies = gameCopyRepo.findByGameId(gameId);
        if (gameCopies == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the list of game copies for game with id %d", gameId));
        }
        return gameCopies;
    }

    /**
     * 
     * @param gameToCreate the request containing information about the game to
     *                     create
     * @return the game that was created
     */
    @Transactional
    public Game createGame(@Valid GameRequestDto gameToCreate) {
        int minNumPlayers = gameToCreate.getMinNumPlayers();
        int maxNumPlayers = gameToCreate.getMaxNumPlayers();
        if (maxNumPlayers < minNumPlayers) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format(
                    "The minNumPlayers %d is greater than the maxNumPlayers %d ", minNumPlayers, maxNumPlayers));
        }
        Game game = new Game(
                gameToCreate.getTitle(),
                gameToCreate.getMinNumPlayers(),
                gameToCreate.getMaxNumPlayers(),
                gameToCreate.getPictureURL(),
                gameToCreate.getDescription());
        return gameRepo.save(game);
    }

    /**
     * 
     * @param gameId       the id of the game
     * @param gameToUpdate the request for updating the game
     * @return the updated game
     */
    @Transactional
    public Game updateGame(int gameId, @Valid GameRequestDto gameToUpdate) {
        Game game = gameRepo.findGameById(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not update game because a game with id %d does not exist ", gameId));
        }
        int minNumPlayers = gameToUpdate.getMinNumPlayers();
        int maxNumPlayers = gameToUpdate.getMaxNumPlayers();
        if (minNumPlayers > maxNumPlayers) {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, String.format(
                    "The minNumPlayers %d is greater than the maxNumPlayers %d ", minNumPlayers, maxNumPlayers));
        }
        game.setTitle(gameToUpdate.getTitle());
        game.setMaxNumPlayers(maxNumPlayers);
        game.setMinNumPlayers(minNumPlayers);
        game.setDescription(gameToUpdate.getDescription());
        game.setPictureURL(gameToUpdate.getPictureURL());
        return gameRepo.save(game);
    }
}
