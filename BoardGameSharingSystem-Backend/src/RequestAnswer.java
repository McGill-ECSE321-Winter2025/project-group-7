//package ca.mcgill.ecse321.eventregistration.model;

import java.sql.Date;
import java.sql.Time;

/**
 * A class that defines the game owner's answer of the request
 */

public class RequestAnswer {
    private int id;
    private Date dropOffDate;
    private Time dropOffTime;
    private String location;
    private String contactEmail;


    public RequestAnswer(Date dropOffDate, Time dropOffTime, String location, String contactEmail) {
        this.dropOffDate = dropOffDate;
        this.location= location;
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
