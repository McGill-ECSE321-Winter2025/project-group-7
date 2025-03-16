package ca.mcgill.ecse321.boardgamesharingsystem.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameRequestDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.service.GameCollectionService;

@RestController
public class GameController {
    @Autowired
    private GameCollectionService gameCollectionService;
    
    /** 
     * Creates a new game.
     * @param gameToCreate contains title, minNumPlayers, maxNumPlayers, pictureURL,and description
     * @return the created Game 
     */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponseDto createGame(@RequestBody GameRequestDto gameToCreate)
    {
        return new GameResponseDto(gameCollectionService.createGame(gameToCreate));
    }

    
    /** 
     * Updates an existing game.
     * @param gameId the ID of the game to update
     * @param gameToUpdate contains title, minNumPlayers, maxNumPlayers, pictureURL,and description
     * @return the updated Game 
     */
    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameResponseDto updateGame(@PathVariable int gameId, @RequestBody GameRequestDto gameToUpdate) 
    {
        return new GameResponseDto(gameCollectionService.updateGame(gameId, gameToUpdate));
    }

    
    /** 
     * Retrieves all games of the system.
     * @return list of all Games
     */
    @GetMapping("/games")
    public List<GameResponseDto> findAllGames() 
    {
        List<Game> games = gameCollectionService.findAllGames();
        List<GameResponseDto> responses = new ArrayList<>();
        games.forEach(game -> responses.add(new GameResponseDto(game)));
        return responses;
    }

    
    /** 
     * Find a game.
     * @param gameId the ID of the game
     * @return the game in question
     */
    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameResponseDto findGameById(@PathVariable int gameId) 
    {
        return new GameResponseDto(gameCollectionService.findGameById(gameId));
    }
}
