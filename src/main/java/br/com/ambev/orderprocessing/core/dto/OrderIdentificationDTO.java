package br.com.ambev.orderprocessing.core.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderIdentificationDTO(String idempotencyKey) {
}
