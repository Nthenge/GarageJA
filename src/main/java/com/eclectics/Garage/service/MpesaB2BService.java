package com.eclectics.Garage.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MpesaB2BService {

    @Value("${mpesa.base.host}") // <--- UPDATED PROPERTY KEY
    private String baseUrl;

    @Value("${mpesa.shortcode}") // 174379: This is the source of the collected payment
    private String initiatorShortCode;

    @Value("${mpesa.initiator.name}")
    private String initiatorName;

    @Value("${mpesa.security.credential}")
    private String securityCredential;

    @Value("${mpesa.callback.url}")
    private String callbackUrl;

    private final MpesaAuthenticationService mpesaAuthService;

    public MpesaB2BService(MpesaAuthenticationService mpesaAuthService) {
        this.mpesaAuthService = mpesaAuthService;
    }

    // CommandID can be BusinessBuyGoods (for Till) or BusinessPayBill (for Paybill)
    // We'll use BusinessBuyGoods as a common case for Tills
    public String transferMoney(String amount, String destinationTill, String remarks, String commandId) throws IOException {
        String token = mpesaAuthService.generateAccessToken();

        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("Initiator", initiatorName);
        json.put("SecurityCredential", securityCredential);
        json.put("CommandID", commandId); // e.g., BusinessBuyGoods, BusinessPayBill
        json.put("SenderIdentifierType", "4"); // 4 for ShortCode (The source is 174379)
        json.put("RecieverIdentifierType", "4"); // 4 for ShortCode (The destination is the Till)
        json.put("Amount", amount);
        json.put("PartyA", initiatorShortCode); // Source: 174379 (Where customer paid)
        json.put("PartyB", destinationTill);    // Destination: Garage Till or System Till
        json.put("AccountReference", "B2B Ref");
        json.put("Remarks", remarks);
        json.put("QueueTimeOutURL", callbackUrl);
        json.put("ResultURL", callbackUrl);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request request = new Request.Builder()
                .url(baseUrl + "/mpesa/b2b/v1/paymentrequest") // <--- B2B Endpoint
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}