package com.eclectics.Garage.service;

import io.jsonwebtoken.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class MpesaAuthenticationService {
    @Value("${mpesa.consumer.key}")
    private String customerKey;
    @Value("${mpesa.consumer.secret}")
    private String customerSecret;
    @Value("${mpesa.consumer.url}")
    private String baseUrl;

    public String generateAccessToken() throws IOException, java.io.IOException, JSONException {
        String appKeySecret = customerKey + ":" + customerSecret;
        String encoded = Base64.getEncoder().encodeToString(appKeySecret.getBytes());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl + "/oauth/v1/generate?grant_type=client_credentials")
                .get()
                .addHeader("Authorization", "Basic " + encoded )
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
}
