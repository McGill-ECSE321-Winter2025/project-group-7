package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

@SpringBootTest
public class EventGameRepositoryTests {
    @Autowired
    private EventGameRepository eventGameRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    public void clearDatabase() {
        eventGameRepository.deleteAll();
        eventRepository.deleteAll();
        gameRepository.deleteAll();
        userAccountRepository.deleteAll(); 
    }

    @Test
    public void testCreateAndReadEventGame() {
        //Arrange
        Game chess = new Game("chess", 2, 2, "chess.com","the chess game consists..." );
        chess = gameRepository.save(chess);

        Date startDate = Date.valueOf("2025-02-11");
        Time startTime = Time.valueOf("11:00:00");
        Date endDate = Date.valueOf("2025-02-11");
        Time endTime = Time.valueOf("22:00:00");

        String creatorName = "mike";
        String creatorEmail = "mike@mikemail.com";
        String creatorPassword = "mike123";
        UserAccount creator = new UserAccount(creatorName, creatorEmail, creatorPassword);
        creator = userAccountRepository.save(creator);
        
        int maxNumParticipants = 10;
        String location = "McConnel 304";
        String description = "ChessV2 playtest";
        String contactEmail = "mikeEvents@mikemail.com";
        Event event = new Event(startDate, startTime, endDate, endTime, maxNumParticipants, location, description, contactEmail, creator);
        event = eventRepository.save(event);

        EventGame eventGame = new EventGame(new EventGame.Key(event, chess));
        eventGame = eventGameRepository.save(eventGame);

        //Act
        EventGame eventGameFromDb = eventGameRepository.findEventGameByKey(eventGame.getKey());

        //Assert
        assertNotNull(eventGameFromDb);
        assertNotNull(eventGameFromDb.getKey());
        assertNotNull(eventGameFromDb.getKey().getEvent());
        assertEquals(eventGameFromDb.getKey().getEvent().getId(), event.getId());
        assertNotNull(eventGameFromDb.getKey().getGame());
        assertEquals(eventGameFromDb.getKey().getGame().getId(), chess.getId());
    }

    @Test
    public void testFindByKeyEvent()
    {
        //Arrange
        Game chess = new Game("chess", 2, 2, "chess.com/favicon", "A medieval game");
        chess = gameRepository.save(chess);
        Game catan = new Game("catan", 2, 4, "freeimages.com/games/catan/img2", "A game about hexagons");
        catan = gameRepository.save(catan);
        Game checkers = new Game("checkers", 2, 2, "checkersIsTheBest.com/image", "Circles on a board");
        checkers = gameRepository.save(checkers);
        Game stratego = new Game("stratego", 2, 2, "stratego.com/official/image", "A strategy game");
        stratego = gameRepository.save(stratego);

        UserAccount Ray = new UserAccount("Ray", "Ray@Raymail.com", "Ray23");
        Ray = userAccountRepository.save(Ray);
        UserAccount Abe = new UserAccount("Abe", "Abe@Abemail.com", "AbeIsTheBest");
        Abe = userAccountRepository.save(Abe);

        Event RayEvent = new Event(
            Date.valueOf("2026-03-12"), Time.valueOf("12:00:00"),
            Date.valueOf("2026-03-12"), Time.valueOf("23:00:00"),
            10, "Adams AUD", "My Board games", 
            "Ray@Raymail.com", Ray
        );
        RayEvent = eventRepository.save(RayEvent);
        Event AbeEvent = new Event(
            Date.valueOf("2025-02-11"), Time.valueOf("11:00:00"),
            Date.valueOf("2025-02-11"), Time.valueOf("22:00:00"),
            10, "McConnel 304", "Friendly Hangout", 
            "Abe@Abemail.com", Abe
        );
        AbeEvent = eventRepository.save(AbeEvent);

        EventGame RayChess = new EventGame(new EventGame.Key(RayEvent, chess));
        RayChess = eventGameRepository.save(RayChess);
        EventGame RayCatan = new EventGame(new EventGame.Key(RayEvent, catan));
        RayCatan = eventGameRepository.save(RayCatan);
        EventGame AbeStratego = new EventGame(new EventGame.Key(AbeEvent, stratego));
        AbeStratego = eventGameRepository.save(AbeStratego);
        EventGame AbeCheckers = new EventGame(new EventGame.Key(AbeEvent, checkers));
        AbeCheckers = eventGameRepository.save(AbeCheckers);
        List<EventGame.Key> eventGames = new ArrayList<>();
        eventGames.add(AbeStratego.getKey());
        eventGames.add(AbeCheckers.getKey());

        //Act
        Iterable<EventGame> foundEventGames = eventGameRepository.findByKey_Event(AbeEvent);
        //Assert
        for (EventGame eventGame : foundEventGames) {
            assertTrue(eventGames.contains(eventGame.getKey()));
            eventGames.remove(eventGame.getKey());
        }
    }

    @Test
    public void testFindByKeyGamePlayed()
    {
        //Arrange
        Game chess = new Game("chess", 2, 2, "chess.com/favicon", "A medieval game");
        chess = gameRepository.save(chess);
        Game catan = new Game("catan", 2, 4, "freeimages.com/games/catan/img2", "A game about hexagons");
        catan = gameRepository.save(catan);

        UserAccount Ray = new UserAccount("Ray", "Ray@Raymail.com", "Ray23");
        Ray = userAccountRepository.save(Ray);
        UserAccount Abe = new UserAccount("Abe", "Abe@Abemail.com", "AbeIsTheBest");
        Abe = userAccountRepository.save(Abe);

        Event RayEvent = new Event(
            Date.valueOf("2026-03-12"), Time.valueOf("12:00:00"),
            Date.valueOf("2026-03-12"), Time.valueOf("23:00:00"),
            10, "Adams AUD", "My Board games", 
            "Ray@Raymail.com", Ray
        );
        RayEvent = eventRepository.save(RayEvent);
        Event AbeEvent = new Event(
            Date.valueOf("2025-02-11"), Time.valueOf("11:00:00"),
            Date.valueOf("2025-02-11"), Time.valueOf("22:00:00"),
            10, "McConnel 304", "Friendly Hangout", 
            "Abe@Abemail.com", Abe
        );
        AbeEvent = eventRepository.save(AbeEvent);
        Event RayEvent2 = new Event(
            Date.valueOf("2026-04-12"), Time.valueOf("12:00:00"),
            Date.valueOf("2026-04-12"), Time.valueOf("23:00:00"),
            10, "Adams AUD", "My Second Event!", 
            "Ray@Raymail.com", Ray
        );
        RayEvent2 = eventRepository.save(RayEvent2);
        Event AbeEvent2 = new Event(
            Date.valueOf("2025-04-11"), Time.valueOf("11:00:00"),
            Date.valueOf("2025-04-11"), Time.valueOf("22:00:00"),
            10, "McConnel 304", "Friendly Hangout 2", 
            "Abe@Abemail.com", Abe
        );
        AbeEvent2 = eventRepository.save(AbeEvent2);

        EventGame RayChess = new EventGame(new EventGame.Key(RayEvent, chess));
        RayChess = eventGameRepository.save(RayChess);
        EventGame RayCatan = new EventGame(new EventGame.Key(RayEvent2, catan));
        RayCatan = eventGameRepository.save(RayCatan);
        EventGame AbeChess = new EventGame(new EventGame.Key(AbeEvent, chess));
        AbeChess = eventGameRepository.save(AbeChess);
        EventGame AbeCatan = new EventGame(new EventGame.Key(AbeEvent2, catan));
        AbeCatan = eventGameRepository.save(AbeCatan);
        List<EventGame.Key> eventGames = new ArrayList<>();
        eventGames.add(RayCatan.getKey());
        eventGames.add(AbeCatan.getKey());

        //Act
        Iterable<EventGame> foundEventGames = eventGameRepository.findByKey_GamePlayed(catan);
        //Assert
        for (EventGame eventGame : foundEventGames) {
            assertTrue(eventGames.contains(eventGame.getKey()));
            eventGames.remove(eventGame.getKey());
        }
    }
}
