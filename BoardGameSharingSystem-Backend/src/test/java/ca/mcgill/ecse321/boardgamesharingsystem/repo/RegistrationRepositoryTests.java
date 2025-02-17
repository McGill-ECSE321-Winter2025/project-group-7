package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration.RegistrationKey;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;

@SpringBootTest
public class RegistrationRepositoryTests {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EventRepository eventRepository;

    @AfterEach
    public void clearDatabase() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testSaveAndRetrieveRegistration() {
        //Arrange
        UserAccount creator = new UserAccount("mike", "mike@mikemail.com", "mike123");
        creator = userAccountRepository.save(creator);
        
        Event event = new Event(
                Date.valueOf("2025-02-11"), Time.valueOf("11:00:00"),
                Date.valueOf("2025-02-11"), Time.valueOf("22:00:00"),
                10, "McConnel 304", "ChessV2 playtest", creator
        );
        event = eventRepository.save(event);
        
        Event eventFromDb = eventRepository.findById(event.getId()).orElse(null);
        assertNotNull(eventFromDb, "Event should exist in the database");
        
        RegistrationKey regKey = new RegistrationKey(creator, eventFromDb);
        Registration registration = new Registration(regKey, Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registration = registrationRepository.save(registration);
        
        //Act
        Registration registrationFromDb = registrationRepository.findRegistrationByKey(regKey);
        
        //Assert
        assertNotNull(registrationFromDb, "Registration should be present in the database");
        assertEquals(creator.getId(), registrationFromDb.getKey().getUser().getId(), "User ID should match");
        assertEquals(event.getId(), registrationFromDb.getKey().getEvent().getId(), "Event ID should match");
        assertEquals(Date.valueOf("2025-02-17"), registrationFromDb.getRegistrationDate(), "Registration date should match");
        assertEquals(Time.valueOf("12:00:00"), registrationFromDb.getRegistrationTime(), "Registration time should match");
    }
}
