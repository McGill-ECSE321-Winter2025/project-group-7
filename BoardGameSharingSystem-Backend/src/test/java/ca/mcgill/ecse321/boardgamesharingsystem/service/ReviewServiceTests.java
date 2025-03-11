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
    public void findUserAccountByValidId() {
        //Arrange
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);

        //Act
        UserAccount userAccount = userAccountRepository.findUserAccountById(10);

        //Assert
        assertNotNull(userAccount);
        assertEquals(miffy.getEmail(), userAccount.getEmail());
        assertEquals(miffy.getName(), userAccount.getName());
        assertEquals(miffy.getPassword(), userAccount.getPassword());
        assertEquals(miffy.getId(), userAccount.getId());

    }

    @Test
    public void findUserAccountByInvalidId(){
        //Arrange
        when(userAccountRepository.findUserAccountById(10)).thenReturn(null);
        
        //Act
        UserAccount userAccount = userAccountRepository.findUserAccountById(10);

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.findUserAccountById(10));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    public void findGameByValidId(){
        //Arrange
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(10)).thenReturn(monopoly);

        //Act
        Game game = gameRepository.findGameById(10);

        //Assert
        assertNotNull(game);
        assertEquals(monopoly.getDescription(), game.getDescription());
        assertEquals(monopoly.getId(), game.getId());
        assertEquals(monopoly.getMaxNumPlayers(), game.getMaxNumPlayers());
        assertEquals(monopoly.getMinNumPlayers(), game.getMinNumPlayers());
        assertEquals(monopoly.getPictureURL(), game.getPictureURL());
        assertEquals(monopoly.getTitle(), game.getTitle());

    }

     @Test
    public void findGameByInvalidId(){
        //Arrange
        when(gameRepository.findGameById(10)).thenReturn(null);

        //Act

        //Assert
        BoardGameSharingSystemException e = assertThrows(BoardGameSharingSystemException.class, () -> reviewService.findGameById(10));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());

    }

    @Test
    public void testCreateValidReview() {
        //Arrange
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(userAccountRepository.findUserAccountById(10)).thenReturn(miffy);

        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        when(gameRepository.findGameById(11)).thenReturn(monopoly);

        when(reviewRepository.save(any(Review.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        //Act
        Review review = reviewService.createReview(VALID_RATING,VALID_COMMENT, 10, 11);

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

        //Act

        //Assert
    }

    @Test
    public void testCreateReviewWithInvalidRating(){
        //Arrange

        //Act

        //Assert
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

        //Act
        Review updatedReview = reviewService.updateReview(newRating, newComment, 12);

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
        UserAccount miffy = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Game monopoly = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);

        //Act

        //Assert
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
        Review updatedReview = reviewService.updateReview(newRating, newComment, 12);
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
        Review updatedReview = reviewService.updateReview(newRating, newComment, 12);
    }

    @Test
    public void testUpdateReviewWithInvalidComment(){
        //Arrange

        //Act

        //Assert
    }

    @Test
    public void testDeleteValidReview(){
        //Arrange

        //Act

        //Assert
    }

    @Test
    public void testDeleteInvalidReview(){
        //Arrange

        //Act

        //Assert
    }


    
}
