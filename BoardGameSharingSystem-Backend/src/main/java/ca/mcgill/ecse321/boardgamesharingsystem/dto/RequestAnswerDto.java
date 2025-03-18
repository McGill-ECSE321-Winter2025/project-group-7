package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;
import java.sql.Time;

public class RequestAnswerDto {
    private int id;
    private int borrowingRequestId;
    private Date dropoffDate;
    private Time dropoffTime;
    private String dropoffLocation;
    private String contactEmail;

    @SuppressWarnings("unused")
    public RequestAnswerDto() {

    }

    public RequestAnswerDto(int id, int borrowingRequestId, Date dropoffDate, Time dropoffTime, String dropoffLocation, String contactEmail) {
        this.id = id;
        this.borrowingRequestId = borrowingRequestId;
        this.dropoffDate = dropoffDate;
        this.dropoffTime = dropoffTime;
        this.dropoffLocation = dropoffLocation;
        this.contactEmail = contactEmail;
    }

    // Getters and Setters
    public int getId() { 
        return id; 
    }

    public void setId(int id) { 
        this.id = id; 
    }

    public int getBorrowingRequestId() { 
        return borrowingRequestId; 
    }

    public void setBorrowingRequestId(int borrowingRequestId) { 
        this.borrowingRequestId = borrowingRequestId; 
    }

    public Date getDropoffDate() { 
        return dropoffDate; 
    }

    public void setDropoffDate(Date dropoffDate) { 
        this.dropoffDate = dropoffDate; 
    }

    public Time getDropoffTime() { 
        return dropoffTime; 
    }

    public void setDropoffTime(Time dropoffTime) { 
        this.dropoffTime = dropoffTime; 
    }

    public String getDropoffLocation() { 
        return dropoffLocation; 
    }

    public void setDropoffLocation(String dropoffLocation) { 
        this.dropoffLocation = dropoffLocation; 
    }

    public String getContactEmail() { 
        return contactEmail; 
    }

    public void setContactEmail(String contactEmail) { 
        this.contactEmail = contactEmail; 
    }
}
