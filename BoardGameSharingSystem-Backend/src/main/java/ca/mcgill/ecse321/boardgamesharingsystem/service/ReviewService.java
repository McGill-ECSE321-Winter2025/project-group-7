package ca.mcgill.ecse321.boardgamesharingsystem.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ReviewDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;
import jakarta.transaction.Transactional;
/**
 * This service class implements functionalities related to Review,
 * including creating, updating, deleting, and retrieving Reviews for any Game in the system.
 */
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private UserAccountRepository userRepo;

    /**
     * Creates a review for a game
     * @param reviewDto Request for the creation
     * @param userID    ID of the user creating the review
     * @param gameID    ID of the game for which the review is made 
     * @return          The created review
     */
    @Transactional
    public Review createReview(ReviewDto reviewDto, int userID, int gameID){
        int rating = reviewDto.getRating();
        String comment = reviewDto.getComment();
        UserAccount user = userRepo.findUserAccountById(userID);
        if (user == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "There is no user with the ID " + userID);

        }

        Game game = gameRepo.findGameById(gameID);

        if (game  == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"There is no game with the ID "+ gameID);

        }

        if (rating < 0 || rating > 100){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"The rating must be between 0 to 100");

        }

        if (comment == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND, "The comment cannot be empty");

        }

        Date now = Date.valueOf(LocalDate.now());
        Review review = new Review(now, rating, comment, user, game);
        reviewRepo.save(review);
        return review;
        

    }

    /**
     * Deletes the review if it exists.
     * @param id ID of the review being deleted
     * @return The successfully deleted review
     */
    @Transactional
    public Review deleteReview(int id){
        Review review = reviewRepo.findReviewById(id);

        if (review == null){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"There is no review with the ID " + id);

        }

        reviewRepo.delete(review);
        return review;
    }

    /**
     * Updates the review's comment and rating
     * @param reviewDto Request for the update
     * @return  The successfully updated review
     */
    @Transactional
    public Review updateReview(ReviewDto reviewDto) {
        int rating = reviewDto.getRating();
        String comment = reviewDto.getComment();
        int reviewID = reviewDto.getId();
        Review review = reviewRepo.findReviewById(reviewID);
        if (review == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"There is no review with the ID "+ reviewID);

        }

        if (rating < 0 || rating > 100){
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"The rating must be between 0 to 100");

        }

        if (comment == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"The comment cannot be empty");

        }
        review.setComment(comment);
        review.setRating(rating);
        return review;



    }

    /**
     * Find all the reviews for a game
     * @param gameId ID of the game for which reviews are being searched
     * @return List of all the reviews found for the game
     */
    @Transactional
    public List<Review> findReviewsForGame(int gameId){
        Game game = gameRepo.findGameById(gameId);
        if (game == null) {
            throw new BoardGameSharingSystemException(HttpStatus.NOT_FOUND,"There is no game with the ID "+ gameId);

        }
        List<Review> allReviews = reviewRepo.findByGameId(gameId);
        return allReviews;
    }

    

}
