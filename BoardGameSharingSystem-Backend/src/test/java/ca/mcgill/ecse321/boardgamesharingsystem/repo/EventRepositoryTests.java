package ca.mcgill.ecse321.boardgamesharingsystem.repo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

@SpringBootTest
public class EventRepositoryTests {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @AfterEach
    public void clearDatabase()
    {
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadEvent()
    {
        //Arrange
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

        //Act
        Event eventFromDb = eventRepository.findEventById(event.getId());
        
        //Assert
        assertNotNull(eventFromDb);
        assertEquals(event.getId(), eventFromDb.getId());
        assertEquals(startDate, eventFromDb.getStartDate());
        assertEquals(endDate, eventFromDb.getEndDate());
        assertEquals(startTime, eventFromDb.getStartTime());
        assertEquals(endTime, eventFromDb.getEndTime());
        assertEquals(maxNumParticipants, eventFromDb.getMaxNumParticipants());
        assertEquals(location, eventFromDb.getLocation());
        assertEquals(description, eventFromDb.getDescription());
        assertNotNull(eventFromDb.getCreator());
        assertEquals(creator.getId(), eventFromDb.getCreator().getId());
    }
}