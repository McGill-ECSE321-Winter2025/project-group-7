package ca.mcgill.ecse321.boardgamesharingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;

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

import ca.mcgill.ecse321.boardgamesharingsystem.dto.ErrorDto;
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
        url = String.format("/reviews?reviewerId=" + userId + "&gameId=" + gameId);
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

    @Test
    @Order(1)
    public void testUpdateReview(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews/" + reviewId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), 99, "its so great", userId, gameId, reviewId);
        HttpEntity<ReviewDto> entity = new HttpEntity<>(request);
        
        //Act
        ResponseEntity<ReviewResponseDto> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ReviewResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(99, response.getBody().getRating());
        assertEquals("its so great", response.getBody().getComment());
        assertEquals(reviewId, response.getBody().getId());
        assertEquals(response.getBody().getGameId(), testReview.getGame().getId());
        assertEquals(response.getBody().getUserId(), testReview.getReviewer().getId());

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
        url = String.format("/reviews/%d", reviewId);

        //Act
        ResponseEntity<ReviewResponseDto> response = restTemplate.exchange(url, HttpMethod.DELETE, null, ReviewResponseDto.class);

        //Assert
        restTemplate.delete(url);
        assertNotEquals(testReview, reviewRepo.findById(reviewId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    

    @Test
    @Order(4)
    public void testCreateReviewWithInvalidUser(){
        //Arrange
        gameId = testGame.getId();
        userId = 5;
        reviewId = testReview.getId();
        if (userId == testUser.getId()){
            userId = 40;
        }
        url = String.format("/reviews?reviewerId=" + userId + "&gameId=" + gameId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, userId, gameId, VALID_MAX_NUM_PLAYERS);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity(url, request, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no user with the ID " + userId, response.getBody().getErrors().get(0));

    }

    @Test
    @Order(5)
    public void testCreateReviewWithInvalidGame(){
        //Arrange
        gameId = 5;
        userId = testUser.getId();
        reviewId = testReview.getId();
       if (gameId == testGame.getId()){
            gameId = 40;
        }
        url = String.format("/reviews?reviewerId=" + userId + "&gameId=" + gameId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, VALID_COMMENT, userId, gameId, VALID_MAX_NUM_PLAYERS);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity(url, request, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no game with the ID "+ gameId, response.getBody().getErrors().get(0));
    }

    @Test
    @Order(6)
    public void testCreateReviewWithInvalidHighRating(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews?reviewerId=" + userId + "&gameId=" + gameId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), 101, VALID_COMMENT, userId, gameId, VALID_MAX_NUM_PLAYERS);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity(url, request, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The rating must be between 0 to 100", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(7)
    public void testCreateReviewWithInvalidLowRating(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews?reviewerId=" + userId + "&gameId=" + gameId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), -1, VALID_COMMENT, userId, gameId, VALID_MAX_NUM_PLAYERS);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity(url, request, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The rating must be between 0 to 100", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(7)
    public void testCreateReviewWithInvalidComment(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews?reviewerId=" + userId + "&gameId=" + gameId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), VALID_RATING, null, userId, gameId, VALID_MAX_NUM_PLAYERS);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.postForEntity(url, request, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The comment cannot be empty", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(8)
    public void testUpdateReviewWithInvalidReview(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = 5;
        if (reviewId == testReview.getId()) {
            reviewId = 40;
        }
        testReview.setComment("its so great");
        testReview.setRating(99);
        url = String.format("/reviews/" + reviewId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()), 99, "its so great", userId, gameId, reviewId);
        HttpEntity<ReviewDto> entity = new HttpEntity<>(request);
        
        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no review with the ID " + reviewId, response.getBody().getErrors().get(0));
    }

    @Test
    @Order(9)
    public void testUpdateReviewWithInvalidComment(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews/" + reviewId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()),99, null, userId, gameId, reviewId);
        HttpEntity<ReviewDto> entity = new HttpEntity<>(request);
        
        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The comment cannot be empty", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(10)
    public void testUpdateReviewWithInvalidHighRating(){        
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews/" + reviewId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()),101, "its so great", userId, gameId, reviewId);
        HttpEntity<ReviewDto> entity = new HttpEntity<>(request);
        
        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The rating must be between 0 to 100", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(11)
    public void testUpdateReviewWithInvalidLowRating(){        
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews/" + reviewId);
        ReviewDto request = new ReviewDto(Date.valueOf(LocalDate.now()),-1, "its so great", userId, gameId, reviewId);
        HttpEntity<ReviewDto> entity = new HttpEntity<>(request);
        
        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.PUT, entity, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The rating must be between 0 to 100", response.getBody().getErrors().get(0));
    }

    @Test
    @Order(12)
    public void testDeleteInvalidReview(){
        //Arrange
        gameId = testGame.getId();
        userId = testUser.getId();
        reviewId = 5;
        if (reviewId == testReview.getId()) {
            reviewId = 40;
        }
        url = String.format("/reviews/%d", reviewId);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no review with the ID " + reviewId, response.getBody().getErrors().get(0));
    }

    @Test
    @Order(13)
    public void testGetReviewByInvalidGame(){        
        //Arrange
        gameId = 5;
        if (gameId == testGame.getId()) {
            gameId = 40;
        }
        userId = testUser.getId();
        reviewId = testReview.getId();
        url = String.format("/reviews?gameId=" + gameId);

        //Act
        ResponseEntity<ErrorDto> response = restTemplate.getForEntity(url, ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no game with the ID "+ gameId, response.getBody().getErrors().get(0));
    }
}
