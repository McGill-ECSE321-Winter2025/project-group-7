package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {
	public Event findEventById(int id);
	public List<Event> findAll();
}
