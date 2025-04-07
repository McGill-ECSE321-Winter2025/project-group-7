package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.service.GameCollectionService;
import ca.mcgill.ecse321.boardgamesharingsystem.service.GameOwningService;

@CrossOrigin(origins="http://localhost:8090")
@RestController
@RequestMapping("gameCopies")
public class GameCopyController {
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
    public GameCopyResponseDto addGameCopyToGameOwner(@PathVariable("gameOwnerId") int gameOwnerId, @PathVariable("gameId") int gameId) {
        return new GameCopyResponseDto(gameOwningService.addGameCopyToGameOwner(gameOwnerId, gameId));
    }

    /**
     * Removes a GameCopy.
     * @param gameCopyId the ID of the GameCopy
     * @return the removed GameCopy
     */
    @DeleteMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto removeGameCopyFromGameOwner(@PathVariable("gameCopyId") int gameCopyId) {
        GameCopy deletedGameCopy = gameOwningService.removeGameCopyFromGameOwner(gameCopyId);
        return new GameCopyResponseDto(deletedGameCopy);
    }

    /**
     * Finds all GameCopies owned by a specific GameOwner.
     * @param gameOwnerId the ID of the GameOwner
     * @return list of GameCopies
     */
    @GetMapping("/forOwner")
    public List<GameCopyResponseDto> findGameCopiesForGameOwner(@RequestParam("gameOwnerId") int gameOwnerId) {
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
    @GetMapping("/forGame")
    public List<GameCopyResponseDto> findGameCopiesFromGame(@RequestParam("gameId") int gameId) {
        List<GameCopy> gameCopies = gameCollectionService.findGameCopiesFromGame(gameId);
        List<GameCopyResponseDto> responses = new ArrayList<>();
        gameCopies.forEach(copy -> responses.add(new GameCopyResponseDto(copy)));
        return responses;
    }
}
