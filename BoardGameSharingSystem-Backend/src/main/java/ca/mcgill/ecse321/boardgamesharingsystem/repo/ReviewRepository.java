package ca.mcgill.ecse321.boardgamesharingsystem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.boardgamesharingsystem.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Integer>{
    public Review findReviewById(int id);

    public List<Review> findByGameId(int id);

    public List<Review> findByReviewerId(int id);
}