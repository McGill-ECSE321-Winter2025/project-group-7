package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;

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
    }

    @Test
    public void testCreateEventGame() {
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
        Event event = new Event(startDate, startTime, endDate, endTime, maxNumParticipants, location, description, creator);
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

}
