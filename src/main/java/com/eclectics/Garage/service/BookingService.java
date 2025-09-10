package com.eclectics.Garage.service;

import com.eclectics.Garage.model.Booking;

import java.util.List;

public interface BookingService {
    String createBooing(Booking booking);
    Booking getBookingById(Long id);
    List<Booking> getAllBookings();
    List<Booking> getBookingByUserId(Long userId);
    List<Booking> getBookingsByGarageId(Long garageId);
    List<Booking> getBookingByMechanicId(Long mechanicId);
    String updateBooking(Long id, Booking booking);
    String deleteBooking(Long id);
}
