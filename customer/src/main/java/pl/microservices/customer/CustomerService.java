package pl.microservices.customer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.microservices.amqp.RabbitMQMessageProducer;
import pl.microservices.clients.fraud.FraudCheckResponse;
import pl.microservices.clients.fraud.FraudClient;
import pl.microservices.clients.notification.NotificationRequest;

@Service
public record CustomerService(CustomerRepository customerRepository,
                              RestTemplate restTemplate,
                              FraudClient fraudClient,
                              RabbitMQMessageProducer rabbitMQMessageProducer) {
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        customerRepository.saveAndFlush(customer);
        // todo: check if email valid
        // todo: check if email not taken

        checkIfCustomerIsFraudster(customer);
        sendNotification(customer);
    }

    private void checkIfCustomerIsFraudster(Customer customer) {
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
    }

    private void sendNotification(Customer customer) {
        rabbitMQMessageProducer.publish(
                getNotificationRequest(customer),
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }

    private static NotificationRequest getNotificationRequest(Customer customer) {
        return NotificationRequest.builder()
                .toCustomerId(customer.getId())
                .toCustomerEmail(customer.getEmail())
                .message("message")
                .build();
    }
}
