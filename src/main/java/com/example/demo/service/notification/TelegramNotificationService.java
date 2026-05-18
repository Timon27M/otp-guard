package com.example.demo.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
@Service("telegramNotificationService")
public class TelegramNotificationService implements INotificationService {

    private final String botToken;
    private final String chatId;
    private final String telegramApiUrl;

    public TelegramNotificationService() {
        Properties config = loadConfig();
        this.botToken = config.getProperty("telegram.bot.token");
        this.chatId = config.getProperty("telegram.chat.id");
        this.telegramApiUrl = config.getProperty("telegram.api.url") + botToken;
    }

    @Override
    public void sendCode(String destination, String code) {
        String message = String.format("Your verification code is: %s", code);
        String url = String.format("%s/sendMessage?chat_id=%s&text=%s",
                telegramApiUrl,
                chatId,
                urlEncode(message));

        sendTelegramRequest(url);
    }

    @Override
    public String getChannelName() {
        return "TELEGRAM";
    }

    private void sendTelegramRequest(String url) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode != 200) {
                log.error("Telegram API error. Status code: {}", statusCode);
            } else {
                log.info("📱 OTP code sent via Telegram");
            }
        } catch (InterruptedException e) {
            log.error("Error sending Telegram message: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Error sending Telegram message: {}", e.getMessage(), e);
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private Properties loadConfig() {
        try (InputStream input = TelegramNotificationService.class.getClassLoader()
                .getResourceAsStream("telegram.properties")) {
            Properties props = new Properties();
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Telegram configuration", e);
        }
    }
}