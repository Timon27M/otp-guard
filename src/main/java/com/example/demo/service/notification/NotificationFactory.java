package com.example.demo.service.notification;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationFactory {

    private final Map<String, INotificationService> notificationServices;

    public NotificationFactory(Map<String, INotificationService> notificationServices) {
        this.notificationServices = notificationServices;
    }

    public INotificationService getService(String channel) {
        String beanName = channel.toLowerCase() + "NotificationService";
        INotificationService service = notificationServices.get(beanName);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported notification channel: " + channel);
        }
        return service;
    }
}
