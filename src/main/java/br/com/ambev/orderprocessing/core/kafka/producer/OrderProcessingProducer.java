package br.com.ambev.orderprocessing.core.kafka.producer;

import br.com.ambev.orderprocessing.core.dto.OrderIdentificationDTO;

public interface OrderProcessingProducer {

    String sendOrder(OrderIdentificationDTO order);
}
