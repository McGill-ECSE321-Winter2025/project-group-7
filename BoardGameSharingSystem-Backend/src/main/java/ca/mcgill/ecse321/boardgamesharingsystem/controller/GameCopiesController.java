package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.service.GameCollectionService;
import ca.mcgill.ecse321.boardgamesharingsystem.service.GameOwningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("gameCopies")
public class GameCopiesController {

    @Autowired
    private GameOwningService gameOwningService;
    @Autowired
    private GameCollectionService gameCollectionService;

    /**
     * Adds a GameCopy for a specific GameOwner and Game.
     * @param gameOwnerId the ID of the GameOwner
     * @param gameId the ID of the Game
     * @return the created GameCopy
     */
    @PostMapping("/{gameOwnerId}/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GameCopyResponseDto addGameCopyToGameOwner(@PathVariable int gameOwnerId, @PathVariable int gameId) {
        return new GameCopyResponseDto(gameOwningService.addGameCopyToGameOwner(gameOwnerId, gameId));
    }

    /**
     * Removes a GameCopy.
     * @param gameCopyId the ID of the GameCopy
     * @return the removed GameCopy
     */
    @DeleteMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto removeGameCopyFromGameOwner(@PathVariable int gameCopyId) {
        GameCopy deletedGameCopy = gameOwningService.removeGameCopyFromGameOwner(gameCopyId);
        return new GameCopyResponseDto(deletedGameCopy);
    }


    /**
     * Retrieves a list of GameCopies, either by GameOwner or by Game.
     * @param gameOwnerId (optional) the ID of the GameOwner
     * @param gameId (optional) the ID of the Game
     * @return a list of GameCopies matching the criteria
     */
    @GetMapping("/")
    public List<GameCopyResponseDto> findGameCopies(@RequestParam(value="gameOwnerId", required=false) Integer gameOwnerId,
                                                    @RequestParam(value="gameId", required=false) Integer gameId) {
        List<GameCopyResponseDto> res = new ArrayList<>();
        if (gameOwnerId != null) {
            return findGameCopiesForGameOwner(gameOwnerId);
        } else if (gameId != null) {
            return findGameCopiesFromGame(gameId);
        }
        return res;
    }

    /**
     * Finds all GameCopies owned by a specific GameOwner.
     * @param gameOwnerId the ID of the GameOwner
     * @return list of GameCopies
     */
    private List<GameCopyResponseDto> findGameCopiesForGameOwner(int gameOwnerId) {
        List<GameCopy> gameCopies = gameOwningService.findGameCopiesForGameOwner(gameOwnerId);
        List<GameCopyResponseDto> responses = new ArrayList<>();
        gameCopies.forEach(copy -> responses.add(new GameCopyResponseDto(copy)));
        return responses;
    }

    /**
     * Finds all GameCopies of a specific Game.
     * @param gameId the ID of the Game
     * @return list of GameCopies
     */
    private List<GameCopyResponseDto> findGameCopiesFromGame(int gameId) {
        List<GameCopy> gameCopies = gameCollectionService.findGameCopiesFromGame(gameId);
        List<GameCopyResponseDto> responses = new ArrayList<>();
        gameCopies.forEach(copy -> responses.add(new GameCopyResponseDto(copy)));
        return responses;
    }
}
