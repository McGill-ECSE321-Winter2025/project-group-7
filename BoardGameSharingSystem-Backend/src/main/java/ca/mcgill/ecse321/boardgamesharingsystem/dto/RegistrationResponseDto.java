package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;

public class RegistrationResponseDto {
    private int userId;
    private int eventId;
    private Date registrationDate;
    private Time registrationTime;
    private Date responseDate;
    private Time responseTime;

    //No args constructor for Jackson
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
}
