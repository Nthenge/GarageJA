package com.eclectics.Garage.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate-limits")
public class RateLimitProperties {
    private Map<String, Limit> limits = new HashMap<>(); // initilize empty map

    public Map<String, Limit> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Limit> limits) {
        this.limits = limits;
    }

    public static class Limit {
        private int capacity;
        private int refill;
        private int refillPeriodMinutes;

        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public int getRefill() { return refill; }
        public void setRefill(int refill) { this.refill = refill; }
        public int getRefillPeriodMinutes() { return refillPeriodMinutes; }
        public void setRefillPeriodMinutes(int refillPeriodMinutes) { this.refillPeriodMinutes = refillPeriodMinutes; }
    }
}


