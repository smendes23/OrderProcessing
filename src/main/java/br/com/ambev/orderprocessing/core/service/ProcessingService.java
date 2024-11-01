package br.com.ambev.orderprocessing.core.service;

import br.com.ambev.orderprocessing.core.dto.OrderDTO;

public interface ProcessingService {

    void validateOrderPersistenceAndPublish(final OrderDTO orderDTO);

}
