package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.service.EventService;

@RestController
public class EventController {
    @Autowired
    private EventService eventService;
    
    /**
     * Create a new Event
     * @param event the Event to create
     * @return the created Event including their generated id and the response's timestamp
     */
    @PostMapping("events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@RequestBody EventDto event)
    {
        return new EventResponseDto(eventService.createEvent(event));
    }

    /**
     * Returns a list of all Events
     * @return a list of all Events
     */
    @GetMapping("events")
    public List<EventResponseDto> findAllEvents()
    {
        List<Event> events = eventService.findAllEvents();
        List<EventResponseDto> responses = new ArrayList<>();
        events.forEach(event -> responses.add(new EventResponseDto(event)));
        return responses;
    }

    /**
     * Updates the Event with ID eventID using the information in event
     * @param id the ID of the event to update
     * @param event the informationt to update to
     * @return the updated Event including id and response timestamp
     */
    @PutMapping("events/{id}")
    public EventResponseDto updateEvent(@PathVariable int id, @RequestBody EventDto event)
    {
        return new EventResponseDto(eventService.updateEvent(id, event));
    }

    /**
     * Deletes the Event with ID eventID
     * @param id the ID of the event to delete
     * @return the deleted Event including id and response timestamp
     */
    @DeleteMapping("events/{id}")
    public EventResponseDto deleteEvent(@PathVariable int id)
    {
        return new EventResponseDto(eventService.deleteEvent(id));
    }
}
