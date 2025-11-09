package com.eclectics.Garage.service;

import com.eclectics.Garage.dto.GarageResponseDTO;
import com.eclectics.Garage.dto.ServiceResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class PaymentSplitService {

    @Value("${system.admin.till}")
    private String systemAdminTill;

    @Value("${system.admin.split.percentage}")
    private double systemCommissionRate; // e.g. 5 means 5%

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public PaymentSplitResult calculateSplit(ServiceResponseDTO service, GarageResponseDTO garage) {
        double totalAmount = service.getPrice();

        double systemAmount = (systemCommissionRate / 100.0) * totalAmount;
        double garageAmount = totalAmount - systemAmount;

        return new PaymentSplitResult(
                df.format(totalAmount),
                df.format(systemAmount),
                df.format(garageAmount),
                systemAdminTill,
                garage.getMpesaTill()
        );
    }

    public static class PaymentSplitResult {
        private final String totalAmount;
        private final String systemAmount;
        private final String garageAmount;
        private final String systemTill;
        private final Integer garageTill;

        public PaymentSplitResult(String totalAmount, String systemAmount, String garageAmount,
                                  String systemTill, Integer garageTill) {
            this.totalAmount = totalAmount;
            this.systemAmount = systemAmount;
            this.garageAmount = garageAmount;
            this.systemTill = systemTill;
            this.garageTill = garageTill;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public String getSystemAmount() {
            return systemAmount;
        }

        public String getGarageAmount() {
            return garageAmount;
        }

        public String getSystemTill() {
            return systemTill;
        }

        public Integer getGarageTill() {
            return garageTill;
        }
    }
}
