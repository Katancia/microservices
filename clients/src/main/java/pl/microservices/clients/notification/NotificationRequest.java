package pl.microservices.clients.notification;

import lombok.Builder;

@Builder
public record NotificationRequest(Long toCustomerId, String toCustomerEmail, String message) {
}
