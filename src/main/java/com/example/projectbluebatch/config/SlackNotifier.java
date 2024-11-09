package com.example.projectbluebatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlackNotifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public void sendMessage(String title, String message) {

        Map<String, Object> payload = new HashMap<>();

        String formattedMessage = "*" + title + "*\n" + message;

        payload.put("text", formattedMessage);

        try {
            restTemplate.postForObject(slackWebhookUrl, payload, String.class);
        } catch (Exception e) {
            System.out.println("slack 알림 전송 오류");
        }
    }
}

