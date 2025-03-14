package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class RegistrationResponseDto {
    private int userId;
    private int eventId;
    private Date registrationDate;
    private Time registrationTime;
    private Date responseDate;
    private Time responseTime;

    private Date eventStartDate;
    private Time eventStartTime;
    private Date eventEndDate;
    private Time eventEndTime;
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
        this.registrationDate = registration.getRegistrationDate();
        this.registrationTime = registration.getRegistrationTime();
        registrationDate = Date.valueOf(LocalDate.now());
        registrationTime = Time.valueOf(LocalTime.now());

        Event event = registration.getKey().getEvent();
        this.eventStartDate = event.getStartDate();
        this.eventStartTime = event.getStartTime();
        this.eventEndDate = event.getEndDate();
        this.eventEndTime = event.getEndTime();
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
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public Time getRegistrationTime() {
        return registrationTime;
    }
    
    public Date getResponseDate() {
        return responseDate;
    }
    
    public Time getResponseTime() {
        return responseTime;
    }    

    public Date getEventStartDate() {
        return eventStartDate;
    }
    
    public Time getEventStartTime() {
        return eventStartTime;
    }
    
    public Date getEventEndDate() {
        return eventEndDate;
    }
    
    public Time getEventEndTime() {
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
