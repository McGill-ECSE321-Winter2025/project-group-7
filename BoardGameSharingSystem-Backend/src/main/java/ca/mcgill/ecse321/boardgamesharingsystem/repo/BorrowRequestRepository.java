package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.BorrowRequest;

public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer>{
    public BorrowRequest findBorrowRequestById(int id);
    public List<BorrowRequest> findByBorrowerId(int borrowerId);
    public List<BorrowRequest> findByGameCopyId(int gameCopyId);
    public void deleteByGameCopyId(int gameCopyId);
    public List<BorrowRequest> findAll();
}
