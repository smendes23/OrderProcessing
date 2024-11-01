package br.com.ambev.orderprocessing.core.service.impl;

import br.com.ambev.orderprocessing.core.dto.OrderDTO;
import br.com.ambev.orderprocessing.core.entity.Order;
import br.com.ambev.orderprocessing.core.kafka.producer.OrderProcessingProducer;
import br.com.ambev.orderprocessing.core.mapper.ProductMapper;
import br.com.ambev.orderprocessing.core.repository.OrderRepository;
import br.com.ambev.orderprocessing.core.repository.ProductRepository;
import br.com.ambev.orderprocessing.core.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static br.com.ambev.orderprocessing.core.mapper.OrderMapper.toEntity;
import static br.com.ambev.orderprocessing.core.mapper.OrderMapper.toPublishDTO;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessingServiceImpl implements ProcessingService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProcessingProducer producer;

    @Override
    public void validateOrderPersistenceAndPublish(final OrderDTO request) {
        log.info("Validate persist and publish order");
        orderRepository.findByIdempotencyKey(request.idempotencyKey())
                .ifPresentOrElse(
                order -> handleExistingOrder(request),
                () -> handleNewOrder(request)
        );
    }

    private void handleExistingOrder(final OrderDTO request) {
        log.info("Add new products to Order: {}", request);

        saveProduct(request);

        producer.sendOrder(toPublishDTO(request.idempotencyKey()));
    }

    private void handleNewOrder(final OrderDTO request) {
        log.info("Convert and save new Order: {}", request);

        Order savedOrder = orderRepository.save(toEntity(request));

        saveProduct(request);

        log.info("Publish new order with idempotencyKey: {}", savedOrder.getIdempotencyKey());
        producer.sendOrder(toPublishDTO(savedOrder.getIdempotencyKey()));
    }


    private void saveProduct(final OrderDTO request) {
        try{
            log.info("Save products: {}", request.products());
            productRepository.saveAll(
                    request
                            .products()
                            .stream()
                            .map(productDTO -> ProductMapper.toEntity(productDTO, request.idempotencyKey())).toList());
        } catch (Exception e) {
            log.error("Error to save products: {}", request.products(), e);
            throw new RuntimeException(e);
        }
    }
}
