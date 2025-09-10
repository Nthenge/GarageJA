package com.eclectics.Garage.repository;

import com.eclectics.Garage.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByGarageId(Long garageId);
    List<Booking> findByMechanicId(Long mechanicId);
}
