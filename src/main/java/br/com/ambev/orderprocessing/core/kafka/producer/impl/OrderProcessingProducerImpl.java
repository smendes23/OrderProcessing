package br.com.ambev.orderprocessing.core.kafka.producer.impl;

import br.com.ambev.orderprocessing.core.dto.OrderIdentificationDTO;
import br.com.ambev.orderprocessing.core.exception.ProducerException;
import br.com.ambev.orderprocessing.core.kafka.producer.OrderProcessingProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderProcessingProducerImpl implements OrderProcessingProducer {

    private final KafkaTemplate<String, OrderIdentificationDTO> kafkaTemplate;

    @Value("${kafka.topic.producer}")
    private String topic ;

    @Override
    public String sendOrder(final OrderIdentificationDTO order) {

        log.info("Publish order to topic: {}", topic);
        CompletableFuture<SendResult<String, OrderIdentificationDTO>> future = kafkaTemplate.send(topic, order);

        return future
                .thenApply(result -> result.getProducerRecord().value().toString())
                .exceptionally(e -> {
                    log.error("Error to publish order: {}", order, e);
                    throw new ProducerException(e.getMessage());
                })
                .join();
    }
}
