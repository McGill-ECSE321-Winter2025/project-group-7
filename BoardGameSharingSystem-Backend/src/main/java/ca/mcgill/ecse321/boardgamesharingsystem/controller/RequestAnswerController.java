package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.RequestAnswerDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.RequestAnswerResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;
import ca.mcgill.ecse321.boardgamesharingsystem.service.BorrowingService;

@RestController
@RequestMapping("/requestAnswer")
@CrossOrigin(origins="http://localhost:8090")
public class RequestAnswerController {
    @Autowired
    private BorrowingService borrowingService;

    /**
     * Creates a new RequestAnswer for a given BorrowingRequest.
     * @param borrowingRequestId the ID of the BorrowingRequest
     * @param requestAnswerDto contains drop-off date, time, and location
     * @return the created RequestAnswer
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestAnswerResponseDto createRequestAnswer(
            @RequestParam("borrowingRequestId") int borrowingRequestId,
            @RequestBody RequestAnswerDto requestAnswerDto
    ) {
        return new RequestAnswerResponseDto(
                borrowingService.createRequestAnswer(
                        borrowingRequestId,
                        requestAnswerDto.getDropoffDate(),
                        requestAnswerDto.getDropoffTime(),
                        requestAnswerDto.getDropoffLocation(),
                        requestAnswerDto.getContactEmail()
                )
        );
    }


    /**
     * Updates an existing RequestAnswer.
     * @param requestAnswerId the ID of the RequestAnswer to update
     * @param requestAnswerDto contains updated drop-off date, time, and location
     * @return the updated RequestAnswer
     */
    @PutMapping("/{requestAnswerId}")
    @ResponseStatus(HttpStatus.OK)
    public RequestAnswerResponseDto updateRequestAnswer(
            @PathVariable("requestAnswerId") int requestAnswerId,
            @RequestBody RequestAnswerDto requestAnswerDto
    ) {
        return new RequestAnswerResponseDto(
                borrowingService.updateRequestAnswer(
                        requestAnswerId,
                        requestAnswerDto.getDropoffDate(),
                        requestAnswerDto.getDropoffTime(),
                        requestAnswerDto.getDropoffLocation()
                )
        );
    }

    /**
     * Deletes a RequestAnswer.
     * @param requestAnswerId the ID of the RequestAnswer to delete
     * @return the deleted RequestAnswer
     */
    @DeleteMapping("/{requestAnswerId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRequestAnswer(@PathVariable("requestAnswerId") int requestAnswerId) {
        borrowingService.deleteRequestAnswer(requestAnswerId);
    }

    /**
     * Retrieves the RequestAnswer for a given BorrowingRequest.
     * @param borrowingRequestId the ID of the BorrowingRequest
     * @return the RequestAnswer associated with the BorrowingRequest
     */
    @GetMapping("")
    public RequestAnswerResponseDto findRequestAnswerForBorrowingRequest(@RequestParam("borrowingRequestId") int borrowingRequestId) {
        RequestAnswer requestAnswer = borrowingService.findRequestAnswerForBorrowingRequest(borrowingRequestId);
        return new RequestAnswerResponseDto(requestAnswer);
    }
}
