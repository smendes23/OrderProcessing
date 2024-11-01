package br.com.ambev.orderprocessing.core.service.impl;

import br.com.ambev.orderprocessing.core.dto.OrderDTO;
import br.com.ambev.orderprocessing.core.dto.OrderIdentificationDTO;
import br.com.ambev.orderprocessing.core.dto.ProductDTO;
import br.com.ambev.orderprocessing.core.entity.Order;
import br.com.ambev.orderprocessing.core.kafka.producer.OrderProcessingProducer;
import br.com.ambev.orderprocessing.core.repository.OrderRepository;
import br.com.ambev.orderprocessing.core.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessingServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderProcessingProducer producer;

    @InjectMocks
    private ProcessingServiceImpl processingService;

    private OrderDTO orderDTO;

    @BeforeEach
    public void setup() {
        ProductDTO productDTO = new ProductDTO("Product1", BigDecimal.valueOf(10.0), 1);
        orderDTO = new OrderDTO(List.of(productDTO),BigDecimal.ZERO,"EM_APROVACAO","12345");
    }

    @Test
    public void testValidateOrderPersistenceAndPublishWhenOrderExistsThenAddProductsAndPublish() {
        ProductDTO productDTO = new ProductDTO("Product1", BigDecimal.valueOf(10.0), 1);
        // Arrange
        Order existingOrder = new Order(1L, BigDecimal.valueOf(10.0), "ARPOVADO", "12345");

        OrderIdentificationDTO orderIdentificationDTO = new OrderIdentificationDTO(existingOrder.getIdempotencyKey());

        when(orderRepository.findByIdempotencyKey(orderDTO.idempotencyKey())).thenReturn(Optional.of(existingOrder));

        // Act
        processingService.validateOrderPersistenceAndPublish(orderDTO);

        // Assert
        verify(productRepository, times(1)).saveAll(any());
        verify(producer, times(1)).sendOrder(orderIdentificationDTO);
    }

    @Test
    public void testValidateOrderPersistenceAndPublishWhenOrderDoesNotExistThenSaveOrderAndProductsAndPublish() {
        // Arrange
        Order savedOrder = new Order(1L, BigDecimal.valueOf(100.0), "COMPLETED", "idempotencyKey1");
        OrderIdentificationDTO orderIdentificationDTO = new OrderIdentificationDTO(savedOrder.getIdempotencyKey());

        when(orderRepository.findByIdempotencyKey(orderDTO.idempotencyKey())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        processingService.validateOrderPersistenceAndPublish(orderDTO);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).saveAll(any());
        verify(producer, times(1)).sendOrder(orderIdentificationDTO);
    }

    @Test
    public void testValidateOrderPersistenceAndPublishWhenExceptionThrownWhileSavingProductsThenThrowRuntimeException() {
        // Arrange
        Order savedOrder = new Order(1L, BigDecimal.valueOf(100.0), "COMPLETED", "idempotencyKey1");
        when(orderRepository.findByIdempotencyKey(orderDTO.idempotencyKey())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doThrow(new RuntimeException("Error saving products")).when(productRepository).saveAll(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> processingService.validateOrderPersistenceAndPublish(orderDTO));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).saveAll(any());
    }
}
