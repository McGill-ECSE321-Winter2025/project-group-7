package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.service.BorrowingService;

@CrossOrigin(origins="http://localhost:8090")
@RestController
public class BorrowRequestController {
    @Autowired
    private BorrowingService borrowingService;

    /**
     * Creates a new borrowing request.
     * @param gameCopyId the id of the game copy 
     * @param borrowerId the id of the borrower
     * @param borrowRequest the Request DTO to create the borrow request
     * @return information contained in a borrowing request 
     */
    @PostMapping("/borrowrequests")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDto createBorrowingRequest(@RequestParam("gameCopyId") int gameCopyId, @RequestParam("borrowerId") int borrowerId, @RequestBody BorrowRequestResponseDto borrowRequest) 
    {
        return new BorrowRequestResponseDto(borrowingService.createBorrowingRequest(gameCopyId, borrowerId, borrowRequest.getStartDate(), borrowRequest.getEndDate()));
    }

    //new
    /**
     * Returns a list of all Borrow Requests.
     * @return a list of all Borrow Requests
     */
    @GetMapping("/borrowrequests/all")
    public List<BorrowRequestResponseDto> findAllRequests()
    {
        List<BorrowRequest> requests = borrowingService.findAllRequests();
        List<BorrowRequestResponseDto> responses = new ArrayList<>();
        requests.forEach(borrowRequest -> responses.add(new BorrowRequestResponseDto(borrowRequest)));
        return responses;
    }
    
    /**
     * Finds all pending borrowing requests for a game copy.
     * @param gameCopyId the id of the game copy 
     * @return a list of information contained in a borrowing request
     */
    @GetMapping("/borrowrequests/{gameCopyId}/pending")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> findPendingBorrowingRequests(@PathVariable("gameCopyId") int gameCopyId) {
        List<BorrowRequest> pendingRequests = borrowingService.findPendingBorrowingRequests(gameCopyId);
    
        return pendingRequests.stream()
                              .map(BorrowRequestResponseDto::new)
                              .toList();
    }
    
    /**
     * Finds all accepted borrowing requests for a game copy. 
     * @param gameCopyId the id of the game copy 
     * @return the list containing information about borrow request for the game copy 
     */
    @GetMapping("/borrowrequests/{gameCopyId}/accepted")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> findAcceptedBorrowingRequests(@PathVariable("gameCopyId") int gameCopyId) {
        List<BorrowRequest> acceptedRequests = borrowingService.findAcceptedBorrowingRequests(gameCopyId);

        return acceptedRequests.stream()
                            .map(BorrowRequestResponseDto::new)
                            .toList();
    }

    /**
     * Declines a pending borrowing request.
     * @param id the id of the borrowing request 
     * @return the information about the declined borrowing request
     */
    
    @DeleteMapping("/borrowrequests/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto declinePendingBorrowingRequest(@PathVariable("id") int id) 
    {
        return new BorrowRequestResponseDto(borrowingService.declinePendingBorrowingRequest(id));
    }
    /**
     * Accepts pending borrowing request.
     * @param id the id of the borrowing request 
     * @return the accepted borrowing 
     */
    @PutMapping("/borrowrequests/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto acceptPendingBorrowingRequest(@PathVariable("id") int id)
    {
        return new BorrowRequestResponseDto(borrowingService.acceptPendingBorrowingRequest(id));
    }
}
