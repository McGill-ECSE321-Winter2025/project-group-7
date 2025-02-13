package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;

public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer>{
    public BorrowRequest findBorrowRequestById(int id);
}
