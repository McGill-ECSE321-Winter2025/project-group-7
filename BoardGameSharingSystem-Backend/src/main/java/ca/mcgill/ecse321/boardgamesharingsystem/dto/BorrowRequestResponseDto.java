package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.sql.Date;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest.RequestStatus;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

public class BorrowRequestResponseDto {
    private int id;
    private Date startDate;
    private Date endDate;
    private UserAccount borrower;
    private GameCopy gameCopy;
    private RequestStatus requestStatus;

    @SuppressWarnings("unused")
    private BorrowRequestResponseDto() {
        
    }

    public BorrowRequestResponseDto(BorrowRequest borrowRequest) {
        this.startDate = borrowRequest.getStartDate();
        this.endDate = borrowRequest.getEndDate();
        this.borrower = borrowRequest.getBorrower();
        this.gameCopy = borrowRequest.getGameCopy();
        this.requestStatus = borrowRequest.getRequestStatus();
    }

    public Integer getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
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
}
