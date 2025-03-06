package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Event;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.model.UserAccount;
import jakarta.transaction.Transactional;

public interface RegistrationRepository extends CrudRepository<Registration, Registration.RegistrationKey> {
	public Registration findRegistrationByKey(Registration.RegistrationKey Key);
	@Transactional
	public void deleteByKey_Event(Event event);
	public Iterable<Registration> findByKey_Event(Event event);
	public Iterable<Registration> findByKey_Participant(UserAccount userAccount);
}
