package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;

public class GameCopyResponseDto {
    private int id;
    private int gameId;
    private int ownerId;

    public GameCopyResponseDto(GameCopy gameCopy) {
        this.id = gameCopy.getId();
        this.gameId = gameCopy.getGame().getId();
        this.ownerId = gameCopy.getGameOwner().getId();
    }

    public int getId() { return id; }
    public int getGameId() { return gameId; }
    public int getOwnerId() { return ownerId; }

    public void setId(int id) { this.id = id; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
}
