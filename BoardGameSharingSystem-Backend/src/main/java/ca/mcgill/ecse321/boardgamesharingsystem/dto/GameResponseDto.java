package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;

public class GameResponseDto {
    private int id;
    private String title;
    private int minNumPlayers;
    private int maxNumPlayers;
    private String pictureURL;
    private String description;

    @SuppressWarnings("unused")
    private GameResponseDto() {

    }

    public GameResponseDto(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.minNumPlayers = game.getMinNumPlayers();
        this.maxNumPlayers = game.getMaxNumPlayers();
        this.pictureURL = game.getPictureURL();
        this.description = game.getDescription();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getMinNumPlayers() {
        return minNumPlayers;
    }

    public int getMaxNumPlayers() {
        return maxNumPlayers;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public String getDescription() {
        return description;
    }
}
