package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;

public interface RegistrationRepository extends CrudRepository<Registration, Integer> {
	public Registration findRegistrationByKey(Registration.RegistrationKey Key);
}
