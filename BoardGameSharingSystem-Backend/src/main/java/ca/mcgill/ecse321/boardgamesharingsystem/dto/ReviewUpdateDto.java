package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;

public class ReviewUpdateDto {
    //TODO
    private Date reviewDate;
    private int rating;
    private String comment;

    ReviewUpdateDto(Date reviewDate, int rating, String comment) {
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.comment = comment;

    }

    
}
