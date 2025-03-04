package ca.mcgill.ecse321.boardgamesharingsystem.dto;

public class GameCreationDto {
    private String title;
    private int minNumPlayers;
    private int maxNumPlayers;
    private String pictureURL;
    private String description;

    public GameCreationDto(String title, int minNumPlayers, int maxNumPlayers, String pictureURL, String description){
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
