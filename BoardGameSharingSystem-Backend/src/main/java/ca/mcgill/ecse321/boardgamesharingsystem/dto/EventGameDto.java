package ca.mcgill.ecse321.boardgamesharingsystem.dto;

public class EventGameDto {
    private int eventId;
    private int gameId;
    public EventGameDto(int eventId, int gameId)
    {
        this.eventId = eventId;
        this.gameId = gameId;
    }

    public int getEventId()
    {
        return eventId;
    }

    public int getGameId()
    {
        return gameId;
    }
}
