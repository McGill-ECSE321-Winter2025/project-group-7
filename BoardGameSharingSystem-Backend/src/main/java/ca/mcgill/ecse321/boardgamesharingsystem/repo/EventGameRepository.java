package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.EventGame;

public interface EventGameRepository extends CrudRepository<EventGame, EventGame.Key>{
    EventGame findEventGameByKey(EventGame.Key key);

    Iterable<EventGame> findEventGameByRegistrantId(int id);
}
