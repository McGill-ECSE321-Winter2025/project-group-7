package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;

public class EventGameResponseDto {
    private int eventId;
    private int gameId;
    private Date responseDate;
    private Time responseTime;

    //No args constructor for Jackson
    @SuppressWarnings("unused")
    private EventGameResponseDto()
    {

    }

    public EventGameResponseDto(EventGame eventGame)
    {
        this.eventId = eventGame.getKey().getEvent().getId();
        this.gameId = eventGame.getKey().getGame().getId();
        responseDate = Date.valueOf(LocalDate.now());
        responseTime = Time.valueOf(LocalTime.now());
    }

    public int getEventId() {
        return eventId;
    }
    
    public int getGameId() {
        return gameId;
    }
    
    public Date getResponseDate() {
        return responseDate;
    }
    
    public Time getResponseTime() {
        return responseTime;
    }    
}
