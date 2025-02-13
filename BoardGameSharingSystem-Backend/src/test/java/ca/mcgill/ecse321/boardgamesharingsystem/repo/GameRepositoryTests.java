package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;

@SpringBootTest
public class GameRepositoryTests {
    @Autowired
    private GameRepository gameRepository;

    @AfterEach
    public void clearDatabase(){
        gameRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadGame(){
        //Arrange
        Game chess = new Game("chess", 2, 2, "chess.com","the chess game consists..." );
        chess = gameRepository.save(chess);

        //Act
        Game chessFromDB= gameRepository.findGameById(chess.getId());

        //Assert
        assertNotNull(chessFromDB);
        assertEquals(chess.getId(), chessFromDB.getId());
        assertEquals(chess.getMinNumPlayers(), chessFromDB.getMinNumPlayers());
        assertEquals(chess.getMaxNumPlayers(), chessFromDB.getMaxNumPlayers());
        assertEquals(chess.getPictureURL(), chessFromDB.getPictureURL());
        assertEquals(chess.getTitle(), chessFromDB.getTitle());
        assertEquals(chess.getDescription(), chessFromDB.getDescription());

    }
}
