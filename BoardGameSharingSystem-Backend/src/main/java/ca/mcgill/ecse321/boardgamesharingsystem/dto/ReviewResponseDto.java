package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;

public class ReviewResponseDto {
    int id;
    private Date reviewDate;
    private int rating;
    private String comment;
    int gameId;
    int userId;

    public ReviewResponseDto(){}

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.reviewDate = review.getReviewDate();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.userId = review.getReviewer().getId();
        this.gameId = review.getGame().getId();
    }

    public int getId() {
        return id;
    }

    public Date getReviewDate(){
        return reviewDate;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public int getGameId() {
        return gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setId(int id) {
        this.id = id;
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
