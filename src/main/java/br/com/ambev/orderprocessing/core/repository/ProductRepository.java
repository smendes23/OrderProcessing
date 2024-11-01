package br.com.ambev.orderprocessing.core.repository;

import br.com.ambev.orderprocessing.core.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
