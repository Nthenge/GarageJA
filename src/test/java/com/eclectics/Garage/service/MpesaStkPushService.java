package com.eclectics.Garage.service;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
public class MpesaStkPushService {
    private String shortCode;
    private String passKey;
    private String baseUrl;
    private String callbackUrl;

    private final MpesaAuthenticationService mpesaAuthenticationService;

    public MpesaStkPushService(MpesaAuthenticationService mpesaAuthenticationService) {
        this.mpesaAuthenticationService = mpesaAuthenticationService;
    }

    public String initiateStkPush(String phoneNumber, String amount) throws JSONException, IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String password = Base64.getEncoder()
                .encodeToString((shortCode + passKey + timeStamp).getBytes());

        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("BusinessShortCode", shortCode);
        json.put("Password", password);
        json.put("TimeStamp", timeStamp);
        json.put("TransactionType", "CustomerTillOnline");
        json.put("Amount", amount);
        json.put("Party1", phoneNumber);
        json.put("Party2", shortCode);
        json.put("PhoneNumber", phoneNumber);
        json.put("CallBackUrl", callbackUrl);
        json.put("Account reference", "Garage");
        json.put("TransactionDesc", "Payment service");

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request request = new Request.Builder()
                .url(baseUrl + "/mpesa/stkpush/v1/processrequest")
                .post(body)
                .addHeader("Authorization", mpesaAuthenticationService.generateAccessToken())
                .build();

        Response response = client.newCall(request).execute();
        return response.body().toString();
    }
}
