package ca.mcgill.ecse321.boardgamesharingsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private int id;
    private String title;
    private int minNumPlayers;
    private int maxNumPlayers;
    private String pictureURL;
    private String description;

    protected Game(){

    }

    public Game(String title, int minNumPlayers, int maxNumPlayers, String pictureURL, String description){
        this.title= title;
        this.minNumPlayers= minNumPlayers;
        this.maxNumPlayers= maxNumPlayers;
        this.pictureURL= pictureURL;
        this.description = description;
    }

    public int getId() {
        return id;
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