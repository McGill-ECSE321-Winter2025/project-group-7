package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameOwnerResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameOwner;
import ca.mcgill.ecse321.boardgamesharingsystem.service.GameOwningService;

@RestController
public class GameOwnerController {
    @Autowired
    GameOwningService gameOwningService;

    /**
     * Create a new game owner.
     * @param id The id of the game owner to create
     * @return The game owner with id, the user id, name, email,(-1, "","" when not present) account type (0 player, 1 game owner)
     */
    @PutMapping("/gameowners/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public GameOwnerResponseDto createGameOwner(@PathVariable int id ){
        GameOwner gameOwner = gameOwningService.createGameOwner(id);
        return new GameOwnerResponseDto(gameOwner);
    }

    /**
     * Find a game owner.
     * @param id The id of the game owner
     * @return The game owner with id, the user id, name, email,(-1, "","" when not present) account type (0 player, 1 game owner)
     */
    @GetMapping("/gameowners/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GameOwnerResponseDto findGameOwner(@PathVariable int id){
        GameOwner gameOwner = gameOwningService.findGameOwner(id);
        return new GameOwnerResponseDto(gameOwner);
    }

}
