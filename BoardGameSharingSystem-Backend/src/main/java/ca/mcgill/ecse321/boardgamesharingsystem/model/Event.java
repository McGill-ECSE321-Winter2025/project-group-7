package ca.mcgill.ecse321.boardgamesharingsystem.model;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private int id;
    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;
    private int maxNumParticipants;
    private String location;
    private String description;
    private String contactEmail;
    @ManyToOne
    private UserAccount creator;

    protected Event(){

    }

    public Event(Date startDate, Time startTime, Date endDate, Time endTime, int maxNumParticipants, String location, String description, String contactEmail, UserAccount creator)
    {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.maxNumParticipants = maxNumParticipants;
        this.location = location;
        this.description = description;
        this.contactEmail = contactEmail;
        this.creator = creator;
    }
    public int getId()
    {
        return id;
    }
    public Date getStartDate()
    {
        return startDate;
    }
    public Time getStartTime()
    {
        return startTime;
    }
    public Date getEndDate()
    {
        return endDate;
    }
    public Time getEndTime()
    {
        return endTime;
    }
    public int getMaxNumParticipants()
    {
        return maxNumParticipants;
    }
    public String getLocation()
    {
        return location;
    }
    public String getDescription()
    {
        return description;
    }
    public String getContactEmail()
    {
        return contactEmail;
    }
    public UserAccount getCreator()
    {
        return creator;
    }
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }
    public void setStartTime(Time startTime)
    {
        this.startTime = startTime;
    }
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
    public void setEndTime(Time endTime)
    {
        this.endTime = endTime;
    }
    public void setMaxNumParticipants(int maxNumParticipants)
    {
        this.maxNumParticipants = maxNumParticipants;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }
}