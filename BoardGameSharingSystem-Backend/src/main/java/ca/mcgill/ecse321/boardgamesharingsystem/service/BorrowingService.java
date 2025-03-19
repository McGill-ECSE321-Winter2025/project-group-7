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

    @Transactional(readOnly = true)
    public List<BorrowRequest> findPendingBorrowingRequests(int gameCopyId) {
        gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Game copy not found."));

        List<BorrowRequest> pendingRequests = borrowingRequestRepository.findByGameCopyId(gameCopyId)
                .stream().filter(request -> request.getRequestStatus() == RequestStatus.Pending).toList();

        if (pendingRequests.isEmpty()) throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "No pending borrowing requests found for this game copy.");
        
        return pendingRequests;
    }

    @Transactional(readOnly = true)
    public List<BorrowRequest> findAcceptedBorrowingRequests(int gameCopyId) {
        gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Game copy not found."));

        List<BorrowRequest> acceptedRequests = borrowingRequestRepository.findByGameCopyId(gameCopyId)
                .stream().filter(request -> request.getRequestStatus() == RequestStatus.Accepted).toList();

        if (acceptedRequests.isEmpty()) throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "No accepted borrowing requests found for this game copy.");
        
        return acceptedRequests;
    }

    @Transactional
    public BorrowRequest declinePendingBorrowingRequest(int borrowingRequestId) {
        BorrowRequest foundRequest = borrowingRequestRepository.findById(borrowingRequestId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrowing request not found."));
            
        if (foundRequest.getRequestStatus() != RequestStatus.Pending)
            throw new BoardGameSharingSystemException(HttpStatus.BAD_REQUEST, "Cannot decline a non-pending borrowing request.");

        borrowingRequestRepository.delete(foundRequest);

        return foundRequest;
    }

    @Transactional
    public BorrowRequest acceptPendingBorrowingRequest(int borrowingRequestId) {
        BorrowRequest foundRequest = borrowingRequestRepository.findById(borrowingRequestId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Borrowing request not found."));

        foundRequest.setRequestStatus(RequestStatus.Accepted);
        borrowingRequestRepository.save(foundRequest);

        return foundRequest;
    }

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

    @Transactional
    public void deleteRequestAnswer(int requestAnswerId) {
        RequestAnswer foundAnswer = requestAnswerRepository.findById(requestAnswerId)
            .orElseThrow(() -> new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "Request answer not found."));

        requestAnswerRepository.delete(foundAnswer);
    }

    @Transactional(readOnly = true)
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
