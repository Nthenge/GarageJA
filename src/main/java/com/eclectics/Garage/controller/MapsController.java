package com.eclectics.Garage.controller;

import com.eclectics.Garage.service.GoogleMapsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/maps")
public class MapsController {

    private final GoogleMapsService googleMapsService;

    public MapsController(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    @GetMapping("/cords")
    public Mono<Map<String, Double>> getCoordinates(@RequestParam String address) {
        return googleMapsService.geocode(address);
    }

    @GetMapping("/address")
    public Mono<String> getAddress(@RequestParam Double lat, @RequestParam Double lng) {
        return googleMapsService.getAddress(lat, lng);
    }
}

