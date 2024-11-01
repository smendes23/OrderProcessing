package br.com.ambev.orderprocessing.core.kafka.consumer;

import br.com.ambev.orderprocessing.core.dto.OrderDTO;

public interface OrderProcessingConsumer {

    void processOrder(OrderDTO order);
}
