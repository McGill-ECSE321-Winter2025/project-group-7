package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import jakarta.transaction.Transactional;

public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private UserAccountRepository userRepo;

    @Transactional
    public Review createReview(int rating, String comment, int userID, int gameID){
        UserAccount user = userRepo.findUserAccountById(userID);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user with the ID " + userID);

        }

        Game game = gameRepo.findGameById(gameID);

        if (game  == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no game with the ID "+ gameID);

        }

        if (rating < 0 || rating > 100){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The rating must be between 0 to 100");

        }

        if (comment == null) {
            throw new IllegalArgumentException("The comment cannot be empty");

        }

        Date now = Date.valueOf(LocalDate.now());
        Review review = new Review(now, rating, comment, user, game);
        reviewRepo.save(review);
        return review;
        

    }

    @Transactional
    public Review deleteReview(int id){
        Review review = reviewRepo.findReviewById(id);

        if (review == null){
            throw new IllegalArgumentException("There is no review with the ID" + id);

        }

        reviewRepo.delete(review);
        return review;
    }

    @Transactional
    public Review updateReview(int rating, String comment, int reviewID) {
        Review review = reviewRepo.findReviewById(reviewID);
        if (review == null) {
            throw new IllegalArgumentException("There is no review with the ID "+ reviewID);

        }

        if (rating < 0 || rating > 100){
            throw new IllegalArgumentException("The rating must be between 0 to 100");

        }

        if (comment == null) {
            throw new IllegalArgumentException("The comment cannotbe empty");

        }
        review.setComment(comment);
        review.setRating(rating);
        return review;



    }

    @Transactional
    public List<Review> findReviewsForGame(int gameId){
        Game game = gameRepo.findGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("There is no game with the ID "+ gameId);

        }
        List<Review> allReviews = reviewRepo.findByGameId(gameId);
        return allReviews;
    }

    

}
