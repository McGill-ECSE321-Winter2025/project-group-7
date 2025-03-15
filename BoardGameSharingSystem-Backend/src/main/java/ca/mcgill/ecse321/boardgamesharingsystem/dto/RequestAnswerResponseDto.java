package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;

/**
 * DTO for RequestAnswer responses.
 */
public class RequestAnswerResponseDto {

    private int id;
    private int requestId;
    private Date dropOffDate;
    private Time dropOffTime;
    private String location;
    private String contactEmail;

    //No args constructor for Jackson
    @SuppressWarnings("unused")
    private RequestAnswerResponseDto()
    {

    }
    
    /**
     * Constructor for the RequestAnswerResponseDto.
     */
    public RequestAnswerResponseDto(RequestAnswer requestAnswer) {
        this.id = requestAnswer.getId();
        this.requestId = requestAnswer.getRequest().getId();
        this.dropOffDate = requestAnswer.getDropOffDate();
        this.dropOffTime = requestAnswer.getDropOffTime();
        this.location = requestAnswer.getLocation();
        this.contactEmail = requestAnswer.getContactEmail();
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRequestId() {
        return requestId;
    }

    public Date getDropOffDate() {
        return dropOffDate;
    }

    public Time getDropOffTime() {
        return dropOffTime;
    }

    public String getLocation() {
        return location;
    }

    public String getContactEmail() {
        return contactEmail;
    }
}
