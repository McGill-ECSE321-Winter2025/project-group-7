package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;

public class ReviewDto {
    private int id;
    private Date reviewDate;
    private int rating;
    private String comment;
    private int userId;
    private int gameId;

    public ReviewDto() {}

    public ReviewDto(Date reviewDate, int rating, String comment, int userId, int gameId, int id) {
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.comment = comment; 
        this.userId = userId;
        this.gameId = gameId;
        this.id = id;
    }

    public int getId(){
        return this.id;
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

    public int getUserId() {
        return this.userId;
    }

    public int getGameId() {
        return this.gameId;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
