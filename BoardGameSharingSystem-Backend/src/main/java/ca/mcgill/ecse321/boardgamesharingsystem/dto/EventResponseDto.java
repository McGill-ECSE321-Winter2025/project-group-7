package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;

public class EventResponseDto {
    private int id;
    private Date startDate;
    private Time startTime;
    private Date endDate;
    private Time endTime;
    private int maxNumParticipants;
    private String location;
    private String description;
    private String contactEmail;
    private int creatorId;
    private Date requestDate;
    private Time requestTime;

    // No args constructor necessary for Jackson
    @SuppressWarnings("unused")
    private EventResponseDto()
    {

    }

    public EventResponseDto(Event event)
    {
        this.id = event.getId();
        this.startDate = event.getStartDate();
        this.startTime = event.getStartTime();
        this.endDate = event.getEndDate();
        this.endTime = event.getEndTime();
        this.maxNumParticipants = event.getMaxNumParticipants();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.contactEmail = event.getContactEmail();
        this.creatorId = event.getCreator().getId();
        requestDate = Date.valueOf(LocalDate.now());
        requestTime = Time.valueOf(LocalTime.now());
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
    
    public int getCreatorId() 
    {
        return creatorId;
    }
    
    public Date getRequestDate() 
    {
        return requestDate;
    }
    
    public Time getRequestTime() 
    {
        return requestTime;
    }
}
