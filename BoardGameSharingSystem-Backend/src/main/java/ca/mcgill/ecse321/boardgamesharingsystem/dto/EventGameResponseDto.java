package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;

public class EventGameResponseDto {
    private int eventId;
    private int gameId;
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

    private String gameTitle;
    private int gameMinNumPlayers;
    private int gameMaxNumPlayers;
    private String gamePictureURL;
    private String gameDescription;

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

        Event event = eventGame.getKey().getEvent();
        this.eventStartDate = event.getStartDate();
        this.eventStartTime = event.getStartTime();
        this.eventEndDate = event.getEndDate();
        this.eventEndTime = event.getEndTime();
        this.eventMaxNumParticipants = event.getMaxNumParticipants();
        this.eventLocation = event.getLocation();
        this.eventDescription = event.getDescription();
        this.eventContactEmail = event.getContactEmail();
        this.eventCreatorId = event.getCreator().getId();

        Game game = eventGame.getKey().getGame();
        this.gameTitle = game.getTitle();
        this.gameMinNumPlayers = game.getMinNumPlayers();
        this.gameMaxNumPlayers = game.getMaxNumPlayers();
        this.gamePictureURL = game.getPictureURL();
        this.gameDescription = game.getDescription();
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

    public String getGameTitle() {
        return gameTitle;
    }
    
    public int getGameMinNumPlayers() {
        return gameMinNumPlayers;
    }
    
    public int getGameMaxNumPlayers() {
        return gameMaxNumPlayers;
    }
    
    public String getGamePictureURL() {
        return gamePictureURL;
    }
    
    public String getGameDescription() {
        return gameDescription;
    }
}
