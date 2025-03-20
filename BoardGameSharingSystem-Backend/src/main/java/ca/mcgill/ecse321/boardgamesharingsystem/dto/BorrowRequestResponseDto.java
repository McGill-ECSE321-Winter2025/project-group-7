package ca.mcgill.ecse321.boardgamesharingsystem.dto;

import java.time.LocalDate;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest.RequestStatus;

public class BorrowRequestResponseDto {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private int borrowerId;
    private String borrowerName;
    private String borrowerEmail;
    private int gameCopyId;
    private String gameTitle;
    private int ownerId;
    private String ownerName;
    private String ownerEmail;

    private RequestStatus requestStatus;

    @SuppressWarnings("unused")
    private BorrowRequestResponseDto() {
        
    }

    public BorrowRequestResponseDto(BorrowRequest borrowRequest) {
        this.startDate = borrowRequest.getStartDate();
        this.endDate = borrowRequest.getEndDate();
        this.borrowerId = borrowRequest.getBorrower().getId();
        this.borrowerName = borrowRequest.getBorrower().getName();
        this.borrowerEmail = borrowRequest.getBorrower().getEmail();
        this.gameCopyId = borrowRequest.getGameCopy().getId();
        this.gameTitle = borrowRequest.getGameCopy().getGame().getTitle();
        this.ownerName = borrowRequest.getGameCopy().getGameOwner().getUser().getName();
        this.ownerEmail = borrowRequest.getGameCopy().getGameOwner().getUser().getEmail();
        this.ownerId = borrowRequest.getGameCopy().getGameOwner().getId();
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

    public int getBorrowerId() {
        return borrowerId;
    }
    
    public String getBorrowerName() {
        return borrowerName;
    }

    public int getGameCopyId() {
        return gameCopyId;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }
    public String getBorrowerEmail() {
        return borrowerEmail;
    }
    public String getGameTitle() {
        return gameTitle;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }
    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
