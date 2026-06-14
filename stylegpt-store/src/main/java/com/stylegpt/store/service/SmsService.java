package com.stylegpt.store.service;

import com.stylegpt.store.exception.AppException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${sms.fast2sms.api-key:}")
    private String apiKey;

    @Value("${sms.fast2sms.enabled:false}")
    private boolean enabled;

    public void sendOtp(String phone, String otp) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            System.out.println("========== MOCK OTP SYSTEM ==========");
            System.out.println("Phone: " + phone);
            System.out.println("OTP: " + otp);
            System.out.println("=====================================");
            return;
        }
        String message = "Your StyleGPT Store OTP is " + otp;
        String body = "route=q"
                + "&message=" + encode(message)
                + "&language=english"
                + "&flash=0"
                + "&numbers=" + encode(phone);
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://www.fast2sms.com/dev/bulkV2"))
                .timeout(Duration.ofSeconds(20))
                .header("authorization", apiKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AppException("SMS provider failed: " + response.body(), HttpStatus.BAD_GATEWAY);
            }
        } catch (IOException ex) {
            throw new AppException("Could not send SMS OTP", HttpStatus.BAD_GATEWAY);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AppException("SMS OTP sending interrupted", HttpStatus.BAD_GATEWAY);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
