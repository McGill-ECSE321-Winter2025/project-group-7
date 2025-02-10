package ca.mcgill.ecse321.boardgamesharingsystem.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
public class BorrowRequest{
    @Id
    @GeneratedValue
    private int id;

    private Date startDate;
    private Date endDate;

    @ManyToOne
    private UserAccount borrower;

    @ManyToOne
    private GameCopy gameCopy;

    @Enumerated(EnumType.STRING) // 存储枚举值的字符串
    private RequestStatus requestStatus;

    public enum RequestStatus {
        Pending, Accepted, Rejected
    }

    protected BorrowRequest() {
    }

    public BorrowRequest(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestStatus = RequestStatus.Pending;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Status getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Status requestStatus) {
        this.requestStatus = requestStatus;
    }
}