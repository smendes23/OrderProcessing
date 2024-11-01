package br.com.ambev.orderprocessing.core.kafka.consumer.impl;

import br.com.ambev.orderprocessing.core.exception.ConsumerException;
import br.com.ambev.orderprocessing.core.kafka.consumer.OrderProcessingConsumer;
import br.com.ambev.orderprocessing.core.dto.OrderDTO;
import br.com.ambev.orderprocessing.core.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessingConsumerImpl implements OrderProcessingConsumer {

    private final ProcessingService service;

    @KafkaListener(topics = "${kafka.topic.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void processOrder(final OrderDTO order) {
        try{
            log.info("Received order: {}", order);
            service.validateOrderPersistenceAndPublish(order);
        }catch (Exception e){
            log.error("Error to process order: {}", order, e);
            throw new ConsumerException(e.getMessage());
        }
    }
}
