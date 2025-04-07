package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration.RegistrationKey;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.EventGameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.EventRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import jakarta.validation.Valid;

/**
 * This service class implements functionalities related to the Events,
 * including creating, updating, deleting, and retrieving events; 
 * adding, removing, and retrieving event games; 
 * and creating, deleting, and retrieving registrations to events.
 */
@Service
@Validated
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private EventGameRepository eventGameRepository;

    /**
     * Searches the database for a UserAccount given an ID and returns it if found
     * @param userID the ID of the requested UserAccount
     * @return the UserAccount if found
     */
    public UserAccount findUserAccountById(int userID)
    {
        UserAccount user = userAccountRepository.findUserAccountById(userID);
        if(user == null) 
        {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("no userAccount found with ID %d", userID));
        }
        return user;
    }

    /**
     * Creates an Event given a Valid EventDto, saves it, and returns the saved Event
     * @param eventToCreate Valid EventDto
     * @return the Event if created
     */
    @Transactional
    public Event createEvent(@Valid EventDto eventToCreate)
    {
        UserAccount creator = findUserAccountById(eventToCreate.getCreatorId());
        validateEventTimes(eventToCreate);
        Event event = new Event(
            Date.valueOf(eventToCreate.getStartDate()),
            Time.valueOf(eventToCreate.getStartTime()),
            Date.valueOf(eventToCreate.getEndDate()),
            Time.valueOf(eventToCreate.getEndTime()),
            eventToCreate.getMaxNumParticipants(),
            eventToCreate.getLocation(),
            eventToCreate.getDescription(),
            eventToCreate.getContactEmail(),
            creator
            );
        return eventRepository.save(event);
    }

    /**
     * Searches the database for an Event, updates it with given attributes, and returns it
     * @param eventID the ID of the Event to update
     * @param updatedEventDetails the attributes to update to
     * @return the updated Event
     */
    @Transactional
    public Event updateEvent(int eventID, @Valid EventDto updatedEventDetails)
    {
        Event eventToUpdate = findEventById(eventID);
        validateEventTimes(updatedEventDetails);
        eventToUpdate.setStartDate(Date.valueOf(updatedEventDetails.getStartDate()));
        eventToUpdate.setStartTime(Time.valueOf(updatedEventDetails.getStartTime()));
        eventToUpdate.setEndDate(Date.valueOf(updatedEventDetails.getEndDate()));
        eventToUpdate.setEndTime(Time.valueOf(updatedEventDetails.getEndTime()));
        eventToUpdate.setMaxNumParticipants(updatedEventDetails.getMaxNumParticipants());
        eventToUpdate.setLocation(updatedEventDetails.getLocation());
        eventToUpdate.setDescription(updatedEventDetails.getDescription());
        eventToUpdate.setContactEmail(updatedEventDetails.getContactEmail());
        return eventRepository.save(eventToUpdate);
    }

    /**
     * Returns all Events in the database as a List
     * @return Events found as List<Event>
     */
    public List<Event> findAllEvents()
    {
        List<Event> events = eventRepository.findAll();
        return events;
    }

    /**
     * Searches the database for an Event given an ID, returns it if found
     * @param eventID the ID of the Event
     * @return the Event if found
     */
    public Event findEventById(int eventID)
    {
        Event event = eventRepository.findEventById(eventID);
        if(event == null)
        {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("no event found with ID %d", eventID));
        }
        return event;
    }

    /**
     * Searches the database for an Event, deletes it from the database if found.
     * @param eventID the ID of the Event
     * @return the Event deleted if found
     */
    @Transactional
    public Event deleteEvent(int eventID)
    {
        Event eventToDelete = findEventById(eventID);
        registrationRepository.deleteByKey_Event(eventToDelete);
        eventGameRepository.deleteByKey_Event(eventToDelete);
        eventRepository.delete(eventToDelete);
        return eventToDelete;
    }

    /**
     * Searches the database for a Game given an ID, returns the Game if found
     * @param gameID the ID of the Game
     * @return the Game if found
     */
    Game findGameById(int gameID)
    {
        Game game = gameRepository.findGameById(gameID);
        if(game == null)
        {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("no game found with ID %d", gameID));
        }
        return game;
    }

    /**
     * Searches the databse for the Event and Game specified by the EventGameDto, creates new EventGame, saves it, and returns it if found
     * @param eventGameToAdd the EventGameDto specifying the Event and Game
     * @return the EventGame added if found
     */
    @Transactional
    public EventGame addGameToEvent(EventGameDto eventGameToAdd)
    {
        Event event = findEventById(eventGameToAdd.getEventId());
        Game game = findGameById(eventGameToAdd.getGameId());
        EventGame eventGameDB = eventGameRepository.findEventGameByKey(new EventGame.Key(event,game));
        if (eventGameDB != null){
            throw new BoardGameSharingSystemException(
                HttpStatus.BAD_REQUEST,
                String.format("Cannot add game %d to event %d since it is already added",
                eventGameToAdd.getGameId(),
                eventGameToAdd.getEventId()));
        }
        EventGame eventGame = new EventGame(new EventGame.Key(event, game));
        return eventGameRepository.save(eventGame);
    }

    /**
     * Searches the database for the Event and Game specified by the EventGameDto, deletes it from the database and returns it if found
     * @param eventGameToRemove the EventGameDto specifying the Event and Game
     * @return the deleted EventGame if found
     */
    @Transactional
    public EventGame removeGameFromEvent(EventGameDto eventGameToRemove)
    {
        Event event = findEventById(eventGameToRemove.getEventId());     
        Game game = findGameById(eventGameToRemove.getGameId());      
        EventGame eventGame = eventGameRepository.findEventGameByKey(new EventGame.Key(event, game));
        if(eventGame == null)
        {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("no event game found with gameId %d and eventId %d",
            eventGameToRemove.getGameId(),
            eventGameToRemove.getEventId()));
        }
        eventGameRepository.delete(eventGame);
        return eventGame;
    }

    /**
     * Searches the database for all EventGames associated to an Event, using a given ID
     * @param eventID the ID of the Event
     * @return a list of all EventGames found
     */
    public List<EventGame> findEventGamesByEvent(int eventID)
    {
        Event event = findEventById(eventID);
        List<EventGame> res = new ArrayList<>();
        Iterable<EventGame> iterableEventGames = eventGameRepository.findByKey_Event(event);
        iterableEventGames.forEach(res::add);
        return res;
    }

    /**
     * Searches the database for all EventGames associated to a Game, using a given ID
     * @param gameID the ID of the Game
     * @return a list of all EventGames found
     */
    public List<EventGame> findEventGamesByGame(int gameID)
    {
        Game game = findGameById(gameID);       
        List<EventGame> res = new ArrayList<>();
        Iterable<EventGame> iterableEventGames = eventGameRepository.findByKey_GamePlayed(game);
        iterableEventGames.forEach(res::add);
        return res;
    }

    /**
     * Searches the database for the Registration associated to an Event and a UserAccount, given the UserAccount's ID and the Event's ID
     * @param userID the UserAccount's ID
     * @param eventID the Event's ID
     * @return the Registration if found
     */
    public Registration findRegistrationByEventAndParticipant(int userID, int eventID)
    {
        Event event = findEventById(eventID);        
        UserAccount user = findUserAccountById(userID);
        Registration registration = registrationRepository.findRegistrationByKey(new Registration.RegistrationKey(user, event));
        if(registration == null)
        {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, String.format("no registration found with userId %d and eventId %d",
            userID,
            eventID));
        }
        return registration;
    }

    /**
     * Searches the database for all Registrations associated to an Event, using a given ID 
     * @param eventID the ID of the Event
     * @return a list of all Registrations found
     */
    public List<Registration> findRegistrationsByEvent(int eventID)
    {
        Event event = findEventById(eventID);       
        List<Registration> res = new ArrayList<>();
        Iterable<Registration> iterableRegistrations = registrationRepository.findByKey_Event(event);
        iterableRegistrations.forEach(res::add);
        return res;
    }

    /**
     * Searches the database for all Registrations associated to a UserAccount, using a given ID 
     * @param userId the ID of the UserAccount
     * @return a list of all Registrations found
     */
    public List<Registration> findRegistrationsByParticipant(int userId)
    {
        UserAccount user = findUserAccountById(userId);
        List<Registration> res = new ArrayList<>();
        Iterable<Registration> iterableRegistrations = registrationRepository.findByKey_Participant(user);
        iterableRegistrations.forEach(res::add);
        return res;
    }

    /**
     * Searches the database for a given UserAccount and Event using the given UserAccount's ID and registers the UserAccount to the Event if found,
     * the Event has not reached maximum capacity, and the Event has not ended. Then returns the Registration
     * @param userID the UserAccount's ID
     * @param eventID the Event's ID
     * @return the Registration created if the UserAccount is registered to the Event
     */
    @Transactional
    public Registration registerUserToEvent(int userID, int eventID)
    {
        Event event = findEventById(eventID);    
        UserAccount user = findUserAccountById(userID);
        Registration registration = registrationRepository.findRegistrationByKey(new RegistrationKey(user, event));
        if (registration != null){
            throw new BoardGameSharingSystemException(
                HttpStatus.BAD_REQUEST,
                String.format(
                "Registration already exists for user with id %d and event id %d",
                userID,
                eventID));
        }              
        if(event.getMaxNumParticipants() <= findRegistrationsByEvent(eventID).size())
        {
            throw new BoardGameSharingSystemException(HttpStatus.EXPECTATION_FAILED, String.format("event %d is already at maximum capacity", eventID));
        }
        if(event.getEndDate().before(Date.valueOf(LocalDate.now())) || 
        (event.getEndDate().equals(Date.valueOf(LocalDate.now())) && !event.getEndTime().after(Time.valueOf(LocalTime.now()))))
        {
            throw new BoardGameSharingSystemException(HttpStatus.EXPECTATION_FAILED, String.format("event %d has already ended", eventID));
        }
        Registration newRegistration = new Registration(new Registration.RegistrationKey(user, event), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        return registrationRepository.save(newRegistration);
    }

    /**
     * Searches the database for a Registration associated with the UserAccount with the given ID and the Event with the given ID
     * Deregisters the UserAccount from the Event if found, by deleting the Registration from the database
     * @param userID the ID of the UserAccount
     * @param eventID the ID of the Event
     * @return the Registration deleted if found
     */
    @Transactional
    public Registration deregisterParticipantFromEvent(int userID, int eventID)
    {
        Registration registrationtoDelete = findRegistrationByEventAndParticipant(userID, eventID);
        registrationRepository.delete(registrationtoDelete);
        return registrationtoDelete;
    }

    /**
     * Helper method that throws an exception if an EventDto's start Date and Time are after it's end Date and Time
     * @param event the EventDto to verify
     */
    private void validateEventTimes(@Valid EventDto event)
    {
        if(event.getStartDate().isAfter(event.getEndDate()) || 
        (event.getStartDate().equals(event.getEndDate()) && !event.getStartTime().isBefore(event.getEndTime())))
        {
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Start Date and Time must be before End Date and Time");
        }
    }
}
