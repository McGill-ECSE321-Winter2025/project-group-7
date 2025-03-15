package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.http.HttpHeaders;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.GameOwnerResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ReviewDto;
import ca.mcgill.ecse321.boardgamesharingsystem.dto.ReviewResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.GameRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamesharingsystem.repo.UserAccountRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ReviewIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private UserAccountRepository userAccountRepo;
    @Autowired
    private GameRepository gameRepo;
    
    private UserAccount testUser;
    private Game testGame;
    private Review testReview;

    private static final String VALID_COMMENT = "Terrible game";
    private static final int VALID_RATING = 1;
   // private static final Date VALID_DATE = Date.valueOf("1998-06-24");
    private int reviewId;
    
    private static final String VALID_EMAIL = "miffy@gmail.com";
    private static final String VALID_NAME = "MiffyEnjoyer123";
    private static final String VALID_PASSWORD = "Applesaregreat123";
    private int userId;

    private static final String VALID_TITLE = "Monopoly";
    private static final int VALID_MIN_NUM_PLAYERS = 2;
    private static final int VALID_MAX_NUM_PLAYERS = 8;
    private static final String VALID_PICTURE_URL = "example.com";
    private static final String VALID_DESCRIPTION = "Capitalism game";
    private int gameId;

    String url;

    @BeforeEach
    public void setUp() {
        testUser = new UserAccount(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        userAccountRepo.save(testUser);
        testGame = new Game(VALID_TITLE, VALID_MIN_NUM_PLAYERS, VALID_MAX_NUM_PLAYERS, VALID_PICTURE_URL, VALID_DESCRIPTION);
        gameRepo.save(testGame);
        testReview = new Review(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, testUser, testGame);
        reviewRepo.save(testReview);
    }


    @AfterEach
    public void cleanUp() {
        reviewRepo.deleteAll();
        userAccountRepo.deleteAll();
        gameRepo.deleteAll();
        url = null;

    }
    @Test
    @Order(0)
    public void testCreateValidReview(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        url = String.format("/reviews?reviewerId=" + userId);
        ReviewDto request = new ReviewDto(testReview.getReviewDate(), VALID_RATING, VALID_COMMENT, userId, gameId, reviewId);

        //Act
        ResponseEntity<ReviewResponseDto> response = restTemplate.postForEntity(url, request, ReviewResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getRating(), testReview.getRating());
        assertEquals(response.getBody().getComment(), testReview.getComment());
        assertNotNull(response.getBody().getId());
        reviewId = response.getBody().getId();
        assertEquals(response.getBody().getGameId(), testReview.getGame().getId());
        assertEquals(response.getBody().getUserId(), testReview.getReviewer().getId());

    }

    //fix assertions
    @Test
    @Order(1)
    public void testUpdateReview(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        testReview.setComment("its so great");
        testReview.setRating(99);
        url = String.format("/reviews/" + reviewId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), 99, "its so great", userId, gameId, reviewId);
        HttpEntity<ReviewDto> entity = new HttpEntity<>(request);
        
        //Act
        ResponseEntity<ReviewResponseDto> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ReviewResponseDto.class);

        //Assert
        assertNotNull(response);
        //assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        /*assertEquals(response.getBody().getRating(), testReview.getRating());
        assertEquals(response.getBody().getComment(), "its so great");
        assertEquals(reviewId, response.getBody().getId());
        assertEquals(response.getBody().getGameId(), testReview.getGame().getId());
        assertEquals(response.getBody().getUserId(), testReview.getReviewer().getId());*/

    }

    @Test
    @Order(2)
    public void testGetReviewByGame(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews?gameId=" + gameId);

        //Act
        ResponseEntity<ReviewResponseDto[]> response = restTemplate.getForEntity(url, ReviewResponseDto[].class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reviewId, response.getBody()[0].getId());
        assertEquals(gameId, response.getBody()[0].getGameId());

    }
    @Test
    @Order(3)
    public void testDeleteValidReview(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews?reviewId=" + reviewId);

        //Act
        ResponseEntity<ReviewResponseDto> response = restTemplate.exchange(url, HttpMethod.DELETE, null, ReviewResponseDto.class);

        //Assert
        restTemplate.delete(url);
        assertNotEquals(testReview, reviewRepo.findById(reviewId));
    }

}
