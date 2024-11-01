package br.com.ambev.orderprocessing.core.mapper;

import br.com.ambev.orderprocessing.core.dto.OrderDTO;
import br.com.ambev.orderprocessing.core.dto.OrderIdentificationDTO;
import br.com.ambev.orderprocessing.core.entity.Order;

public class OrderMapper {

    public static Order toEntity(final OrderDTO request){
        return Order.builder()
                .idempotencyKey(request.idempotencyKey())
                .status(request.status())
                .totalPrice(request.totalAmount())
                .build();
    }

    public static OrderIdentificationDTO toPublishDTO(final String idempotencyKey){
        return OrderIdentificationDTO.builder()
                .idempotencyKey(idempotencyKey)
                .build();
    }
}
