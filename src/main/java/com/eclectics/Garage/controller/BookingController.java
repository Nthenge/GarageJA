package com.eclectics.Garage.controller;

import com.eclectics.Garage.model.Booking;
import com.eclectics.Garage.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public Booking getOneBookings(@PathVariable("bookingId") Long Id){
        return bookingService.getBookingById(Id);
    }

    @GetMapping()
    public List<Booking> getAllBookings(){
        return bookingService.getAllBookings();
    }

    @PostMapping()
    public String createBookings(@RequestBody Booking booking){
        bookingService.createBooing(booking);
        return "Booking created successfully";
    }

    @PutMapping("/{bookingId}")
    public String updateBooking(@PathVariable Long bookingId, @RequestBody Booking booking){
        bookingService.updateBooking(bookingId, booking);
        return "Booking updated successfully";
    }

    @DeleteMapping("/{bookingId}")
    public String deleteABooking(@PathVariable("bookingId") Long bookingId){
        bookingService.deleteBooking(bookingId);
        return "Booking Deleted Succesfully";
    }

}
