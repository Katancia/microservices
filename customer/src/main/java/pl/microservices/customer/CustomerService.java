package pl.microservices.customer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.microservices.clients.fraud.FraudCheckResponse;
import pl.microservices.clients.fraud.FraudClient;
import pl.microservices.clients.notification.NotificationClient;
import pl.microservices.clients.notification.NotificationRequest;

@Service
public record CustomerService(CustomerRepository customerRepository,
                              RestTemplate restTemplate,
                              FraudClient fraudClient,
                              NotificationClient notificationClient) {
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

        //todo: make it async & add to queue
        sendNotification(customer);
    }

    private void checkIfCustomerIsFraudster(Customer customer) {
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
    }

    private void sendNotification(Customer customer) {
        notificationClient.sendNotification(
                NotificationRequest.builder()
                        .toCustomerId(customer.getId())
                        .toCustomerEmail(customer.getEmail())
                        .message("message")
                        .build()
        );
    }
}
