package ca.mcgill.ecse321.boardgamesharingsystem.model;
import java.sql.Date;
import java.sql.Time;


public class Review {
    private int id;
    private Date reviewDate;
    private int rating;
    private String comment;

    public Review(Date reviewDate, int rating, String conmment) {
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.comment = comment;

    }

    public int getID(){
        return this.id;

    }
    public Date getReviewDate(){
        return this.reviewDate;
    }

    public String getComment(){
        return this.comment;

    }


}
