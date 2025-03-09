package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.sql.Date;
import java.sql.Time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.RequestAnswerRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;

@Service
public class RequestAnswerService {

    private final RequestAnswerRepository requestAnswerRepository;
    private final BorrowRequestRepository borrowingRequestRepository;

    @Autowired
    public RequestAnswerService(RequestAnswerRepository requestAnswerRepository, BorrowRequestRepository borrowingRequestRepository) {
        this.requestAnswerRepository = requestAnswerRepository;
        this.borrowingRequestRepository = borrowingRequestRepository;
    }

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

    @Transactional
    public RequestAnswer updateRequestAnswer(
            int requestAnswerId,
            Date dropoffDate,
            Time dropoffTime,
            String dropoffLocation
    ) {
        RequestAnswer foundAnswer = requestAnswerRepository
                .findById(requestAnswerId)
                .orElseThrow(() -> new IllegalArgumentException("Request answer not found."));

        if (dropoffDate == null || dropoffTime == null || dropoffLocation == null || dropoffLocation.isBlank()) {
            throw new IllegalArgumentException("Drop-off date, time, and location must be valid.");
        }

        foundAnswer.setDropOffDate(dropoffDate);
        foundAnswer.setDropOffTime(dropoffTime);
        foundAnswer.setLocation(dropoffLocation);
        return requestAnswerRepository.save(foundAnswer);
    }

    @Transactional
    public void deleteRequestAnswer(int requestAnswerId) {
        RequestAnswer foundAnswer = requestAnswerRepository
                .findById(requestAnswerId)
                .orElseThrow(() -> new IllegalArgumentException("Request answer not found."));

        requestAnswerRepository.delete(foundAnswer);
    }

    @Transactional(readOnly = true)
    public RequestAnswer findRequestAnswerForBorrowingRequest(int borrowingRequestId) {
        BorrowRequest foundRequest = borrowingRequestRepository
                .findById(borrowingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing request not found."));

        if (foundRequest.getRequestStatus() != BorrowRequest.RequestStatus.Accepted) {
            throw new IllegalStateException("Cannot find request answer for a non-accepted borrowing request.");
        }

        RequestAnswer foundAnswer = requestAnswerRepository.findRequestAnswerByRequestId(borrowingRequestId);
        if (foundAnswer == null) {
            throw new IllegalArgumentException("No request answer found for this borrowing request.");
        }

        return foundAnswer;
    }
}
