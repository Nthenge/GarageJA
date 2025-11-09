package com.eclectics.Garage.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MpesaB2CService {

    @Value("${mpesa.consumer.url}")
    private String baseUrl;

    @Value("${mpesa.shortcode}")
    private String initiatorShortCode; // your business short code (system admin)

    @Value("${mpesa.initiator.name}")
    private String initiatorName;

    @Value("${mpesa.security.credential}")
    private String securityCredential;

    @Value("${mpesa.callback.url}")
    private String callbackUrl;

    private final MpesaAuthenticationService mpesaAuthService;

    public MpesaB2CService(MpesaAuthenticationService mpesaAuthService) {
        this.mpesaAuthService = mpesaAuthService;
    }

    public String sendMoneyToGarage(String amount, String garageTill, String remarks) throws IOException {
        String token = mpesaAuthService.generateAccessToken();

        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("InitiatorName", initiatorName);
        json.put("SecurityCredential", securityCredential);
        json.put("CommandID", "BusinessPayment");
        json.put("Amount", amount);
        json.put("PartyA", initiatorShortCode);
        json.put("PartyB", garageTill);
        json.put("Remarks", remarks);
        json.put("QueueTimeOutURL", callbackUrl);
        json.put("ResultURL", callbackUrl);
        json.put("Occasion", "GaragePayment");

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request request = new Request.Builder()
                .url(baseUrl + "/mpesa/b2c/v1/paymentrequest")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
