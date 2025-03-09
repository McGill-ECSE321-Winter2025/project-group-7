package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;


public class GameCopyDto {
    private Long id;
    private Long gameId;
    private Long ownerId;
    private String condition;

    // Constructors
    public GameCopyDto() {}

    public GameCopyDto(Long id, Long gameId, Long ownerId, String condition) {
        this.id = id;
        this.gameId = gameId;
        this.ownerId = ownerId;
        this.condition = condition;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
