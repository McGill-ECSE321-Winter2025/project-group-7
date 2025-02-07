package ca.mcgill.ecse321.boardgamesharingsystem.model;

import java.sql.Date;
import java.sql.Time;

public class Event {
    private int id;
    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;
    private int maxNumParticipants;
    private String location;
    private String description;

    public Event(Date startDate, Time startTime, Date endDate, Time endTime, int maxNumParticipants, String location, String description)
    {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.maxNumParticipants = maxNumParticipants;
        this.location = location;
        this.description = description;
    }
    public int getId()
    {
        return id;
    }
    public int getStartDate()
    {
        return startDate;
    }
    public int getStartTime()
    {
        return startTime;
    }
    public int getEndDate()
    {
        return endDate;
    }
    public int getEndTime()
    {
        return endTime;
    }
    public int getMaxNumParticipants()
    {
        return maxNumParticipants;
    }
    public int getLocation()
    {
        return location;
    }
    public int getDescription()
    {
        return description;
    }
}