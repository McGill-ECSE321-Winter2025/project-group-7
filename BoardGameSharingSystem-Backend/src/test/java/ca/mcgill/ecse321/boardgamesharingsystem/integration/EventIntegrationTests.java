package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.RegistrationResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.EventGameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.EventRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EventIntegrationTests {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private EventGameRepository eventGameRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    
    private static final String NAME = "Mike";
    private static final String EMAIL = "mike@mikemail.com";
    private static final String PASSWORD = "mikePassword";

    
    private static final Date VALID_START_DATE = Date.valueOf("2026-02-11");
    private static final Time VALID_START_TIME = Time.valueOf("11:00:00");
    private static final Date VALID_END_DATE = Date.valueOf("2026-02-12");
    private static final Time VALID_END_TIME = Time.valueOf("11:00:00");
    private static final int VALID_MAX_NUM_PARTICIPANTS = 2;
    private static final String VALID_LOCATION = "Stewart Bio N2/2";
    private static final String VALID_DESCRIPTION = "Come Hangout!";
    private static final String VALID_CONTACT_EMAIL = EMAIL;
    
    private static final String TITLE = "ChessV2";
    private static final int MIN_NUM_PLAYERS = 2;
    private static final int MAX_NUM_PLAYERS = 3;
    private static final String PICTURE_URL = "Chess.image";
    private static final String DESCRIPTION = "Strategy GAme";

    @AfterEach
    public void cleanup()
    {
        registrationRepository.deleteAll();
        eventGameRepository.deleteAll();
        eventRepository.deleteAll();
        gameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }
    @BeforeEach
    public void setup()
    {

    }

    @Test
    @Order(1)
    public void testCreateValidEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        EventDto eventRequest = new EventDto(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user.getId());

        //Act
        ResponseEntity<EventResponseDto> response = client.postForEntity("/events", eventRequest, EventResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        EventResponseDto createEventDto = response.getBody();
        assertNotNull(createEventDto);
        assertEquals(VALID_START_DATE, createEventDto.getStartDate());
        assertEquals(VALID_START_TIME, createEventDto.getStartTime());
        assertEquals(VALID_END_DATE, createEventDto.getEndDate());
        assertEquals(VALID_END_TIME, createEventDto.getEndTime());
        assertEquals(VALID_MAX_NUM_PARTICIPANTS, createEventDto.getMaxNumParticipants());
        assertEquals(VALID_LOCATION, createEventDto.getLocation());
        assertEquals(VALID_DESCRIPTION, createEventDto.getDescription());
        assertEquals(VALID_CONTACT_EMAIL, createEventDto.getContactEmail());
        assertEquals(user.getId(), createEventDto.getCreatorId());
    }

    @Test
    @Order(2)
    public void testCreateEventWithInvalidUserId()
    {
        //Arrange
        EventDto eventRequest = new EventDto(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, -1);

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/events", eventRequest, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
        assertTrue((response.getBody().getErrors().contains("no userAccount found with ID -1")));
    }

    @Test
    @Order(3)
    public void testFindAllEvents()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Event e2 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION + "a", VALID_CONTACT_EMAIL, user);
        e2 = eventRepository.save(e2);
        Event e3 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION + "b", VALID_CONTACT_EMAIL, user);
        e3 = eventRepository.save(e3);
        List<Integer> expectedEvents = Arrays.asList(e1.getId(), e2.getId(), e3.getId());

        EventDto eventRequest = new EventDto(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user.getId());
        
        //Act
        ResponseEntity<List<EventResponseDto>> response = client.exchange(
            "/events",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<EventResponseDto>>() {}
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<EventResponseDto> eventList = response.getBody();
        assertNotNull(eventList);
        List<Integer> eventIdList = new ArrayList<>();
        eventList.forEach(event -> eventIdList.add(event.getId()));
        assertEquals(3, eventList.size());
        for (Integer expected : eventIdList) {
            assertTrue(expectedEvents.contains(expected));
        }
    }

    @Test
    @Order(4)
    public void updateEventWithValidDetails()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        EventDto request = new EventDto(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, 200, "Vancouver", "International Board Game Conference", "IBGC@mail.com", user.getId());

        //Act
        ResponseEntity<EventResponseDto> response = client.exchange(
            "/events/" + e1.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(request),
            EventResponseDto.class);
        
        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EventResponseDto updatedEvent = response.getBody();
        assertNotNull(updatedEvent);
        assertEquals(e1.getId(), updatedEvent.getId());
        assertEquals(VALID_START_DATE, updatedEvent.getStartDate());
        assertEquals(VALID_START_TIME, updatedEvent.getStartTime());
        assertEquals(VALID_END_DATE, updatedEvent.getEndDate());
        assertEquals(VALID_END_TIME, updatedEvent.getEndTime());
        assertEquals(200, updatedEvent.getMaxNumParticipants());
        assertEquals("Vancouver", updatedEvent.getLocation());
        assertEquals("International Board Game Conference", updatedEvent.getDescription());
        assertEquals("IBGC@mail.com", updatedEvent.getContactEmail());
        assertEquals(user.getId(), updatedEvent.getCreatorId());
    }

    @Test
    @Order(5)
    public void updateEventWithInvalidStartEndDate()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        EventDto request = new EventDto(Date.valueOf("2030-10-10"), VALID_START_TIME, Date.valueOf("2030-10-8"), VALID_END_TIME, 200, "Vancouver", "International Board Game Conference", "IBGC@mail.com", user.getId());

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/events/" + e1.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ErrorDto.class);
        
        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertEquals("Start Date and Time must be before End Date and Time", error.getErrors().getFirst());
    }

    @Test
    @Order(6)
    public void testDeleteValidEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);

        //Act
        ResponseEntity<EventResponseDto> response = client.exchange(
            "/events/" + e1.getId(),
            HttpMethod.DELETE,
            null,
            EventResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        EventResponseDto deletedEvent = response.getBody();
        assertNotNull(deletedEvent);
        assertEquals(VALID_START_DATE, deletedEvent.getStartDate());
        assertEquals(VALID_START_TIME, deletedEvent.getStartTime());
        assertEquals(VALID_END_DATE, deletedEvent.getEndDate());
        assertEquals(VALID_END_TIME, deletedEvent.getEndTime());
        assertEquals(VALID_MAX_NUM_PARTICIPANTS, deletedEvent.getMaxNumParticipants());
        assertEquals(VALID_LOCATION, deletedEvent.getLocation());
        assertEquals(VALID_DESCRIPTION, deletedEvent.getDescription());
        assertEquals(VALID_CONTACT_EMAIL, deletedEvent.getContactEmail());
        assertEquals(user.getId(), deletedEvent.getCreatorId());
    }

    @Test
    @Order(7)
    public void testDeleteEventWithInvalidId()
    {
        // Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/events/" + "-1",
            HttpMethod.DELETE,
            null,
            ErrorDto.class
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains("no event found with ID -1"));
    }

    @Test
    @Order(8)
    public void testAddValidGameToEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game = gameRepository.save(game);

        //Act
        ResponseEntity<EventGameResponseDto> response = client.exchange(
            "/eventGames/" + e1.getId() + "/" + game.getId(),
            HttpMethod.PUT,
            null,
            EventGameResponseDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        EventGameResponseDto createdEventGame = response.getBody();
        assertNotNull(createdEventGame);
        assertEquals(e1.getId(), createdEventGame.getEventId());
        assertEquals(e1.getStartDate(), createdEventGame.getEventStartDate());
        assertEquals(e1.getStartTime(), createdEventGame.getEventStartTime());
        assertEquals(e1.getEndDate(), createdEventGame.getEventEndDate());
        assertEquals(e1.getEndTime(), createdEventGame.getEventEndTime());
        assertEquals(e1.getMaxNumParticipants(), createdEventGame.getEventMaxNumParticipants());
        assertEquals(e1.getLocation(), createdEventGame.getEventLocation());
        assertEquals(e1.getDescription(), createdEventGame.getEventDescription());
        assertEquals(e1.getContactEmail(), createdEventGame.getEventContactEmail());
        assertEquals(user.getId(), createdEventGame.getEventCreatorId());

        assertEquals(game.getId(), createdEventGame.getGameId());
        assertEquals(game.getTitle(), createdEventGame.getGameTitle());
        assertEquals(game.getMinNumPlayers(), createdEventGame.getGameMinNumPlayers());
        assertEquals(game.getMaxNumPlayers(), createdEventGame.getGameMaxNumPlayers());
        assertEquals(game.getPictureURL(), createdEventGame.getGamePictureURL());
        assertEquals(game.getDescription(), createdEventGame.getGameDescription());

        assertNotNull(createdEventGame.getResponseDate());
        assertNotNull(createdEventGame.getResponseTime());
    }

    @Test
    @Order(9)
    public void testAddAlreadyAddedGameToEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game = gameRepository.save(game);
        EventGame eventGame = new EventGame(new EventGame.Key(e1, game));
        eventGame = eventGameRepository.save(eventGame);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/eventGames/" + e1.getId() + "/" + game.getId(),
            HttpMethod.PUT,
            null,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("Cannot add game %d to event %d since it is already added", game.getId(), e1.getId())));
    }

    @Test
    @Order(10)
    public void testRemoveValidGameFromEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game = gameRepository.save(game);
        EventGame eventGame = new EventGame(new EventGame.Key(e1, game));
        eventGame = eventGameRepository.save(eventGame);

        // Act
        ResponseEntity<EventGameResponseDto> response = client.exchange(
            "/eventGames/" + e1.getId() + "/" + game.getId(),
            HttpMethod.DELETE,
            null,
            EventGameResponseDto.class
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        EventGameResponseDto deletedEventGame = response.getBody();
        assertNotNull(deletedEventGame);
        assertEquals(e1.getId(), deletedEventGame.getEventId());
        assertEquals(game.getId(), deletedEventGame.getGameId());

        assertEquals(e1.getStartDate(), deletedEventGame.getEventStartDate());
        assertEquals(e1.getStartTime(), deletedEventGame.getEventStartTime());
        assertEquals(e1.getEndDate(), deletedEventGame.getEventEndDate());
        assertEquals(e1.getEndTime(), deletedEventGame.getEventEndTime());
        assertEquals(e1.getMaxNumParticipants(), deletedEventGame.getEventMaxNumParticipants());
        assertEquals(e1.getLocation(), deletedEventGame.getEventLocation());
        assertEquals(e1.getDescription(), deletedEventGame.getEventDescription());
        assertEquals(e1.getContactEmail(), deletedEventGame.getEventContactEmail());
        assertEquals(user.getId(), deletedEventGame.getEventCreatorId());

        assertEquals(game.getTitle(), deletedEventGame.getGameTitle());
        assertEquals(game.getMinNumPlayers(), deletedEventGame.getGameMinNumPlayers());
        assertEquals(game.getMaxNumPlayers(), deletedEventGame.getGameMaxNumPlayers());
        assertEquals(game.getPictureURL(), deletedEventGame.getGamePictureURL());
        assertEquals(game.getDescription(), deletedEventGame.getGameDescription());

        assertNotNull(deletedEventGame.getResponseDate());
        assertNotNull(deletedEventGame.getResponseTime());
    }

    @Test
    @Order(11)
    public void testRemoveInvalidGameFromEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game = gameRepository.save(game);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/eventGames/" + e1.getId() + "/" + game.getId(),
            HttpMethod.DELETE,
            null,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("no event game found with gameId %d and eventId %d", game.getId(), e1.getId())));
    }

    @Test
    @Order(12)
    public void testFindEventGamesbyValidEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Game game1 = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game1 = gameRepository.save(game1);
        Game game2 = new Game(TITLE + "a", MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game2 = gameRepository.save(game2);
        EventGame eventGame1 = new EventGame(new EventGame.Key(e1, game1));
        eventGame1 = eventGameRepository.save(eventGame1);
        EventGame eventGame2 = new EventGame(new EventGame.Key(e1, game2));
        eventGame2 = eventGameRepository.save(eventGame2);

        //Act
        ResponseEntity<List<EventGameResponseDto>> response = client.exchange(
            "/eventGames/fromEvent?eventId={eventId}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<EventGameResponseDto>>() {},
            e1.getId()
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<EventGameResponseDto> eventGameList = response.getBody();
        assertNotNull(eventGameList);
        assertEquals(2, eventGameList.size());
        List<Integer> gameIds = new ArrayList<>();
        eventGameList.forEach(game -> gameIds.add(game.getGameId()));
        assertTrue((gameIds.contains(game1.getId())));
        assertTrue((gameIds.contains(game2.getId())));
    }

    @Test
    @Order(13)
    public void testFindEventGamesbyInvalidEvent()
    {
        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/eventGames/fromEvent?eventId={eventId}",
            HttpMethod.GET,
            null,
            ErrorDto.class,
            -1
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains("no event found with ID -1"));
    }
    
    @Test
    @Order(14)
    public void testFindEventGamesbyValidGame()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Event e2 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION + "a", VALID_DESCRIPTION + "a", VALID_CONTACT_EMAIL + "a", user);
        e2 = eventRepository.save(e2);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        game = gameRepository.save(game);
        EventGame eventGame1 = new EventGame(new EventGame.Key(e1, game));
        eventGame1 = eventGameRepository.save(eventGame1);
        EventGame eventGame2 = new EventGame(new EventGame.Key(e2, game));
        eventGame2 = eventGameRepository.save(eventGame2);

        //Act
        ResponseEntity<List<EventGameResponseDto>> response = client.exchange(
            "/eventGames/fromGame?gameId={gameId}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<EventGameResponseDto>>() {},
            game.getId()
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<EventGameResponseDto> eventGameList = response.getBody();
        assertNotNull(eventGameList);
        assertEquals(2, eventGameList.size());

        List<Integer> eventIds = new ArrayList<>();
        eventGameList.forEach(event -> eventIds.add(event.getEventId()));
        assertTrue((eventIds.contains(e1.getId())));
        assertTrue((eventIds.contains(e2.getId())));
    }

    @Test
    @Order(15)
    public void testFindEventGamesbyInvalidGame()
    {
        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/eventGames/fromGame?gameId={gameId}",
            HttpMethod.GET,
            null,
            ErrorDto.class,
            -1
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains("no game found with ID -1"));
    }

    @Test
    @Order(16)
    public void testRegisterUserToValidEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        
        //Act
        ResponseEntity<RegistrationResponseDto> response = client.exchange(
            "/registrations/" + e1.getId() + "/" + user.getId(),
            HttpMethod.PUT,
            null,
            RegistrationResponseDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        RegistrationResponseDto foundRegistration = response.getBody();
        assertNotNull(foundRegistration);
        assertEquals(user.getId(), foundRegistration.getUserId());
        assertEquals(e1.getId(), foundRegistration.getEventId());
        assertNotNull(foundRegistration.getRegistrationDate());
        assertNotNull(foundRegistration.getRegistrationTime());
        assertNotNull(foundRegistration.getResponseDate());
        assertNotNull(foundRegistration.getResponseTime());

        assertEquals(e1.getStartDate(), foundRegistration.getEventStartDate());
        assertEquals(e1.getStartTime(), foundRegistration.getEventStartTime());
        assertEquals(e1.getEndDate(), foundRegistration.getEventEndDate());
        assertEquals(e1.getEndTime(), foundRegistration.getEventEndTime());
        assertEquals(e1.getMaxNumParticipants(), foundRegistration.getEventMaxNumParticipants());
        assertEquals(e1.getLocation(), foundRegistration.getEventLocation());
        assertEquals(e1.getDescription(), foundRegistration.getEventDescription());
        assertEquals(e1.getContactEmail(), foundRegistration.getEventContactEmail());
        assertEquals(user.getId(), foundRegistration.getEventCreatorId());

        assertEquals(user.getEmail(), foundRegistration.getRegistrantEmail());
        assertEquals(user.getName(), foundRegistration.getRegistrantName());
        assertEquals(user.getPassword(), foundRegistration.getRegistrantPassword());
    }
    
    @Test
    @Order(17)
    public void testRegisterUserToAlreadyRegisteredEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Registration registration = new Registration(new Registration.RegistrationKey(user, e1), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration = registrationRepository.save(registration);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/registrations/" + e1.getId() + "/" + user.getId(),
            HttpMethod.PUT,
            null,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("Registration already exists for user with id %d and event id %d", user.getId(), e1.getId())));
    }

    @Test
    @Order(18)
    public void testFindValidRegistrationByEventAndParticipant()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Registration registration = new Registration(new Registration.RegistrationKey(user, e1), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration = registrationRepository.save(registration);

        //Act
        ResponseEntity<RegistrationResponseDto> response = client.exchange(
            "/registrations/" + e1.getId() + "/" + user.getId(),
            HttpMethod.GET,
            null,
            RegistrationResponseDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RegistrationResponseDto foundRegistration = response.getBody();
        assertNotNull(foundRegistration);
        assertEquals(user.getId(), foundRegistration.getUserId());
        assertEquals(e1.getId(), foundRegistration.getEventId());
        assertNotNull(foundRegistration.getRegistrationDate());
        assertNotNull(foundRegistration.getRegistrationTime());
        assertNotNull(foundRegistration.getResponseDate());
        assertNotNull(foundRegistration.getResponseTime());

        assertEquals(e1.getStartDate(), foundRegistration.getEventStartDate());
        assertEquals(e1.getStartTime(), foundRegistration.getEventStartTime());
        assertEquals(e1.getEndDate(), foundRegistration.getEventEndDate());
        assertEquals(e1.getEndTime(), foundRegistration.getEventEndTime());
        assertEquals(e1.getMaxNumParticipants(), foundRegistration.getEventMaxNumParticipants());
        assertEquals(e1.getLocation(), foundRegistration.getEventLocation());
        assertEquals(e1.getDescription(), foundRegistration.getEventDescription());
        assertEquals(e1.getContactEmail(), foundRegistration.getEventContactEmail());
        assertEquals(user.getId(), foundRegistration.getEventCreatorId());

        assertEquals(user.getEmail(), foundRegistration.getRegistrantEmail());
        assertEquals(user.getName(), foundRegistration.getRegistrantName());
        assertEquals(user.getPassword(), foundRegistration.getRegistrantPassword());
    }

    @Test
    @Order(19)
    public void testFindInvalidRegistrationByEventAndParticipant()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);

        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/registrations/" + e1.getId() + "/" + user.getId(),
            HttpMethod.GET,
            null,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("no registration found with userId %d and eventId %d", user.getId(), e1.getId())), error.getErrors().getFirst());
    }

    @Test
    @Order(20)
    public void testFindRegistrationByValidEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        UserAccount user2 = new UserAccount(NAME+"a", EMAIL+"a", PASSWORD+"a");
        user2 = userAccountRepository.save(user2);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Registration registration = new Registration(new Registration.RegistrationKey(user, e1), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration = registrationRepository.save(registration);
        Registration registration2 = new Registration(new Registration.RegistrationKey(user2, e1), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration2 = registrationRepository.save(registration2);

        //Act
        ResponseEntity<List<RegistrationResponseDto>> response = client.exchange(
            "/registrations/fromEvent?eventId={eventId}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<RegistrationResponseDto>>() {},
            e1.getId()
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<RegistrationResponseDto> registrations = response.getBody();
        assertNotNull(registrations);
        assertEquals(2, registrations.size());
        List<Integer> participantIds = new ArrayList<>();
        registrations.forEach(participant -> participantIds.add(participant.getUserId()));
        assertTrue(participantIds.contains(user.getId()));
        assertTrue(participantIds.contains(user2.getId()));
    }

    @Test
    @Order(21)
    public void testFindRegistrationByInvalidEvent()
    {
        //Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/registrations/fromEvent?eventId={eventId}",
            HttpMethod.GET,
            null,
            ErrorDto.class,
            -1
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("no event found with ID %d", -1)));
    }

    @Test
    @Order(22)
    public void testFindRegistrationByValidParticipant()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Event e2 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION + "a", VALID_CONTACT_EMAIL, user);
        e2 = eventRepository.save(e2);
        Registration registration = new Registration(new Registration.RegistrationKey(user, e1), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration = registrationRepository.save(registration);
        Registration registration2 = new Registration(new Registration.RegistrationKey(user, e2), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration2 = registrationRepository.save(registration2);

        //Act
            ResponseEntity<List<RegistrationResponseDto>> response = client.exchange(
            "/registrations/fromParticipant?participantId={participantId}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<RegistrationResponseDto>>() {},
            user.getId()
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<RegistrationResponseDto> registrations = response.getBody();
        assertNotNull(registrations);
        assertEquals(2, registrations.size());
        List<Integer> eventIds = new ArrayList<>();
        registrations.forEach(event -> eventIds.add(event.getEventId()));
        assertTrue(eventIds.contains(e1.getId()));
        assertTrue(eventIds.contains(e2.getId()));
    }

    @Test
    @Order(23)
    public void testFindRegistrationByInvalidParticipant()
    {
        // Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/registrations/fromParticipant?participantId={participantId}",
            HttpMethod.GET,
            null,
            ErrorDto.class,
            -1
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("no userAccount found with ID %d", -1)));
    }

    @Test
    @Order(24)
    public void testValidDeregisterParticipantFromEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        Registration registration = new Registration(new Registration.RegistrationKey(user, e1), Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()));
        registration = registrationRepository.save(registration);

        // Act
        ResponseEntity<RegistrationResponseDto> response = client.exchange(
            "/registrations/" + e1.getId() + "/" + user.getId(),
            HttpMethod.DELETE,
            null,
            RegistrationResponseDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RegistrationResponseDto foundRegistration = response.getBody();
        assertNotNull(foundRegistration);
        assertEquals(user.getId(), foundRegistration.getUserId());
        assertEquals(e1.getId(), foundRegistration.getEventId());
        assertNotNull(foundRegistration.getRegistrationDate());
        assertNotNull(foundRegistration.getRegistrationTime());
        assertNotNull(foundRegistration.getResponseDate());
        assertNotNull(foundRegistration.getResponseTime());

        assertEquals(e1.getStartDate(), foundRegistration.getEventStartDate());
        assertEquals(e1.getStartTime(), foundRegistration.getEventStartTime());
        assertEquals(e1.getEndDate(), foundRegistration.getEventEndDate());
        assertEquals(e1.getEndTime(), foundRegistration.getEventEndTime());
        assertEquals(e1.getMaxNumParticipants(), foundRegistration.getEventMaxNumParticipants());
        assertEquals(e1.getLocation(), foundRegistration.getEventLocation());
        assertEquals(e1.getDescription(), foundRegistration.getEventDescription());
        assertEquals(e1.getContactEmail(), foundRegistration.getEventContactEmail());
        assertEquals(user.getId(), foundRegistration.getEventCreatorId());

        assertEquals(user.getEmail(), foundRegistration.getRegistrantEmail());
        assertEquals(user.getName(), foundRegistration.getRegistrantName());
        assertEquals(user.getPassword(), foundRegistration.getRegistrantPassword());
    }
    @Test
    @Order(24)
    public void testInvalidDeregisterParticipantFromEvent()
    {
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        user = userAccountRepository.save(user);
        Event e1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        e1 = eventRepository.save(e1);
        // Act
        ResponseEntity<ErrorDto> response = client.exchange(
            "/registrations/" + e1.getId() + "/" + user.getId(),
            HttpMethod.DELETE,
            null,
            ErrorDto.class
        );

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().contains(String.format("no registration found with userId %d and eventId %d", user.getId(), e1.getId())));
    }
}
