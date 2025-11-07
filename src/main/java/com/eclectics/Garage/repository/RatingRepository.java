package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByGarageId(Long garageId);

    List<Rating> findByMechanicId(Long mechanicId);
}
