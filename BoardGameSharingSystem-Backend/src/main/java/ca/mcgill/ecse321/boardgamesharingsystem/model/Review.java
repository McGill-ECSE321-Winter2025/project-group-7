package ca.mcgill.ecse321.boardgamesharingsystem.model;
import java.sql.Date;
import java.sql.Time;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {
    @Id @GeneratedValue private int id;
    private Date reviewDate;
    private int rating;
    private String comment;

    @ManyToOne UserAccount reviewer;
    @ManyToOne Game game;

    public Review(Date reviewDate, int rating, String comment) {
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
