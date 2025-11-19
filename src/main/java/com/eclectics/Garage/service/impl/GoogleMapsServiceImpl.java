package com.eclectics.Garage.service.impl;

import com.eclectics.Garage.service.GoogleMapsService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsServiceImpl implements GoogleMapsService {
    @Value("${google.api.key}")
    private String apiKey;

    private final WebClient client = WebClient.create("https://maps.googleapis.com/maps/api");
    private final WebClient webClient = WebClient.create("https://maps.googleapis.com/maps/api");

    @Override
    public Mono<Map<String, Double>> geocode(String address) {
        return client.get()
                .uri(uri -> uri.path("/geocode/json")
                        .queryParam("address", address)
                        .queryParam("key", apiKey).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    JsonNode loc = json.get("results").get(0)
                            .get("geometry").get("location");

                    Map<String, Double> map = new HashMap<>();
                    map.put("lat", loc.get("lat").asDouble());
                    map.put("lng", loc.get("lng").asDouble());
                    return map;
                });
    }

    @Override
    public Mono<String> getAddress(Double lat, Double lng) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode/json")
                        .queryParam("latlng", lat + "," + lng)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("results").get(0).get("formatted_address").asText());
    }
}
