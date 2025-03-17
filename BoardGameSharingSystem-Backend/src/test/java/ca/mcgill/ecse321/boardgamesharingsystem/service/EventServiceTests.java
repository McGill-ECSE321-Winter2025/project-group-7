package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.EventGameDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.EventGameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.EventRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventServiceTests {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private EventGameRepository eventGameRepository;
    @InjectMocks
    private EventService eventService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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
    
    @Test
    public void testFindUserAccountByValidId()
    {
        //Arrange
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));

        //Act
        UserAccount userAccount = eventService.findUserAccountById(42);

        //Assert
        assertNotNull(userAccount);
		assertEquals(NAME, userAccount.getName());
		assertEquals(EMAIL, userAccount.getEmail());
		assertEquals(PASSWORD, userAccount.getPassword());
    }

    @Test
    public void testFindUserAccountThatDoesntExist()
    {
        //Arrange + Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.findUserAccountById(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("no userAccount found with ID 42", exception.getMessage());
    }

    @Test
    public void testCreateValidEvent()
    {
        //Arrange
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        when(eventRepository.save(any(Event.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        EventDto eventDto = new EventDto(VALID_START_DATE.toLocalDate(), VALID_START_TIME.toLocalTime(), VALID_END_DATE.toLocalDate(), VALID_END_TIME.toLocalTime(), VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, 42);

        //Act
        Event createdEvent = eventService.createEvent(eventDto);

        //Assert
        assertNotNull(createdEvent);
        assertEquals(VALID_START_DATE, createdEvent.getStartDate());
        assertEquals(VALID_START_TIME, createdEvent.getStartTime());
        assertEquals(VALID_END_DATE, createdEvent.getEndDate());
        assertEquals(VALID_END_TIME, createdEvent.getEndTime());
        assertEquals(VALID_MAX_NUM_PARTICIPANTS, createdEvent.getMaxNumParticipants());
        assertEquals(VALID_LOCATION, createdEvent.getLocation());
        assertEquals(VALID_DESCRIPTION, createdEvent.getDescription());
        assertEquals(VALID_CONTACT_EMAIL, createdEvent.getContactEmail());
        assertEquals(NAME, createdEvent.getCreator().getName());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testCreateEventWithInvalidEndDate()
    {
        //Arrange
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        EventDto eventDto = new EventDto(VALID_START_DATE.toLocalDate(), VALID_START_TIME.toLocalTime(), LocalDate.of(2026,02,10), VALID_END_TIME.toLocalTime(), VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, 42);

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,() -> eventService.createEvent(eventDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Start Date and Time must be before End Date and Time", exception.getMessage());
    }

    @Test
    public void testCreateEventWithInvalidEndTime()
    {
        //Arrange
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        EventDto eventDto = new EventDto(VALID_START_DATE.toLocalDate(), VALID_START_TIME.toLocalTime(), VALID_START_DATE.toLocalDate(), LocalTime.of(10,00,00), VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, 42);

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,() -> eventService.createEvent(eventDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Start Date and Time must be before End Date and Time", exception.getMessage());
    }

    @Test
    public void testCreateEventWithEqualStartTimeEndTime()
    {
        //Arrange
        when(userAccountRepository.findUserAccountById(42)).thenReturn(new UserAccount(NAME, EMAIL, PASSWORD));
        EventDto eventDto = new EventDto(VALID_START_DATE.toLocalDate(), VALID_START_TIME.toLocalTime(), VALID_START_DATE.toLocalDate(), VALID_START_TIME.toLocalTime(), VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, 42);

        //Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,() -> eventService.createEvent(eventDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Start Date and Time must be before End Date and Time", exception.getMessage());
    }

    @Test
    public void testCreateInvalidEventDto()
    {
        //Arrange + Act
        EventDto event = new EventDto(LocalDate.of(2020,10,10), VALID_START_TIME.toLocalTime(), LocalDate.of(2020,10,11), VALID_END_TIME.toLocalTime(), 0, null, null, null, 42);
        List<String> expectedConstraintViolations = new ArrayList<>();
        expectedConstraintViolations.add("max number of participants must be at least 1");
        expectedConstraintViolations.add("endDate must not be in the past");
        expectedConstraintViolations.add("startDate must not be in the past");
        expectedConstraintViolations.add("location must not be null");
        expectedConstraintViolations.add("description must not be null");
        expectedConstraintViolations.add("contactEmail must not be null");

        //Assert
        Set<ConstraintViolation<EventDto>> violations = validator.validate(event);
        for (ConstraintViolation<EventDto> violation : violations) {
            assertTrue(expectedConstraintViolations.contains(violation.getMessage()));
            expectedConstraintViolations.remove(violation.getMessage());
        }
        assertTrue(expectedConstraintViolations.isEmpty());
    }

    @Test
    public void testFindEventByValidId()
    {
        //Arrange
        when(eventRepository.findEventById(42)).thenReturn(new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, new UserAccount(NAME, EMAIL, PASSWORD)));

        //Act
        Event event = eventService.findEventById(42);

        // Assert
        assertNotNull(event);
        assertEquals(VALID_START_DATE, event.getStartDate());
        assertEquals(VALID_START_TIME, event.getStartTime());
        assertEquals(VALID_END_DATE, event.getEndDate());
        assertEquals(VALID_END_TIME, event.getEndTime());
        assertEquals(VALID_MAX_NUM_PARTICIPANTS, event.getMaxNumParticipants());
        assertEquals(VALID_LOCATION, event.getLocation());
        assertEquals(VALID_DESCRIPTION, event.getDescription());
        assertEquals(VALID_CONTACT_EMAIL, event.getContactEmail());

        UserAccount creator = event.getCreator();
        assertNotNull(creator);
        assertEquals(NAME, creator.getName());

    }

    @Test
    public void testFindEventThatDoesntExist()
    {
        //Arrange + Act + Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.findEventById(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("no event found with ID 42", exception.getMessage());
    }

    public void testFindAllEventsSetup(int numOfEvents)
    {
        //Arrange
        UserAccount account = new UserAccount(NAME, EMAIL, PASSWORD);
        List<Integer> expectedEvents = new ArrayList<>();
        List<Event> mockEventList = new ArrayList<>();
        for(int i = 0; i < numOfEvents; i++)
        {
            Event eventToAdd = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, i, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, account);
            expectedEvents.add(i);
            mockEventList.add(eventToAdd);
        }
        when(eventRepository.findAll()).thenReturn(mockEventList);

        //Act
        List<Event> foundEvents = eventService.findAllEvents();

        //Assert
        assertEquals(foundEvents.size(), numOfEvents);
        for(int i = 0; i < numOfEvents; i++)
        {
            assertTrue(expectedEvents.contains(foundEvents.get(i).getMaxNumParticipants()));
            expectedEvents.remove((Integer)i);
        }
    }

    @Test
    public void testFindAllEventsMultiple()
    {
        testFindAllEventsSetup(10);
    }

    @Test
    public void testFindAllEventsSingle()
    {
        testFindAllEventsSetup(1);
    }

    @Test
    public void testFindAllEventsNone()
    {

        testFindAllEventsSetup(0);
    }

    @Test
    public void testUpdateValidEvent()
    {
        //Arrange
        when(eventRepository.save(any(Event.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        when(eventRepository.findEventById(42)).thenReturn(new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user));
        LocalDate newStartDate = LocalDate.of(2040,10,10);
        LocalTime newStarTime = LocalTime.of(14,00,00);
        LocalDate newEndDate = LocalDate.of(2040,10,11);
        LocalTime newEndTime = LocalTime.of(15,00,00);
        int newMaxNumParticipants = 5;
        String newLocation = "Concordia";
        String newDescription = "Taking a break";
        String newContactEmail = "concordiaBoardGameAssociation@gmail.com";
        EventDto eventDetails = new EventDto(newStartDate, newStarTime, newEndDate, newEndTime, newMaxNumParticipants, newLocation, newDescription, newContactEmail, 42);

        //Act
        Event updatedEvent = eventService.updateEvent(42, eventDetails);

        //Assert
        assertNotNull(updatedEvent);
        assertEquals(newStartDate, updatedEvent.getStartDate().toLocalDate());
        assertEquals(newStarTime, updatedEvent.getStartTime().toLocalTime());
        assertEquals(newEndDate, updatedEvent.getEndDate().toLocalDate());
        assertEquals(newEndTime, updatedEvent.getEndTime().toLocalTime());
        assertEquals(newMaxNumParticipants, updatedEvent.getMaxNumParticipants());
        assertEquals(newLocation, updatedEvent.getLocation());
        assertEquals(newDescription, updatedEvent.getDescription());
        assertEquals(newContactEmail, updatedEvent.getContactEmail());
        assertEquals(user.getName(), updatedEvent.getCreator().getName());
    }

    @Test
    void testDeleteEvent() {
        // Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event eventToDelete = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        when(eventRepository.findEventById(42)).thenReturn(eventToDelete);

        // Act
        Event deletedEvent = eventService.deleteEvent(42);

        // Assert
        assertNotNull(deletedEvent);
        assertEquals(VALID_START_DATE, deletedEvent.getStartDate());
        assertEquals(VALID_START_TIME, deletedEvent.getStartTime());
        assertEquals(VALID_END_DATE, deletedEvent.getEndDate());
        assertEquals(VALID_END_TIME, deletedEvent.getEndTime());
        assertEquals(VALID_MAX_NUM_PARTICIPANTS, deletedEvent.getMaxNumParticipants());
        assertEquals(VALID_LOCATION, deletedEvent.getLocation());
        assertEquals(VALID_DESCRIPTION, deletedEvent.getDescription());
        assertEquals(VALID_CONTACT_EMAIL, deletedEvent.getContactEmail());
        assertEquals(user.getName(), deletedEvent.getCreator().getName());

        verify(registrationRepository, times(1)).deleteByKey_Event(eventToDelete);
        verify(eventRepository, times(1)).delete(eventToDelete);
    }

    @Test
    public void testFindGameByValidId()
    {
        //Arrange
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        when(gameRepository.findGameById(42)).thenReturn(game);

        //Act
        Game foundGAme = gameRepository.findGameById(42);

        // Assert
        assertNotNull(foundGAme);
        assertEquals(TITLE, game.getTitle());
        assertEquals(MIN_NUM_PLAYERS, game.getMinNumPlayers());
        assertEquals(MAX_NUM_PLAYERS, game.getMaxNumPlayers());
        assertEquals(PICTURE_URL, game.getPictureURL());
        assertEquals(DESCRIPTION, game.getDescription());
    }

    @Test
    public void testFindGameThatDoesntExist()
    {
        //Arrange/Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.findGameById(42));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("no game found with ID 42", exception.getMessage());
    }
    
    @Test
    public void testAddGameToEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        when(gameRepository.findGameById(42)).thenReturn(game);
        when(eventGameRepository.save(any(EventGame.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        when(eventGameRepository.findEventGameByKey(any(EventGame.Key.class))).thenReturn(null);
        EventGameDto eventDetails = new EventGameDto(42, 42);

        //Act
        EventGame eventGame = eventService.addGameToEvent(eventDetails);

        //Assert
        assertNotNull(eventGame);
        EventGame.Key key = eventGame.getKey();
        assertNotNull(key);
        Event foundEvent = key.getEvent();
        assertNotNull(foundEvent);
        Game foundGame = key.getGame();
        assertNotNull(foundGame);
        assertEquals(VALID_DESCRIPTION, foundEvent.getDescription());
        assertEquals(TITLE, foundGame.getTitle());
    }

    @Test
    public void testAddGameToEventAlreadyAdded()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        when(gameRepository.findGameById(42)).thenReturn(game);
        EventGame eventGame = new EventGame(new EventGame.Key(event, game));
        when(eventGameRepository.findEventGameByKey(any(EventGame.Key.class))).thenReturn(eventGame);
        EventGameDto eventDetails = new EventGameDto(42, 42);

        //Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () ->  eventService.addGameToEvent(eventDetails));
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot add game 42 to event 42 since it is already added", exception.getMessage());
    }

    @Test
    public void testRemoveValidGameFromEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        when(gameRepository.findGameById(42)).thenReturn(game);
        EventGame eventGame= new EventGame(new EventGame.Key(event, game));
        when(eventGameRepository.findEventGameByKey(any(EventGame.Key.class))).thenReturn(eventGame);
        EventGameDto eventDetails = new EventGameDto(42, 42);

        //Act
        EventGame returnedEventGame = eventService.removeGameFromEvent(eventDetails);

        //Assert
        assertNotNull(returnedEventGame);
        EventGame.Key key = returnedEventGame.getKey();
        assertNotNull(key);
        Event foundEvent = key.getEvent();
        assertNotNull(foundEvent);
        Game foundGame = key.getGame();
        assertNotNull(foundGame);
        assertEquals(VALID_DESCRIPTION, foundEvent.getDescription());
        assertEquals(TITLE, foundGame.getTitle());
        verify(eventGameRepository, times(1)).delete(eventGame);
    }

    @Test
    public void testRemoveInvalidGameFromEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        Game game = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        when(gameRepository.findGameById(42)).thenReturn(game);
        when(eventGameRepository.findEventGameByKey(any(EventGame.Key.class))).thenReturn(null);
        EventGameDto eventDetails = new EventGameDto(42, 42);

        //Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class,() -> eventService.removeGameFromEvent(eventDetails));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("no event game found with gameId 42 and eventId 42", exception.getMessage());
    }

    @Test
    public void testFindEventGamesByEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        Game game1 = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);
        Game game2 = new Game(TITLE+"1", MIN_NUM_PLAYERS+1, MAX_NUM_PLAYERS+1, PICTURE_URL+"1", DESCRIPTION+"1");

        when(eventRepository.findEventById(42)).thenReturn(event1);

        List<EventGame> eventGames = new ArrayList<>();
        eventGames.add(new EventGame(new EventGame.Key(event1, game1)));
        eventGames.add(new EventGame(new EventGame.Key(event1, game2)));
        Iterable<EventGame> iterable = eventGames;

        when(eventGameRepository.findByKey_Event(event1)).thenReturn(iterable);

        //Act
        List<EventGame> foundEventGames = eventService.findEventGamesByEvent(42);

        //Assert
        assertTrue(foundEventGames.size() == 2);
        for(EventGame eventGame : foundEventGames)
        {
            assertTrue(eventGames.contains(eventGame));
            assertNotNull(eventGame.getKey());
            assertNotNull(eventGame.getKey().getEvent());
            assertEquals(event1.getDescription(), eventGame.getKey().getEvent().getDescription());
            eventGames.remove(eventGame);
        }
    }

    @Test
    public void testFindEventGamesByGame()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        Event event2 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS+1, VALID_LOCATION+"1", VALID_DESCRIPTION+"1", VALID_CONTACT_EMAIL+"1", user);
        Game game1 = new Game(TITLE, MIN_NUM_PLAYERS, MAX_NUM_PLAYERS, PICTURE_URL, DESCRIPTION);

        when(gameRepository.findGameById(42)).thenReturn(game1);

        List<EventGame> eventGames = new ArrayList<>();
        eventGames.add(new EventGame(new EventGame.Key(event1, game1)));
        eventGames.add(new EventGame(new EventGame.Key(event2, game1)));
        Iterable<EventGame> iterable = eventGames;

        when(eventGameRepository.findByKey_GamePlayed(game1)).thenReturn(iterable);

        //Act
        List<EventGame> foundEventGames = eventService.findEventGamesByGame(42);

        //Assert
        assertTrue(foundEventGames.size() == 2);
        for(EventGame eventGame : foundEventGames)
        {
            assertTrue(eventGames.contains(eventGame));
            assertNotNull(eventGame.getKey());
            assertNotNull(eventGame.getKey().getGame());
            assertEquals(game1.getTitle(), eventGame.getKey().getGame().getTitle());
            eventGames.remove(eventGame);
        }
    }

    @Test
    public void testFindRegistrationByValidEventAndParticipant()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        Registration registration = new Registration(new Registration.RegistrationKey(user, event), VALID_START_DATE, VALID_START_TIME);

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(registration);

        //Act
        Registration foundRegistration = eventService.findRegistrationByEventAndParticipant(42, 42);

        //Assert
        assertNotNull(foundRegistration);
        assertEquals(VALID_START_DATE, foundRegistration.getRegistrationDate());
        assertEquals(VALID_START_TIME, foundRegistration.getRegistrationTime());
        assertNotNull(foundRegistration.getKey());
        assertNotNull(foundRegistration.getKey().getEvent());
        assertNotNull(foundRegistration.getKey().getUser());
        assertEquals(user.getName(), foundRegistration.getKey().getUser().getName());
        assertEquals(event.getDescription(), foundRegistration.getKey().getEvent().getDescription());
    }

    @Test
    public void testFindRegistrationByInvalidEventAndParticipant()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(null);

        //Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.findRegistrationByEventAndParticipant(42, 42));
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("no registration found with userId 42 and eventId 42", exception.getMessage());
    }

    @Test
    public void testFindRegistrationsByEvent()
    {
        //Arrange
        UserAccount user1 = new UserAccount(NAME, EMAIL, PASSWORD);
        UserAccount user2 = new UserAccount(NAME+"1", EMAIL+"1", PASSWORD+"1");
        Event event1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user1);

        when(eventRepository.findEventById(42)).thenReturn(event1);

        List<Registration> registrations = new ArrayList<>();
        registrations.add(new Registration(new Registration.RegistrationKey(user1, event1), VALID_START_DATE, VALID_START_TIME));
        registrations.add(new Registration(new Registration.RegistrationKey(user2, event1), VALID_START_DATE, VALID_START_TIME));
        Iterable<Registration> iterable = registrations;

        when(registrationRepository.findByKey_Event(event1)).thenReturn(iterable);

        //Act
        List<Registration> foundRegistrations = eventService.findRegistrationsByEvent(42);

        //Assert
        assertTrue(foundRegistrations.size() == 2);
        for(Registration registration : foundRegistrations)
        {
            assertTrue(foundRegistrations.contains(registration));
            assertNotNull(registration.getKey());
            assertNotNull(registration.getKey().getEvent());
            assertEquals(event1.getDescription(), registration.getKey().getEvent().getDescription());
            foundRegistrations.remove(registration);
        }
    }

    @Test
    public void testFindRegistrationsByParticipant()
    {
        //Arrange
        UserAccount user1 = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event1 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user1);
        Event event2 = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS+1, VALID_LOCATION+"1", VALID_DESCRIPTION+"1", VALID_CONTACT_EMAIL+"1", user1);

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user1);

        List<Registration> registrations = new ArrayList<>();
        registrations.add(new Registration(new Registration.RegistrationKey(user1, event1), VALID_START_DATE, VALID_START_TIME));
        registrations.add(new Registration(new Registration.RegistrationKey(user1, event2), VALID_START_DATE, VALID_START_TIME));
        Iterable<Registration> iterable = registrations;

        when(registrationRepository.findByKey_Participant(user1)).thenReturn(iterable);

        //Act
        List<Registration> foundRegistrations = eventService.findRegistrationsByParticipant(42);

        //Assert
        assertTrue(foundRegistrations.size() == 2);
        for(Registration registration : foundRegistrations)
        {
            assertTrue(foundRegistrations.contains(registration));
            assertNotNull(registration.getKey());
            assertNotNull(registration.getKey().getUser());
            assertEquals(user1.getName(), registration.getKey().getUser().getName());
            foundRegistrations.remove(registration);
        }
    }

    @Test
    public void testRegisterUserToEventWithAvailableCapacity()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        List<Registration> registrations = new ArrayList<>();

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findByKey_Event(any(Event.class))).thenReturn(registrations);
        when(registrationRepository.save(any(Registration.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(null);

        //Act
        Registration createdRegistration = eventService.registerUserToEvent(42, 42);

        //Assert
        assertNotNull(createdRegistration);
        assertNotNull(createdRegistration.getKey());
        assertNotNull(createdRegistration.getKey().getEvent());
        assertNotNull(createdRegistration.getKey().getUser());
        assertEquals(event.getDescription(), createdRegistration.getKey().getEvent().getDescription());
        assertEquals(user.getName(), createdRegistration.getKey().getUser().getName());
    }

    @Test
    public void testRegisterUserToEventWithUnavailableCapacity()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, 1, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        List<Registration> registrations = new ArrayList<>();
        registrations.add(new Registration(new Registration.RegistrationKey(user, event), VALID_START_DATE, VALID_START_TIME));

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findByKey_Event(any(Event.class))).thenReturn(registrations);
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(null);

        //Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.registerUserToEvent(42, 42));
        assertNotNull(exception);
        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
        assertEquals("event 42 is already at maximum capacity", exception.getMessage());
    }

    @Test
    public void testRegisterUserToEventThatAlreadyEnded()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Date StartDate = Date.valueOf("2020-10-10");
        Date EndDate = Date.valueOf("2020-10-11");
        Event event = new Event(StartDate, VALID_START_TIME, EndDate, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        List<Registration> registrations = new ArrayList<>();

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findByKey_Event(any(Event.class))).thenReturn(registrations);
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(null);

        //Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.registerUserToEvent(42, 42));
        assertNotNull(exception);
        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
        assertEquals("event 42 has already ended", exception.getMessage());
    }

    @Test
    public void testRegisterUserToEventAlreadyRegistered()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Date StartDate = Date.valueOf("2020-10-10");
        Date EndDate = Date.valueOf("2020-10-11");
        Event event = new Event(StartDate, VALID_START_TIME, EndDate, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        Registration registration = new Registration(new Registration.RegistrationKey(user, event), VALID_START_DATE, VALID_START_TIME);
        List<Registration> registrations = new ArrayList<>();
        registrations.add(registration);

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(registration);

        //Act/Assert
        BoardGameSharingSystemException exception = assertThrows(BoardGameSharingSystemException.class, () -> eventService.registerUserToEvent(42, 42));
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Registration already exists for user with id 42 and event id 42", exception.getMessage());
    }

    @Test
    public void testDeregisterUserToEvent()
    {
        //Arrange
        UserAccount user = new UserAccount(NAME, EMAIL, PASSWORD);
        Date RegistrationDate = Date.valueOf("2020-10-10");
        Time RegistrationTime = Time.valueOf("11:00:00");
        Event event = new Event(VALID_START_DATE, VALID_START_TIME, VALID_END_DATE, VALID_END_TIME, VALID_MAX_NUM_PARTICIPANTS, VALID_LOCATION, VALID_DESCRIPTION, VALID_CONTACT_EMAIL, user);
        Registration registration = new Registration(new Registration.RegistrationKey(user, event), RegistrationDate, RegistrationTime);

        when(userAccountRepository.findUserAccountById(42)).thenReturn(user);
        when(eventRepository.findEventById(42)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(any(Registration.RegistrationKey.class))).thenReturn(registration);

        //Act
        Registration deletedRegistration = eventService.deregisterParticipantFromEvent(42, 42);

        //Assert
        assertNotNull(deletedRegistration);
        assertNotNull(deletedRegistration.getKey());
        assertNotNull(deletedRegistration.getKey().getUser());
        assertNotNull(deletedRegistration.getKey().getEvent());
        assertEquals(deletedRegistration.getRegistrationDate(), registration.getRegistrationDate());
        assertEquals(deletedRegistration.getKey().getEvent().getDescription(), registration.getKey().getEvent().getDescription());
        assertEquals(deletedRegistration.getKey().getUser().getName(), registration.getKey().getUser().getName());
        verify(registrationRepository, times(1)).delete(registration);
    }
}