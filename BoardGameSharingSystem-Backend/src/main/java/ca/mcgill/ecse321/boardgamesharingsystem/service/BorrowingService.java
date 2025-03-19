package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.time.LocalDate;
import java.sql.Time;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RequestAnswerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.model.GameCopy;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest.RequestStatus;
import org.springframework.validation.annotation.Validated;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import org.springframework.http.HttpStatus;

/**
 * This service class implements functionalities related to the borrowing of board games,
 * including creating, accepting, declining, and retrieving borrowing requests, as well
 * as managing request answers.
 */
@Service
@Validated
public class BorrowingService
{
    @Autowired
    private BorrowRequestRepository borrowingRequestRepository;
    @Autowired
    private RequestAnswerRepository requestAnswerRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private GameCopyRepository gameCopyRepository;

    /**
     * Creates a borrowing request.
     * @param gameCopyId The id of the game copy 
     * @param borrowerId The id of the borrower
     * @param startDate The start date of the borrowing
     * @param endDate The end date of the borrowing
     * @return The created borrowing request
     */
    @Transactional
    public BorrowRequest createBorrowingRequest(int gameCopyId, int borrowerId, LocalDate startDate, LocalDate endDate) {
        GameCopy foundGameCopy = gameCopyRepository.findById(gameCopyId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Game copy not found."));
        
        UserAccount foundBorrower = userAccountRepository.findById(borrowerId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrower not found."));

        if (startDate == null || endDate == null) throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Dates cannot be null.");

        if (!endDate.isAfter(startDate)) throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "End date must be after start date.");

        BorrowRequest newRequest = new BorrowRequest(startDate, endDate, foundBorrower, foundGameCopy);
        borrowingRequestRepository.save(newRequest);
        return newRequest;
    }

    /**
     * Finds all pending borrowing requests for a game copy.
     * @param gameCopyId The id of the game copy
     * @return The list of pending borrow request for the game copy
     */
    public List<BorrowRequest> findPendingBorrowingRequests(int gameCopyId) {
        gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Game copy not found."));

        List<BorrowRequest> pendingRequests = borrowingRequestRepository.findByGameCopyId(gameCopyId)
                .stream().filter(request -> request.getRequestStatus() == RequestStatus.Pending).toList();

        if (pendingRequests.isEmpty()) throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "No pending borrowing requests found for this game copy.");
        
        return pendingRequests;
    }

    /**
     * Find accepted borrwoing requests for a game copy.
     * @param gameCopyId The id of the game copy
     * @return The list of accepted borrow requests for the game copy
     */
    public List<BorrowRequest> findAcceptedBorrowingRequests(int gameCopyId) {
        gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Game copy not found."));

        List<BorrowRequest> acceptedRequests = borrowingRequestRepository.findByGameCopyId(gameCopyId)
                .stream().filter(request -> request.getRequestStatus() == RequestStatus.Accepted).toList();

        if (acceptedRequests.isEmpty()) throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "No accepted borrowing requests found for this game copy.");
        
        return acceptedRequests;
    }

    /**
     * Decline a pending borrow request.
     * @param borrowingRequestId The id of the borrow request
     * @return The borrow request that was declined
     */
    @Transactional
    public BorrowRequest declinePendingBorrowingRequest(int borrowingRequestId) {
        BorrowRequest foundRequest = borrowingRequestRepository.findById(borrowingRequestId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrowing request not found."));
            
        if (foundRequest.getRequestStatus() != RequestStatus.Pending)
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Cannot decline a non-pending borrowing request.");

        borrowingRequestRepository.delete(foundRequest);

        return foundRequest;
    }

    /**
     * Accept a pending borrow request.
     * @param borrowingRequestId The id of the pending borrow request
     * @return The accepted borrow request
     */
    @Transactional
    public BorrowRequest acceptPendingBorrowingRequest(int borrowingRequestId) {
        BorrowRequest foundRequest = borrowingRequestRepository.findById(borrowingRequestId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrowing request not found."));

        foundRequest.setRequestStatus(RequestStatus.Accepted);
        borrowingRequestRepository.save(foundRequest);

        return foundRequest;
    }

    /**
     * Creates a request answer.
     * @param borrowingRequestId The id of the borrow request
     * @param dropoffDate The dropoff date
     * @param dropoffTime The dropoff time
     * @param dropoffLocation The dropoff location
     * @param contactEmail The contact email
     * @return The created request answer
     */
    @Transactional
    public RequestAnswer createRequestAnswer(int borrowingRequestId, LocalDate dropoffDate, Time dropoffTime, String dropoffLocation, String contactEmail) {
        BorrowRequest foundRequest = borrowingRequestRepository.findById(borrowingRequestId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrowing request not found."));

        if (dropoffDate == null || dropoffTime == null || dropoffLocation.isBlank() || contactEmail.isBlank())
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Drop-off date, time, location, and contact email must be valid.");

        RequestAnswer requestAnswer = new RequestAnswer(dropoffDate, dropoffTime, dropoffLocation, foundRequest, contactEmail);
        System.out.println("Saving DropOffDate: " + dropoffDate);

        return requestAnswerRepository.save(requestAnswer);
    }

    /**
     * Updates a request answer.
     * @param requestAnswerId The id of the request answer
     * @param dropoffDate The new dropoff date
     * @param dropoffTime The new dropoff time
     * @param dropoffLocation The new dropoff location
     * @return The updated request answer
     */
    @Transactional
    public RequestAnswer updateRequestAnswer(int requestAnswerId, LocalDate dropoffDate, Time dropoffTime, String dropoffLocation) {
        RequestAnswer foundAnswer = requestAnswerRepository.findById(requestAnswerId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Request answer not found."));

        if (dropoffDate == null || dropoffTime == null || dropoffLocation.isBlank())
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Drop-off date, time, and location must be valid.");

        foundAnswer.setDropOffDate(dropoffDate);
        foundAnswer.setDropOffTime(dropoffTime);
        foundAnswer.setLocation(dropoffLocation);
        
        return requestAnswerRepository.save(foundAnswer);
    }

    /**
     * Deletes a request answer
     * @param requestAnswerId The id of the request answer
     */
    @Transactional
    public void deleteRequestAnswer(int requestAnswerId) {
        RequestAnswer foundAnswer = requestAnswerRepository.findById(requestAnswerId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Request answer not found."));

        requestAnswerRepository.delete(foundAnswer);
    }

    /**
     * Finds a request answer for a given borrowing request.
     * @param borrowingRequestId
     * @return The request answer for the borrowing request
     */
    public RequestAnswer findRequestAnswerForBorrowingRequest(int borrowingRequestId) {
        BorrowRequest foundRequest = borrowingRequestRepository.findById(borrowingRequestId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrowing request not found."));

        if (foundRequest.getRequestStatus() != RequestStatus.Accepted)
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Cannot find request answer for a non-accepted borrowing request.");

        RequestAnswer foundAnswer = requestAnswerRepository.findRequestAnswerByRequestId(borrowingRequestId);

        if (foundAnswer == null)
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "No request answer found for this borrowing request.");

        return foundAnswer;
    }
}
