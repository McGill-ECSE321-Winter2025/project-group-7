package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;

public class GameCopyResponseDto {
    private int id;
    private int gameId;
    private int ownerId;
    private String gameDescription;
    private int gameMaxNumPlayers;
    private int gameMinNumPlayers;
    private String gamePicture;
    private String gameTitle;
    private String userEmail;
    private String userName;
    private int userId;

    @SuppressWarnings("unused")
    private GameCopyResponseDto() {

    }

    public GameCopyResponseDto(GameCopy gameCopy) {
        this.id = gameCopy.getId();
        this.gameId = gameCopy.getGame().getId();
        this.ownerId = gameCopy.getGameOwner().getId();
        this.gameDescription = gameCopy.getGame().getDescription();
        this.gameMaxNumPlayers = gameCopy.getGame().getMaxNumPlayers();
        this.gameMinNumPlayers = gameCopy.getGame().getMinNumPlayers();
        this.gamePicture = gameCopy.getGame().getPictureURL();
        this.gameTitle = gameCopy.getGame().getTitle();
        this.userEmail = gameCopy.getGameOwner().getUser().getEmail();
        this.userName = gameCopy.getGameOwner().getUser().getName();
        this.userId = gameCopy.getGameOwner().getUser().getId();
    }

    public int getId() {
        return id;
    }
    public int getGameId() {
        return gameId;
    }
    public int getOwnerId() {
        return ownerId;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public int getGameMaxNumPlayers() {
        return gameMaxNumPlayers;
    }

    public int getGameMinNumPlayers() {
        return gameMinNumPlayers;
    }

    public String getGamePicture() {
        return gamePicture;
    }
    public String getGameTitle() {
        return gameTitle;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public String getUserName() {
        return userName;
    }
    public int getUserId() {
        return userId;
    }
}
