package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EventDto {
    @FutureOrPresent(message = "startDate must not be in the past")
    private Date startDate;
    private Time startTime;
    @FutureOrPresent(message = "endDate must not be in the past")
    private Date endDate;
    private Time endTime;
    @Min(value = 1, message = "max number of participants must be at least 1")
    private int maxNumParticipants;
    @NotNull(message = "location must not be null")
    private String location;
    @NotNull(message = "description must not be null")
    private String description;
    @NotNull(message = "contactEmail must not be null")
    private String contactEmail;
    private int creatorId;

    public EventDto(Date startDate, Time startTime, Date endDate, Time endTime, int maxNumParticipants, String location, String description, String contactEmail, int creatorId)
    {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.maxNumParticipants = maxNumParticipants;
        this.location = location;
        this.description = description;
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
