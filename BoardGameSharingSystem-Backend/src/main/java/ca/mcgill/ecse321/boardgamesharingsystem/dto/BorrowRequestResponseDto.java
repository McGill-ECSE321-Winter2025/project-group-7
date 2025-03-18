package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.time.LocalDate;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest.RequestStatus;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class BorrowRequestResponseDto {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserAccount borrower;
    private GameCopy gameCopy;
    private RequestStatus requestStatus;

    @SuppressWarnings("unused")
    private BorrowRequestResponseDto() {
        
    }

    public BorrowRequestResponseDto(BorrowRequest borrowRequest) {
        this.startDate = borrowRequest.getStartDate().toLocalDate();
        this.endDate = borrowRequest.getEndDate().toLocalDate();
        this.borrower = borrowRequest.getBorrower();
        this.gameCopy = borrowRequest.getGameCopy();
        this.requestStatus = borrowRequest.getRequestStatus();
        this.id = borrowRequest.getId();
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

    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }
}
