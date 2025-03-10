package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class GameRequestDto {
    @NotBlank(message = "The game must have a title.")
    private String title;
    @Positive(message = "The game must have a positive minNumPlayers.")
    private int minNumPlayers;
    @Positive(message = "The game must have a positive maxNUmPlayers.")
    private int maxNumPlayers;
    private String pictureURL;
    @NotBlank(message = "The game must have a description.")
    private String description;

    public GameRequestDto(String title, int minNumPlayers, int maxNumPlayers, String pictureURL, String description){
        this.title= title;
        this.minNumPlayers= minNumPlayers;
        this.maxNumPlayers= maxNumPlayers;
        this.pictureURL= pictureURL;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public int getMaxNumPlayers() {
        return maxNumPlayers;
    }

    public int getMinNumPlayers() {
        return minNumPlayers;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public String getDescription() {
        return description;
    }
}
