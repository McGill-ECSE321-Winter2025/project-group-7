package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;

@SpringBootTest
public class ReviewRepositoryTests {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @AfterEach
    public void clearDatabase(){
        reviewRepository.deleteAll();
        gameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadReview(){
        //Arrange
        String name = "bojack";
        String email = "bojack@gmail.com";
        String password = "password";
        UserAccount currentUser = new UserAccount(name, email, password);
        currentUser = userAccountRepository.save(currentUser);

        String title = "uno";
        int min = 2;
        int max = 10;
        String pictureURL = "https://www.example.com";
        String description = "fun card game";
        Game game = new Game(title, min, max, pictureURL, description);
        game = gameRepository.save(game);

        Date reviewDate = Date.valueOf("2025-02-14");
        int rating = 1;
        String comment = "terrible game";
        Review review = new Review(reviewDate, rating, comment, currentUser, game);
        review = reviewRepository.save(review);

        //Act
        Review reviewFromDb = reviewRepository.findReviewById(review.getId());

        //Assert
        assertNotNull(reviewFromDb);
        assertEquals(reviewFromDb.getId(), review.getId());
        assertEquals(reviewFromDb.getRating(), rating);
        assertEquals(reviewFromDb.getComment(), comment);
        assertEquals(reviewFromDb.getReviewDate(), reviewDate);
        assertNotNull(reviewFromDb.getGame());
        assertEquals(reviewFromDb.getGame().getId(), game.getId());
        assertNotNull(reviewFromDb.getReviewer());
        assertEquals(reviewFromDb.getReviewer().getId(), currentUser.getId());
    }
}
