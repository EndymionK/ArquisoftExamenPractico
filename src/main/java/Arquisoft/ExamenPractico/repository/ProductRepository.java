package Arquisoft.ExamenPractico.repository;

import Arquisoft.ExamenPractico.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}