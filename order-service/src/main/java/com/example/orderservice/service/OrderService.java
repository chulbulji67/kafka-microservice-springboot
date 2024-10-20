package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final WebClient webClient;

    @Autowired
    public OrderService(KafkaTemplate kafkaTemplate,@LoadBalanced WebClient.Builder webClientBuilder) {
        this.kafkaTemplate = kafkaTemplate;
        this.webClient = webClientBuilder.build();
    }

    public void placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        log.info("Placed Method Called");
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtos().stream().map(this::mapToDto).toList();
        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = orderRequest.getOrderLineItemsDtos().stream().map(OrderLineItemsDto::getSkuCode).toList();

        //Call Inventory Service and check if it is prisent or not
//        InventoryResponse[] inventoryResponses = webClient.get().uri("http://inventory-service/api/inventory",
//                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()).retrieve()
//                .bodyToMono(InventoryResponse[].class).block();

//        boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isPresent);
//        log.info("is all Product available {}", allProductsInStock);


        try {
            InventoryResponse[] inventoryResponses = webClient.get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopics", new OrderPlacedEvent(order.getOrderNumber()));
        } catch (Exception e) {
            log.error("Error calling Inventory Service: {}", e.getMessage());
            throw e; // Rethrow the exception to trigger the fallback
        }


//        if(Boolean.TRUE.equals(allProductsInStock)){
//        orderRepository.save(order);
//        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));}
//
//        else {
//            throw new IllegalArgumentException("Product is Not in stock");
//        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
       return OrderLineItems.builder().id(orderLineItemsDto.getId())
                .price(orderLineItemsDto.getPrice())
                .skuCode(orderLineItemsDto.getSkuCode())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }
}
