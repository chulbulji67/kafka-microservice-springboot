package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
@EnableKafka
@EnableDiscoveryClient
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class);
    }

    @KafkaListener(topics = "notificationTopics")
    public void getNotification(OrderPlacedEvent orderPlacedEvent){
        System.out.println("Why Log not working");
        log.info("order received with order id {}", orderPlacedEvent.getOrderId());
    }
}

