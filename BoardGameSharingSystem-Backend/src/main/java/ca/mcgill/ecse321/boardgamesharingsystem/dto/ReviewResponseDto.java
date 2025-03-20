package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;

public class ReviewResponseDto {
    private int id;
    private Date reviewDate;
    private int rating;
    private String comment;
    private int gameId;
    private int userId;

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
}
