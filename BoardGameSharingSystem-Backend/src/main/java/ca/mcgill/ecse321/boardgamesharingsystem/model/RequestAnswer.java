package ca.mcgill.ecse321.boardgamesharingsystem.model;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * A class that defines the game owner's answer of the request
 */

@Entity
public class RequestAnswer {
    @Id
    @GeneratedValue
    private int id;
    private Date dropOffDate;
    private Time dropOffTime;
    private String location;

    @OneToOne
    private BorrowRequest request;
    private String contactEmail;

    protected RequestAnswer(){

    }

    public RequestAnswer(Date dropOffDate, Time dropOffTime, String location, BorrowRequest request, String contactEmail) {
        this.dropOffDate = dropOffDate;
        this.location= location;
        this.request = request;
        this.contactEmail = contactEmail;
        this.dropOffTime = dropOffTime;
    }

    public int getId() {
        return id;
    }

    public Date getDropOffDate() {
        return dropOffDate;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getLocation() {
        return location;
    }

    public Time getDropOffTime(){
        return dropOffTime;
    }

    public BorrowRequest getRequest() {
        return request;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setDropOffDate(Date dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public void setDropOffTime(Time dropOffTime) {
        this.dropOffTime = dropOffTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}