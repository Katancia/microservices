package pl.microservices.notification;

import org.springframework.stereotype.Service;
import pl.microservices.clients.notification.NotificationRequest;

import java.time.LocalDateTime;

@Service
public record NotificationService(NotificationRepository notificationRepository) {


    public void send(NotificationRequest notificationRequest) {
        Notification notification = Notification.builder()
                .toCustomerId(notificationRequest.toCustomerId())
                .toCustomerEmail(notificationRequest.toCustomerEmail())
                .message(notificationRequest.message())
                .sentAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }
}
