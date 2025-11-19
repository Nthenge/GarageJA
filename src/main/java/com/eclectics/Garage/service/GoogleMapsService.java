package com.eclectics.Garage.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface GoogleMapsService {
    Mono<Map<String, Double>> geocode(String address);

    Mono<String> getAddress(Double lat, Double lng);
}
