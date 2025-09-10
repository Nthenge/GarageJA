package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.exception.ExceptionHandlerNotFound;
import com.eclectics.Garage.model.Booking;
import com.eclectics.Garage.repository.BookingRepository;
import com.eclectics.Garage.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public String createBooing(Booking booking) {
        bookingRepository.save(booking);
        return "Created a booking";
    }

    @Override
    public Booking getBookingById(Long id) {
        if (bookingRepository.findById(id).isEmpty())
            throw new ExceptionHandlerNotFound("Requested Booking does not exist");
        return bookingRepository.findById(id).get();
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<Booking> getBookingsByGarageId(Long garageId) {
        return List.of();
    }

    @Override
    public List<Booking> getBookingByMechanicId(Long mechanicId) {

        return List.of();
    }

    @Override
    public String updateBooking(Long id, Booking booking) {
        bookingRepository.save(booking);
        return "Booking Updated";
    }

    @Override
    public String deleteBooking(Long id) {
        bookingRepository.deleteById(id);
        return "Deleted";
    }
}
