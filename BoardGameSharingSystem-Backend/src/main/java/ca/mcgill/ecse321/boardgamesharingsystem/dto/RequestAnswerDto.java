public class RequestAnswerDto {
    private Long id;
    private Long borrowingRequestId;
    private String dropoffDate;
    private String dropoffTime;
    private String dropoffLocation;

    // Constructors
    public RequestAnswerDto() {}

    public RequestAnswerDto(Long id, Long borrowingRequestId, String dropoffDate, String dropoffTime, String dropoffLocation) {
        this.id = id;
        this.borrowingRequestId = borrowingRequestId;
        this.dropoffDate = dropoffDate;
        this.dropoffTime = dropoffTime;
        this.dropoffLocation = dropoffLocation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBorrowingRequestId() { return borrowingRequestId; }
    public void setBorrowingRequestId(Long borrowingRequestId) { this.borrowingRequestId = borrowingRequestId; }

    public String getDropoffDate() { return dropoffDate; }
    public void setDropoffDate(String dropoffDate) { this.dropoffDate = dropoffDate; }

    public String getDropoffTime() { return dropoffTime; }
    public void setDropoffTime(String dropoffTime) { this.dropoffTime = dropoffTime; }

    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
}
