package ca.mcgill.ecse321.boardgamesharingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameUpdateDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;

@Service
@Validated
public class GameCollectionService {
    @Autowired
    private GameRepository gameRepo;

    //Still in progress -- not tested
    public Game findGameById(int gameId){
        Game game = gameRepo.findGameById(gameId);

        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No game has id %d", gameId));
        }

        else{
            return game;
        }
        
    }

    public Game findAllGames(){
        return null;
    }

    public Game findGameCopiesFromGame(int gameId){
        return null;
    }

    //Still in progress -- not tested
    @Transactional
    public Game createGame(GameCreationDto gameToCreate){
        Game game = new Game(
				gameToCreate.getTitle(),
				gameToCreate.getMaxNumPlayers(),
				gameToCreate.getMinNumPlayers(),
                gameToCreate.getPictureURL(),
                gameToCreate.getDescription());
		return gameRepo.save(game);
    }

    @Transactional
    public Game deleteGame(int gameId){
        return null;
    }

    @Transactional
    public Game updateGame(GameUpdateDto gameToUpdate){
        return null;
    }
}
