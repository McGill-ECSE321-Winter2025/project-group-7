package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class ReviewCreationDto {
    private Date reviewDate;
    private int rating;
    private String comment;
    private UserAccount reviewer;
    private Game game;

    public ReviewCreationDto(Date reviewDate, int rating, String comment, UserAccount reviewer, Game game) {
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.comment = comment; 
        this.reviewer = reviewer;
        this.game = game;

    }

    public String getComment(){
        return this.comment;

    }

    public int getRating() {
        return  this.rating;

    }
    
    public Date getReviewDate() {
        return this.reviewDate;

    }

    public UserAccount getUser() {
        return this.reviewer;

    }

    public Game getGame() {
        return this.game;

    }

}
