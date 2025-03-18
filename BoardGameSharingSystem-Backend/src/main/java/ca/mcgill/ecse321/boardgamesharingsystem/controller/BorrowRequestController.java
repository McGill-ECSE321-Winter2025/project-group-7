package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.BorrowRequestResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.service.BorrowingService;

@RestController
public class BorrowRequestController {
    @Autowired
    private BorrowingService borrowingService;

    @PostMapping("/borrowrequests")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDto createBorrowingRequest(@RequestParam int gameCopyId, @RequestParam int borrowerId, @RequestBody BorrowRequestResponseDto borrowRequest) 
    {
        return new BorrowRequestResponseDto(borrowingService.createBorrowingRequest(gameCopyId, borrowerId, borrowRequest.getStartDate(), borrowRequest.getEndDate()));
    }
    
    @GetMapping("/borrowrequests/{gameCopyId}/pending")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> findPendingBorrowingRequests(@PathVariable int gameCopyId) {
        List<BorrowRequest> pendingRequests = borrowingService.findPendingBorrowingRequests(gameCopyId);
    
        if (pendingRequests.isEmpty()) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, 
                "No pending borrowing requests found for game copy with id " + gameCopyId);
        }
    
        return pendingRequests.stream()
                              .map(BorrowRequestResponseDto::new)
                              .toList();
    }
    
    @GetMapping("/borrowrequests/{gameCopyId}/accepted")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> findAcceptedBorrowingRequests(@PathVariable int gameCopyId) {
        List<BorrowRequest> acceptedRequests = borrowingService.findAcceptedBorrowingRequests(gameCopyId);

        if (acceptedRequests.isEmpty()) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, 
                "No accepted borrowing requests found for game copy with id " + gameCopyId);
        }

        return acceptedRequests.stream()
                            .map(BorrowRequestResponseDto::new)
                            .toList();
    }


    @DeleteMapping("/borrowrequests/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto declinePendingBorrowingRequest(@PathVariable int id) 
    {
        return new BorrowRequestResponseDto(borrowingService.declinePendingBorrowingRequest(id));
    }

    @PutMapping("/borrowrequests/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto acceptPendingBorrowingRequest(@PathVariable int id)
    {
        return new BorrowRequestResponseDto(borrowingService.acceptPendingBorrowingRequest(id));
    }
}
