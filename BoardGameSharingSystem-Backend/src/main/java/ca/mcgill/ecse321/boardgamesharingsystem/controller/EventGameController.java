package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.service.EventService;

@RestController
public class EventGameController {
    @Autowired
    private EventService eventService;
    
    /**
     * Adds a Game to an Event by creating an EventGame
     * @param eventId the Id of the Event to add a Game to
     * @param gameId the Id of the Game to add to the Event
     * @return the EventGame created, including a timestamp of the response
     */
    @PostMapping("eventGames/{eventId}/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EventGameResponseDto addGameToEvent(@PathVariable int eventId, @PathVariable int gameId)
    {
        return new EventGameResponseDto(eventService.addGameToEvent(new EventGameDto(eventId, gameId)));
    }

    /**
     * Removes a Game from an Event
     * @param eventId the Id of the Event to remove a Game from
     * @param gameId the Id of the Game to remove from the event
     * @return the EventGame deleted, including a timestamp of the response
     */
    @DeleteMapping("eventGames/{eventId}/{gameId}")
    public EventGameResponseDto removeGameFromEvent(@PathVariable int eventId, @PathVariable int gameId)
    {
        return new EventGameResponseDto(eventService.removeGameFromEvent(new EventGameDto(eventId, gameId)));
    }

    /**
     * Returns a list of all EventGames associated to an Event or Game, depending on the requestParam.
     * Event will take precedence over Game, and if neither is present, an empty list is returned.
     * @param eventId the optional Event associated to the EventGames
     * @param eventId the optional Game associated to the EventGames
     * @return the list of found EventGames
     */
    @GetMapping("eventGames")
    private List<EventGameResponseDto> findEventGamesby(@RequestParam(value="eventId", required=false) Integer eventId, @RequestParam(value="gameId", required=false) Integer gameId)
    {
        List<EventGameResponseDto> res = new ArrayList<>();
        if(eventId != null)
        {
            return findEventGamesbyEvent(eventId);
        }
        else if(gameId != null)
        {
            return findEventGamesbyGame(gameId);
        }
        return res;
    }

    /**
     * Returns a list of all EventGames associated to an Event
     * @param eventId the Event associated to the EventGames
     * @return the list of found EventGames
     */
    private List<EventGameResponseDto> findEventGamesbyEvent(int eventId)
    {
        List<EventGame> eventGamesFound = eventService.findEventGamesByEvent(eventId);
        List<EventGameResponseDto> responses = new ArrayList<>();
        eventGamesFound.forEach(eventGame -> responses.add(new EventGameResponseDto(eventGame)));
        return responses;
    }

    /**
     * Returns a list of all EventGames associated to a Game
     * @param gameId the Game associated to the EventGames
     * @return the list of found EventGames
     */
    private List<EventGameResponseDto> findEventGamesbyGame(int gameId)
    {
        List<EventGame> eventGamesFound = eventService.findEventGamesByGame(gameId);
        List<EventGameResponseDto> responses = new ArrayList<>();
        eventGamesFound.forEach(eventGame -> responses.add(new EventGameResponseDto(eventGame)));
        return responses;
    }
}
