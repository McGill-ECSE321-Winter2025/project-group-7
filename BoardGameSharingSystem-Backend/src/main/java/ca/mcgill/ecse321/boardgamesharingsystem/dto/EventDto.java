package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EventDto {
    @FutureOrPresent(message = "startDate must not be in the past")
    private LocalDate startDate;
    private LocalTime startTime;
    @FutureOrPresent(message = "endDate must not be in the past")
    private LocalDate endDate;
    private LocalTime endTime;
    @Min(value = 1, message = "max number of participants must be at least 1")
    private int maxNumParticipants;
    @NotNull(message = "location must not be null")
    private String location;
    @NotNull(message = "description must not be null")
    private String description;
    @NotNull(message = "contactEmail must not be null")
    private String contactEmail;
    private int creatorId;

    @SuppressWarnings("unused")
    private EventDto(){}

    public EventDto(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, int maxNumParticipants, String location, String description, String contactEmail, int creatorId)
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
}
