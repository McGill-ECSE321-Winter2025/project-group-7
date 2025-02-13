package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.RequestAnswer;

public interface RequestAnswerRepository extends CrudRepository<RequestAnswer, Integer>{
    public RequestAnswer findRequestAnswerById(int id);
}
