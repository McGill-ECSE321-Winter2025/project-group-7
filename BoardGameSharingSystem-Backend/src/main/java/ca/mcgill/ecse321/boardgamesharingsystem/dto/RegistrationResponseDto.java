package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class RegistrationResponseDto {
    private int userId;
    private int eventId;
    private LocalDate registrationDate;
    private LocalTime registrationTime;
    private LocalDate responseDate;
    private LocalTime responseTime;

    private LocalDate eventStartDate;
    private LocalTime eventStartTime;
    private LocalDate eventEndDate;
    private LocalTime eventEndTime;
    private int eventMaxNumParticipants;
    private String eventLocation;
    private String eventDescription;
    private String eventContactEmail;
    private int eventCreatorId;

    private String registrantEmail;
    private String registrantName;
    private String registrantPassword;

    //No args constructor for Jackson
    @SuppressWarnings("unused")
    private RegistrationResponseDto()
    {

    }

    public RegistrationResponseDto(Registration registration)
    {
        this.userId = registration.getKey().getUser().getId();
        this.eventId = registration.getKey().getEvent().getId();
        this.registrationDate = registration.getRegistrationDate().toLocalDate();
        this.registrationTime = registration.getRegistrationTime().toLocalTime();
        responseDate = LocalDate.now();
        responseTime = LocalTime.now();

        Event event = registration.getKey().getEvent();
        this.eventStartDate = event.getStartDate().toLocalDate();
        this.eventStartTime = event.getStartTime().toLocalTime();
        this.eventEndDate = event.getEndDate().toLocalDate();
        this.eventEndTime = event.getEndTime().toLocalTime();
        this.eventMaxNumParticipants = event.getMaxNumParticipants();
        this.eventLocation = event.getLocation();
        this.eventDescription = event.getDescription();
        this.eventContactEmail = event.getContactEmail();
        this.eventCreatorId = event.getCreator().getId();

        UserAccount user = registration.getKey().getUser();
        this.registrantEmail = user.getEmail();
        this.registrantName = user.getName();
        this.registrantPassword = user.getPassword();
    }

    public int getUserId() {
        return userId;
    }
    
    public int getEventId() {
        return eventId;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public LocalTime getRegistrationTime() {
        return registrationTime;
    }
    
    public LocalDate getResponseDate() {
        return responseDate;
    }
    
    public LocalTime getResponseTime() {
        return responseTime;
    }    

    public LocalDate getEventStartDate() {
        return eventStartDate;
    }
    
    public LocalTime getEventStartTime() {
        return eventStartTime;
    }
    
    public LocalDate getEventEndDate() {
        return eventEndDate;
    }
    
    public LocalTime getEventEndTime() {
        return eventEndTime;
    }
    
    public int getEventMaxNumParticipants() {
        return eventMaxNumParticipants;
    }
    
    public String getEventLocation() {
        return eventLocation;
    }
    
    public String getEventDescription() {
        return eventDescription;
    }
    
    public String getEventContactEmail() {
        return eventContactEmail;
    }
    
    public int getEventCreatorId() {
        return eventCreatorId;
    }
    
    public String getRegistrantEmail() {
        return registrantEmail;
    }
    
    public String getRegistrantName() {
        return registrantName;
    }
    
    public String getRegistrantPassword() {
        return registrantPassword;
    }
}
