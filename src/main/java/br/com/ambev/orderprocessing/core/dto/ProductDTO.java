package br.com.ambev.orderprocessing.core.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDTO(
        String name,
        BigDecimal price,
        Integer quantity
) {
}
