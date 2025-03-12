package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.sql.Date;
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
     * Creates a new BorrowingRequest.
     * 
     * @param gameCopyId  The ID of the game copy to be borrowed.
     * @param borrowerId  The ID of the user account requesting the borrow.
     * @param startDate   The start date of the borrowing period.
     * @param endDate     The end date of the borrowing period.
     * @return            The newly created BorrowingRequest.
     */
    @Transactional
    public BorrowRequest createBorrowingRequest(
        int gameCopyId,
        int borrowerId,
        Date startDate,
        Date endDate
    )
    {
        GameCopy foundGameCopy = gameCopyRepository
            .findById(gameCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Game copy not found."));

        UserAccount foundBorrower = userAccountRepository
            .findById(borrowerId)
            .orElseThrow(() -> new IllegalArgumentException("Borrower not found."));

        if (startDate == null || endDate == null)
        {
            throw new IllegalArgumentException("Dates cannot be null.");
        }

        if (!endDate.after(startDate))
        {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        BorrowRequest newRequest = new BorrowRequest(startDate, endDate, foundBorrower, foundGameCopy);
        borrowingRequestRepository.save(newRequest);

        return newRequest;
    }

    /**
     * Finds all pending BorrowingRequest entities for the specified game copy.
     * 
     * @param gameCopyId The ID of the game copy whose pending requests are sought.
     * @return           A list of pending BorrowingRequest entities.
     */
    @Transactional(readOnly = true)
    public List<BorrowRequest> findPendingBorrowingRequests(int gameCopyId)
    {
        GameCopy foundGameCopy = gameCopyRepository
                .findById(gameCopyId)
                .orElseThrow(() -> new IllegalArgumentException("Game copy not found."));

        List<BorrowRequest> pendingRequests = borrowingRequestRepository
                .findByGameCopyId(gameCopyId)
                .stream()
                .filter(request -> request.getRequestStatus() == RequestStatus.Pending)
                .toList();

        if (pendingRequests.isEmpty()) {
            throw new IllegalStateException("No pending borrowing requests found for this game copy.");
        }

        return pendingRequests;
    }

    /**
     * Finds all accepted BorrowingRequest entities for the specified game copy.
     * 
     * @param gameCopyId The ID of the game copy whose accepted requests are sought.
     * @return           A list of accepted BorrowingRequest entities.
     */
    @Transactional(readOnly = true)
    public List<BorrowRequest> findAcceptedBorrowingRequests(int gameCopyId)
    {
        GameCopy foundGameCopy = gameCopyRepository
                .findById(gameCopyId)
                .orElseThrow(() -> new IllegalArgumentException("Game copy not found."));

        List<BorrowRequest> acceptedRequests = borrowingRequestRepository
                .findByGameCopyId(gameCopyId)
                .stream()
                .filter(request -> request.getRequestStatus() == RequestStatus.Accepted)
                .toList();

        if (acceptedRequests.isEmpty()) {
            throw new IllegalStateException("No accepted borrowing requests found for this game copy.");
        }

        return acceptedRequests;
    }

    /**
     * Declines a pending BorrowingRequest by deleting it from the database.
     * 
     * @param borrowingRequestId The ID of the pending BorrowingRequest to be declined.
     * @return                   The deleted BorrowingRequest entity.
     */
    @Transactional
    public BorrowRequest declinePendingBorrowingRequest(int borrowingRequestId)
    {
        BorrowRequest foundRequest = borrowingRequestRepository
            .findById(borrowingRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Borrowing request not found."));

        if (foundRequest.getRequestStatus() != RequestStatus.Pending)
        {
            throw new IllegalStateException("Cannot decline a non-pending borrowing request.");
        }

        borrowingRequestRepository.delete(foundRequest);

        return foundRequest;
    }

    /**
     * Accepts a pending BorrowingRequest by setting its status to ACCEPTED.
     * 
     * @param borrowingRequestId The ID of the BorrowingRequest to be accepted.
     * @return                   The updated BorrowingRequest entity with ACCEPTED status.
     */
    @Transactional
    public BorrowRequest acceptPendingBorrowingRequest(int borrowingRequestId)
    {
        BorrowRequest foundRequest = borrowingRequestRepository
            .findById(borrowingRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Borrowing request not found."));

        foundRequest.setRequestStatus(RequestStatus.Accepted);
        borrowingRequestRepository.save(foundRequest);

        return foundRequest;
    }

    /**
     * Creates a RequestAnswer for the specified BorrowingRequest.
     * 
     * @param borrowingRequestId The ID of the BorrowingRequest.
     * @param dropoffDate        The proposed drop-off date.
     * @param dropoffTime        The proposed drop-off time.
     * @param dropoffLocation    The proposed drop-off location.
     */
    @Transactional
    public RequestAnswer createRequestAnswer(
            int borrowingRequestId,
            Date dropoffDate,
            Time dropoffTime,
            String dropoffLocation,
            String contactEmail
    ) {
        BorrowRequest foundRequest = borrowingRequestRepository
                .findById(borrowingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing request not found."));

        if (dropoffDate == null || dropoffTime == null || dropoffLocation == null || dropoffLocation.isBlank() || contactEmail == null || contactEmail.isBlank()) {
            throw new IllegalArgumentException("Drop-off date, time, location, and contact email must be valid.");
        }

        RequestAnswer requestAnswer = new RequestAnswer(dropoffDate, dropoffTime, dropoffLocation, foundRequest, contactEmail);

        return requestAnswerRepository.save(requestAnswer);
    }

    /**
     * Updates an existing RequestAnswer with new drop-off details.
     * 
     * @param requestAnswerId   The ID of the RequestAnswer to update.
     * @param dropoffDate       The new drop-off date.
     * @param dropoffTime       The new drop-off time.
     * @param dropoffLocation   The new drop-off location.
     */
    @Transactional
    public RequestAnswer updateRequestAnswer(
        int requestAnswerId,
        Date dropoffDate,
        Time dropoffTime,
        String dropoffLocation
    )
    {
        RequestAnswer foundAnswer = requestAnswerRepository
            .findById(requestAnswerId)
            .orElseThrow(() -> new IllegalArgumentException("Request answer not found."));

        if (dropoffDate == null || dropoffTime == null || dropoffLocation == null || dropoffLocation.isBlank())
        {
            throw new IllegalArgumentException("Drop-off date, time, and location must be valid.");
        }

        foundAnswer.setDropOffDate(dropoffDate);
        foundAnswer.setDropOffTime(dropoffTime);
        foundAnswer.setLocation(dropoffLocation);

        return requestAnswerRepository.save(foundAnswer);
    }

    /**
     * Deletes a RequestAnswer from the database by its ID.
     * 
     * @param requestAnswerId The ID of the RequestAnswer to delete.
     */
    @Transactional
    public void deleteRequestAnswer(int requestAnswerId)
    {
        RequestAnswer foundAnswer = requestAnswerRepository
            .findById(requestAnswerId)
            .orElseThrow(() -> new IllegalArgumentException("Request answer not found."));

        requestAnswerRepository.delete(foundAnswer);
    }

    /**
     * Finds the RequestAnswer associated with an accepted BorrowingRequest.
     * 
     * @param borrowingRequestId The ID of the BorrowingRequest whose RequestAnswer is sought.
     * @return                   The found RequestAnswer entity if the BorrowingRequest is accepted.
     */
    @Transactional(readOnly = true)
    public RequestAnswer findRequestAnswerForBorrowingRequest(int borrowingRequestId)
    {
        BorrowRequest foundRequest = borrowingRequestRepository
                .findById(borrowingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing request not found."));

        if (foundRequest.getRequestStatus() != RequestStatus.Accepted)
        {
            throw new IllegalStateException("Cannot find request answer for a non-accepted borrowing request.");
        }

        //findRequestAnswerByBorrowRequestId is in progress
        RequestAnswer foundAnswer = requestAnswerRepository.findRequestAnswerByRequestId(borrowingRequestId);

        if (foundAnswer == null)
        {
            throw new IllegalArgumentException("No request answer found for this borrowing request.");
        }

        return foundAnswer;
    }

}