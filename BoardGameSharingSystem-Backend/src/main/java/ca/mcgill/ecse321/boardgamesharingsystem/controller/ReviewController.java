package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

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

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ReviewDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ReviewResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;
import ca.mcgill.ecse321.boardgamesharingsystem.service.ReviewService;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDto createReviewForGame(@RequestParam int reviewerId, @RequestParam int gameId, @RequestBody ReviewDto review) {
        return new ReviewResponseDto(reviewService.createReview(review, review.getUserId(),review.getGameId()));
    }

    @GetMapping("/reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponseDto> findReviewsForGame(@RequestParam int gameId) {
        List <Review> reviewsFound = reviewService.findReviewsForGame(gameId);
        List <ReviewResponseDto> responses = new ArrayList<>();
        reviewsFound.forEach(review -> responses.add(new ReviewResponseDto(review)));
        return responses;
    }

    @PutMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponseDto updateReview(@PathVariable int id, @RequestBody ReviewDto review) {
        return new ReviewResponseDto(reviewService.updateReview(review));
    }

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponseDto deleteReview(@PathVariable int id) {
        return new ReviewResponseDto(reviewService.deleteReview(id));
    }

}
