package ca.mcgill.ecse321.boardgamesharingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ReviewDto;
import ca.mcgill.ecse321.boardgamesharingsystem.exception.BoardGameSharingSystemException;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@SpringBootTest
public class ReviewServiceTests {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private ReviewService reviewService;

    private static final String VALID_COMMENT = "Terrible game";
    private static final int VALID_RATING = 1;
    
    private static final String VALID_EMAIL = "miffy@gmail.com";
    private static final String VALID_NAME = "MiffyEnjoyer123";
    private static final String VALID_PASSWORD = "Applesaregreat123";

    private static final String VALID_TITLE = "Monopoly";
    private static final int VALID_MIN_NUM_PLAYERS = 2;
    private static final int VALID_MAX_NUM_PLAYERS = 8;
    private static final String VALID_PICTURE_URL = "example.com";
    private static final String VALID_DESCRIPTION = "Capitalism game";
   
    @Test
    public void testCreateValidReview() {
        //Arrange
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, 10, 11, 1);
        
        //Act
        Review review = reviewService.createReview(reviewDto, 10, 11);

        //Assert
        assertNotNull(review);
        assertEquals(VALID_RATING, review.getRating());
        assertEquals(VALID_COMMENT, review.getComment());
        assertEquals(review.getReviewer().getId(), miffy.getId());
        assertEquals(review.getGame().getId(), monopoly.getId());
        verify(reviewRepository, times(1)).save(any(Review.class));

    }

    @Test
    public void testCreateReviewWithInvalidUserAccount(){
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        
        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, 9, 11, review.getId());

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.createReview(reviewDto, 9, 11));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no user with the ID 9", e.getMessage());
    }

    @Test
    public void testCreateReviewWithInvalidGame() {
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        
        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, 10, 9, review.getId());

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.createReview(reviewDto, 10, 9));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no game with the ID "+ 9, e.getMessage());

    }

    @Test
    public void testCreateReviewWithInvalidComment(){
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, null, miffy, monopoly);
        
        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, null, 10, 11, review.getId());

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.createReview(reviewDto, 10, 11));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The comment cannot be empty", e.getMessage());
    }

    @Test
    public void testCreateReviewWithHighRating(){
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
        Review review = new Review(Date.valueOf(LocalDate.now()), 101, VALID_COMMENT, miffy, monopoly);
        
        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), 101, VALID_COMMENT, 10, 11, review.getId());

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.createReview(reviewDto, 10, 11));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The rating must be between 0 to 100", e.getMessage());
    }

    @Test
    public void testCreateReviewWithLowRating(){
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
        Review review = new Review(Date.valueOf(LocalDate.now()), -1, VALID_COMMENT, miffy, monopoly);
        
        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), -1, VALID_COMMENT, 10, 11, review.getId());

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.createReview(reviewDto, 10, 11));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The rating must be between 0 to 100", e.getMessage());
    }

    @Test
    public void testUpdateValidReview(){
        //Arrange
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        when(reviewRepository.findReviewById(12)).thenReturn(review);
        int newRating = 4;
        String newComment = "Wow great game!!!!!";
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), newRating, newComment, miffy.getId(), monopoly.getId(), 12);

        //Act
        Review updatedReview = reviewService.updateReview(reviewDto);

        //Assert
        assertNotNull(updatedReview);
        assertEquals(newComment, updatedReview.getComment());
        assertEquals(newRating, updatedReview.getRating());
        assertEquals(updatedReview.getId(), review.getId());
        assertEquals(updatedReview.getReviewer().getId(), miffy.getId());
        assertEquals(updatedReview.getGame().getId(), monopoly.getId());
    }

    @Test
    public void testUpdateInvalidReview(){
         //Arrange
         Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
         when(gameRepository.findGameById(11)).thenReturn(monopoly);
         UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
         when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);
         //Act
         ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, 10, 11, 5);
 
         //Assert
         BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.updateReview(reviewDto));
         assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
         assertEquals("There is no review with the ID 5", e.getMessage());
    }

    @Test
    public void testUpdateReviewWithInvalidHighRating(){
        //Arrange
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        when(reviewRepository.findReviewById(12)).thenReturn(review);
        int newRating = 101;
        String newComment = "Wow great game!!!!!";

        //Act
        //Review updatedReview = reviewService.updateReview(newRating, newComment, 12);
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), newRating, newComment, miffy.getId(), monopoly.getId(), 12);
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.updateReview(reviewDto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The rating must be between 0 to 100", e.getMessage());
    }

    @Test
    public void testUpdateReviewWithInvalidLowRating(){
        //Arrange
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        when(reviewRepository.findReviewById(12)).thenReturn(review);
        int newRating = -1;
        String newComment = "Wow great game!!!!!";

        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), newRating, newComment, miffy.getId(), monopoly.getId(), 12);
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.updateReview(reviewDto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The rating must be between 0 to 100", e.getMessage());
    }

    @Test
    public void testUpdateReviewWithInvalidComment(){
        //Arrange
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        when(reviewRepository.findReviewById(12)).thenReturn(review);
        String newComment = null;

        //Act
        ReviewDto reviewDto = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, newComment, miffy.getId(), monopoly.getId(), 12);

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.updateReview(reviewDto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The comment cannot be empty", e.getMessage());
    }

    @Test
    public void testFindReviewsForValidGame() {
        //Arrange
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        UserAccount boris = new UserAccount("cool_boris", "boris@gmail.com", VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review1 = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        Review review2 = new Review(Date.valueOf(LocalDate.now()), 5, "soooooo bad omfg", boris, monopoly);
        when(gameRepository.findGameById(12)).thenReturn(monopoly);
        when(reviewRepository.findReviewById(11)).thenReturn(review1);
        when(reviewRepository.findReviewById(10)).thenReturn(review2);
        List<Review> r = new ArrayList<>();
        r.add(review1);
        r.add(review2);
        when(reviewRepository.findByGameId(12)).thenReturn(r);

        //Act
        List<Review> reviews = reviewService.findReviewsForGame(12);

        //Assert
        assertNotNull(reviews);
        assertEquals(review1.getComment(), reviews.get(0).getComment());
        assertEquals(reviews.get(0).getId(), review1.getId());
        assertEquals(reviews.get(0).getRating(), review1.getRating());
        assertEquals(reviews.get(0).getReviewDate(), review1.getReviewDate());
        assertEquals(reviews.get(0).getReviewer().getId(), review1.getReviewer().getId());
        assertEquals(reviews.get(0).getGame().getId(), review1.getGame().getId());
        assertEquals(reviews.get(1).getComment(), review2.getComment());
        assertEquals(reviews.get(1).getId(), review2.getId());
        assertEquals(reviews.get(1).getRating(), review2.getRating());
        assertEquals(reviews.get(1).getReviewDate(), review2.getReviewDate());
        assertEquals(reviews.get(1).getReviewer().getId(), review2.getReviewer().getId());
        assertEquals(reviews.get(1).getGame().getId(), review2.getGame().getId());

    }

    @Test
    public void testFindReviewsForInvalidGame() {
        //Arrange
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        UserAccount boris = new UserAccount("cool_boris", "boris@gmail.com", VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review1 = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        Review review2 = new Review(Date.valueOf(LocalDate.now()), 5, "soooooo bad omfg", boris, monopoly);
        when(gameRepository.findGameById(12)).thenReturn(monopoly);
        when(reviewRepository.findReviewById(11)).thenReturn(review1);
        when(reviewRepository.findReviewById(10)).thenReturn(review2);
        List<Review> r = new ArrayList<>();
        r.add(review1);
        r.add(review2);
        when(reviewRepository.findByGameId(12)).thenReturn(r);

        //Act

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.findReviewsForGame(13));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no game with the ID 13", e.getMessage());

    }

    @Test
    public void testFindReviewsForGameWithNoReviews(){
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(12)).thenReturn(monopoly);
        List<Review> r = new ArrayList<>();
        when(reviewRepository.findByGameId(12)).thenReturn(r);

        //Act
        List<Review> reviews = reviewService.findReviewsForGame(12);

        //Assert
        assertNotNull(reviews);
        assertEquals(reviews.size(), r.size());

    }

    @Test
    public void testDeleteValidReview(){
        //Arrange
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        when(reviewRepository.findReviewById(12)).thenReturn(review);
    
        //Act
        Review deletedReview = reviewService.deleteReview(12);
        
        //Assert
        assertNotNull(deletedReview);
        assertEquals(review.getComment(), deletedReview.getComment());
        assertEquals(review.getRating(), deletedReview.getRating());
        assertEquals(review.getReviewDate(), deletedReview.getReviewDate());
        assertEquals(review.getReviewer().getId(), deletedReview.getReviewer().getId());
        assertEquals(review.getGame().getId(), deletedReview.getGame().getId());
        assertEquals(review.getId(), deletedReview.getId());
        
    }

    @Test
    public void testDeleteInvalidReview(){
        //Arrange
        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        Review review = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, miffy, monopoly);
        when(reviewRepository.findReviewById(12)).thenReturn(review);

        //Act
        
        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.deleteReview(1));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no review with the ID 1", e.getMessage());
    }
}
