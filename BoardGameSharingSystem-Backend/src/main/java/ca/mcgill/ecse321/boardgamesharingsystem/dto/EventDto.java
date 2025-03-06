package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EventDto {
    @FutureOrPresent
    private Date startDate;
    @FutureOrPresent
    private Time startTime;
    @Future
    private Date endDate;
    @Future
    private Time endTime;
    @Min(1)
    private int maxNumParticipants;
    @NotNull
    private String location;
    @NotNull
    private String description;
    @NotNull
    private String contactEmail;
    private int creatorId;

    public EventDto(Date startDate, Time startTime, Date endDate, Time endTime, int maxNumParticipants, String location, String description, String contactEmail, int creatorId)
    {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.maxNumParticipants = maxNumParticipants;
        this.location = location == null ? "" : location;
        this.description = description == null ? "" : description;
        this.contactEmail = contactEmail;
        this.creatorId = creatorId;
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

    public int getCreatorId() 
    {
        return creatorId;
    }
}
