package pl.microservices.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "pl.microservices.notification",
                "pl.microservices.amqp",
        }
)
@EnableEurekaClient
@EnableFeignClients(
        basePackages = "pl.microservices.clients"
)
public class NotificationApplication {
    public static void main(String[] args) {

        SpringApplication.run(NotificationApplication.class, args);
    }
}
