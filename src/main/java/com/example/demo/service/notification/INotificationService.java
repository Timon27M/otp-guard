package com.example.demo.service.notification;

public interface INotificationService {
    void sendCode(String recipient, String code);

    String getChannelName();
}
