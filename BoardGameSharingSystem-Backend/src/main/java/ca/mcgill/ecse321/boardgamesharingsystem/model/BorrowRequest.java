package ca.mcgill.ecse321.boardgamesharingsystem.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class BorrowRequest{
    @Id
    @GeneratedValue
    private int id;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private UserAccount borrower;

    @ManyToOne
    private GameCopy gameCopy;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    public enum RequestStatus {
        Pending, Accepted, Rejected
    }

    protected BorrowRequest() {
    }

    public BorrowRequest(LocalDate startDate, LocalDate endDate, UserAccount borrower, GameCopy gameCopy) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.borrower = borrower;
        this.gameCopy = gameCopy;
        this.requestStatus = RequestStatus.Pending;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public UserAccount getBorrower() {
        return borrower;
    }

    public GameCopy getGameCopy() {
        return gameCopy;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setBorrower(UserAccount borrower) {
        this.borrower = borrower;
    }

    public void setGameCopy(GameCopy gameCopy) {
        this.gameCopy = gameCopy;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}