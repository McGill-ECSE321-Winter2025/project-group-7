package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;

import java.util.List;

public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer>{
    public BorrowRequest findBorrowRequestById(int id);
    public List<BorrowRequest> findByBorrowerId(int borrowerId);
    public List<BorrowRequest> findByGameCopyId(int gameCopyId);

    //new
    public List<BorrowRequest> findAll();
}
