package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Game;

public interface EventGameRepository extends CrudRepository<EventGame, EventGame.Key>{
    public EventGame findEventGameByKey(EventGame.Key key); 
    public Iterable<EventGame> findByKey_Event(Event event);
    public Iterable<EventGame> findByKey_GamePlayed(Game game);
}
