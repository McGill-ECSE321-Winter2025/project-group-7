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
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration.RegistrationKey;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

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
                10, "McConnel 304", "ChessV2 playtest", 
                "mikeEvents@mikemail.com", creator
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

    @Test
    public void testDeleteByKeyEvent()
    {
        //Arrange
        UserAccount Abe = new UserAccount("Abe", "Abe@mail.com", "AbePassword");
        userAccountRepository.save(Abe);
        UserAccount Ray = new UserAccount("Ray", "Ray@mail.com", "RayPassword");
        userAccountRepository.save(Ray);
        UserAccount Dave = new UserAccount("Dave", "Dave@mail.com", "DavePassword");
        userAccountRepository.save(Dave);
        UserAccount Amy = new UserAccount("Amy", "Amy@mail.com", "AmyPassword");
        userAccountRepository.save(Amy);

        Event AbeEvent = new Event(
                Date.valueOf("2025-02-11"), Time.valueOf("11:00:00"),
                Date.valueOf("2025-02-11"), Time.valueOf("22:00:00"),
                10, "McConnel 304", "ChessV2 playtest", 
                "Abe@Abemail.com", Abe
        );
        Event AbeEventFromDb = eventRepository.save(AbeEvent);
        Event RayEvent = new Event(
                Date.valueOf("2026-03-12"), Time.valueOf("12:00:00"),
                Date.valueOf("2026-03-12"), Time.valueOf("23:00:00"),
                10, "Adams AUD", "ChessV3 Demo", 
                "Ray@Raymail.com", Ray
        );
        Event RayEventFromDb = eventRepository.save(RayEvent);
        Registration registration1 = new Registration(new RegistrationKey(Abe, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registrationRepository.save(registration1);
        Registration registration2 = new Registration(new RegistrationKey(Ray, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:01"));
        registrationRepository.save(registration2);
        Registration registration3 = new Registration(new RegistrationKey(Dave, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:02"));
        registrationRepository.save(registration3);
        Registration registration4 = new Registration(new RegistrationKey(Amy, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:03"));
        registrationRepository.save(registration4);
        Registration registration11 = new Registration(new RegistrationKey(Abe, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registrationRepository.save(registration11);
        Registration registration12 = new Registration(new RegistrationKey(Ray, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:01"));
        registrationRepository.save(registration12);
        Registration registration13 = new Registration(new RegistrationKey(Dave, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:02"));
        registrationRepository.save(registration13);
        Registration registration14 = new Registration(new RegistrationKey(Amy, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:03"));
        registrationRepository.save(registration14);
        List<String> accounts = new ArrayList<>();
        accounts.add("Abe");
        accounts.add("Ray");
        accounts.add("Dave");
        accounts.add("Amy");
        
        //Act
        registrationRepository.deleteByKey_Event(RayEventFromDb);

        //Assert
        Iterable<Registration> allRegistrations = registrationRepository.findAll();
        int count = 0;
        for(Registration registration : allRegistrations)
        {
            assertEquals(AbeEventFromDb.getId(), registration.getKey().getEvent().getId());
            assertTrue(accounts.contains(registration.getKey().getUser().getName()));
            accounts.remove(registration.getKey().getUser().getName());
            count++;
        }
        assertEquals(4, count);
    }

    @Test
    public void testFindByKeyEvent()
    {
        //Arrange
        UserAccount Abe = new UserAccount("Abe", "Abe@mail.com", "AbePassword");
        userAccountRepository.save(Abe);
        UserAccount Ray = new UserAccount("Ray", "Ray@mail.com", "RayPassword");
        userAccountRepository.save(Ray);
        UserAccount Dave = new UserAccount("Dave", "Dave@mail.com", "DavePassword");
        userAccountRepository.save(Dave);
        UserAccount Amy = new UserAccount("Amy", "Amy@mail.com", "AmyPassword");
        userAccountRepository.save(Amy);

        Event AbeEvent = new Event(
                Date.valueOf("2025-02-11"), Time.valueOf("11:00:00"),
                Date.valueOf("2025-02-11"), Time.valueOf("22:00:00"),
                10, "McConnel 304", "ChessV2 playtest", 
                "Abe@Abemail.com", Abe
        );
        Event AbeEventFromDb = eventRepository.save(AbeEvent);
        Event RayEvent = new Event(
                Date.valueOf("2026-03-12"), Time.valueOf("12:00:00"),
                Date.valueOf("2026-03-12"), Time.valueOf("23:00:00"),
                10, "Adams AUD", "ChessV3 Demo", 
                "Ray@Raymail.com", Ray
        );
        Event RayEventFromDb = eventRepository.save(RayEvent);
        Registration registration1 = new Registration(new RegistrationKey(Abe, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registrationRepository.save(registration1);
        Registration registration2 = new Registration(new RegistrationKey(Ray, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:01"));
        registrationRepository.save(registration2);
        Registration registration3 = new Registration(new RegistrationKey(Dave, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:02"));
        registrationRepository.save(registration3);
        Registration registration4 = new Registration(new RegistrationKey(Amy, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:03"));
        registrationRepository.save(registration4);
        Registration registration11 = new Registration(new RegistrationKey(Abe, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registrationRepository.save(registration11);
        Registration registration12 = new Registration(new RegistrationKey(Ray, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:01"));
        registrationRepository.save(registration12);
        Registration registration13 = new Registration(new RegistrationKey(Dave, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:02"));
        registrationRepository.save(registration13);
        Registration registration14 = new Registration(new RegistrationKey(Amy, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:03"));
        registrationRepository.save(registration14);
        List<String> accounts = new ArrayList<>();
        accounts.add("Abe");
        accounts.add("Ray");
        accounts.add("Dave");
        accounts.add("Amy");

        //Act
        Iterable<Registration> foundRegistrations = registrationRepository.findByKey_Event(RayEventFromDb);

        //Assert
        for(Registration registration : foundRegistrations)
        {
            assertEquals(RayEventFromDb.getId(), registration.getKey().getEvent().getId());
            assertTrue(accounts.contains(registration.getKey().getUser().getName()));
            accounts.remove(registration.getKey().getUser().getName());
        }
    }
    @Test
    public void testFindByKeyParticipant()
    {
        //Arrange
        UserAccount Abe = new UserAccount("Abe", "Abe@mail.com", "AbePassword");
        userAccountRepository.save(Abe);
        UserAccount Ray = new UserAccount("Ray", "Ray@mail.com", "RayPassword");
        userAccountRepository.save(Ray);
        UserAccount Dave = new UserAccount("Dave", "Dave@mail.com", "DavePassword");
        userAccountRepository.save(Dave);
        UserAccount Amy = new UserAccount("Amy", "Amy@mail.com", "AmyPassword");
        userAccountRepository.save(Amy);

        Event AbeEvent = new Event(
                Date.valueOf("2025-02-11"), Time.valueOf("11:00:00"),
                Date.valueOf("2025-02-11"), Time.valueOf("22:00:00"),
                10, "McConnel 304", "ChessV2 playtest", 
                "Abe@Abemail.com", Abe
        );
        Event AbeEventFromDb = eventRepository.save(AbeEvent);
        Event RayEvent = new Event(
                Date.valueOf("2026-03-12"), Time.valueOf("12:00:00"),
                Date.valueOf("2026-03-12"), Time.valueOf("23:00:00"),
                10, "Adams AUD", "ChessV3 Demo", 
                "Ray@Raymail.com", Ray
        );
        Event RayEventFromDb = eventRepository.save(RayEvent);
        Registration registration1 = new Registration(new RegistrationKey(Abe, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registrationRepository.save(registration1);
        Registration registration2 = new Registration(new RegistrationKey(Ray, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:01"));
        registrationRepository.save(registration2);
        Registration registration3 = new Registration(new RegistrationKey(Dave, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:02"));
        registrationRepository.save(registration3);
        Registration registration4 = new Registration(new RegistrationKey(Amy, AbeEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:03"));
        registrationRepository.save(registration4);
        Registration registration11 = new Registration(new RegistrationKey(Abe, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:00"));
        registrationRepository.save(registration11);
        Registration registration12 = new Registration(new RegistrationKey(Ray, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:01"));
        registrationRepository.save(registration12);
        Registration registration13 = new Registration(new RegistrationKey(Dave, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:02"));
        registrationRepository.save(registration13);
        Registration registration14 = new Registration(new RegistrationKey(Amy, RayEventFromDb), Date.valueOf("2025-02-17"), Time.valueOf("12:00:03"));
        registrationRepository.save(registration14);
        List<Integer> events = new ArrayList<>();
        events.add(AbeEventFromDb.getId());
        events.add(RayEventFromDb.getId());

        //Act
        Iterable<Registration> foundRegistrations = registrationRepository.findByKey_Participant(Ray);

        //Assert
        for(Registration registration : foundRegistrations)
        {
            assertEquals(Ray.getName(), registration.getKey().getUser().getName());
            assertTrue(events.contains(registration.getKey().getEvent().getId()));
            events.remove((Integer)registration.getKey().getEvent().getId());
        }
    }
}
