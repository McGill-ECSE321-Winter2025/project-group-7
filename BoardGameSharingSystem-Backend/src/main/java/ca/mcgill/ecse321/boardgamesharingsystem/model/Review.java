package ca.mcgill.ecse321.boardgamesharingsystem.model;
import java.sql.Date;
import java.sql.Time;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;

@Entity
//@Table(name = "Review")
public class Review {
    @Id 
    @GeneratedValue 
    //@Column(updatable = false, nullable = false, unique = true)
    private int id;

   // @Column
    private Date reviewDate;
    private int rating;
    private String comment;

    @ManyToOne 
    UserAccount reviewer;
    @ManyToOne 
    Game game;

    public Review(Date reviewDate, int rating, String comment, UserAccount reviewer, Game game) {
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.comment = comment;
        this.reviewer = reviewer;
        this.game = game;

    }

    public int getID(){
        return this.id;

    }

    public Date getReviewDate(){
        return this.reviewDate;
    
    }

    public int getRating(){
        return this.rating;

    }

    public String getComment(){
        return this.comment;

    }

    public Game getGame(){
        return this.game;

    }

    public UserAccount getReviewer() {
        return this.reviewer;

    }

    public void setComment(String comment){
        this.comment = comment; 

    }

    public void setReviewDate(Date reviewDate){
        this.reviewDate = reviewDate;

    }

    public void setRating(int rating){
        this.rating = rating;

    }



}
