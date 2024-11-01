package br.com.ambev.orderprocessing.core.mapper;

import br.com.ambev.orderprocessing.core.dto.ProductDTO;
import br.com.ambev.orderprocessing.core.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static Product toEntity(final  ProductDTO dto, final  String idempotencyKey){
        return Product.builder()
                .name(dto.name())
                .price(dto.price())
                .idempotencyKey(idempotencyKey)
                .quantity(dto.quantity())
                .build();
    }
}
