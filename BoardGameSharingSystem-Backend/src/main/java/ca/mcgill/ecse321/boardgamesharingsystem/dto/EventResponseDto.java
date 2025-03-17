package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;

public class EventResponseDto {
    private int id;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private int maxNumParticipants;
    private String location;
    private String description;
    private String contactEmail;
    private int creatorId;
    private LocalDate requestDate;
    private LocalTime requestTime;

    // No args constructor necessary for Jackson
    @SuppressWarnings("unused")
    private EventResponseDto()
    {

    }

    public EventResponseDto(Event event)
    {
        this.id = event.getId();
        this.startDate = event.getStartDate().toLocalDate();
        this.startTime = event.getStartTime().toLocalTime();
        this.endDate = event.getEndDate().toLocalDate();
        this.endTime = event.getEndTime().toLocalTime();
        this.maxNumParticipants = event.getMaxNumParticipants();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.contactEmail = event.getContactEmail();
        this.creatorId = event.getCreator().getId();
        requestDate = LocalDate.now();
        requestTime = LocalTime.now();
    }

    public int getId() 
    {
        return id;
    }
    
    public LocalDate getStartDate() 
    {
        return startDate;
    }
    
    public LocalTime getStartTime() 
    {
        return startTime;
    }
    
    public LocalDate getEndDate() 
    {
        return endDate;
    }
    
    public LocalTime getEndTime() 
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
    
    public LocalDate getRequestDate() 
    {
        return requestDate;
    }
    
    public LocalTime getRequestTime() 
    {
        return requestTime;
    }
}
