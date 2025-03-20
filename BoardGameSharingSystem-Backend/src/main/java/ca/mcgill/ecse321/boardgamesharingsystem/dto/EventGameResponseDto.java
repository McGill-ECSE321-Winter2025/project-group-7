package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;

public class EventGameResponseDto {
    private int eventId;
    private int gameId;
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
        responseDate = LocalDate.now();
        responseTime = LocalTime.now();

        Event event = eventGame.getKey().getEvent();
        this.eventStartDate = event.getStartDate().toLocalDate();
        this.eventStartTime = event.getStartTime().toLocalTime();
        this.eventEndDate = event.getEndDate().toLocalDate();
        this.eventEndTime = event.getEndTime().toLocalTime();
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
