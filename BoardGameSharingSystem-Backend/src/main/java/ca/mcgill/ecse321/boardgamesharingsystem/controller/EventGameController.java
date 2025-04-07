package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.service.EventService;

@CrossOrigin(origins="http://localhost:8090")
@RestController
public class EventGameController {
    @Autowired
    private EventService eventService;
    
    /**
     * Adds a Game to an Event by creating an EventGame.
     * @param eventId the Id of the Event to add a Game to
     * @param gameId the Id of the Game to add to the Event
     * @return the EventGame created, including a timestamp of the response
     */
    @PutMapping("/eventGames/{eventId}/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EventGameResponseDto addGameToEvent(@PathVariable("eventId") int eventId, @PathVariable("gameId") int gameId)
    {
        return new EventGameResponseDto(eventService.addGameToEvent(new EventGameDto(eventId, gameId)));
    }

    /**
     * Removes a Game from an Event.
     * @param eventId the Id of the Event to remove a Game from
     * @param gameId the Id of the Game to remove from the event
     * @return the EventGame deleted, including a timestamp of the response
     */
    @DeleteMapping("/eventGames/{eventId}/{gameId}")
    public EventGameResponseDto removeGameFromEvent(@PathVariable("eventId") int eventId, @PathVariable("gameId") int gameId)
    {
        return new EventGameResponseDto(eventService.removeGameFromEvent(new EventGameDto(eventId, gameId)));
    }

    /**
     * Returns a list of all EventGames associated to an Event.
     * @param eventId the Event associated to the EventGames
     * @return the list of found EventGames
     */
    @GetMapping("/eventGames/fromEvent")
    public List<EventGameResponseDto> findEventGamesbyEvent(@RequestParam("eventId") int eventId)
    {
        List<EventGame> eventGamesFound = eventService.findEventGamesByEvent(eventId);
        List<EventGameResponseDto> responses = new ArrayList<>();
        eventGamesFound.forEach(eventGame -> responses.add(new EventGameResponseDto(eventGame)));
        return responses;
    }

    /**
     * Returns a list of all EventGames associated to a Game.
     * @param gameId the Game associated to the EventGames
     * @return the list of found EventGames
     */
    @GetMapping("/eventGames/fromGame")
    public List<EventGameResponseDto> findEventGamesbyGame(@RequestParam("gameId") int gameId)
    {
        List<EventGame> eventGamesFound = eventService.findEventGamesByGame(gameId);
        List<EventGameResponseDto> responses = new ArrayList<>();
        eventGamesFound.forEach(eventGame -> responses.add(new EventGameResponseDto(eventGame)));
        return responses;
    }
}
