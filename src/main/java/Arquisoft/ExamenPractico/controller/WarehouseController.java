package Arquisoft.ExamenPractico.controller;

import Arquisoft.ExamenPractico.dto.ProductDTO;
import Arquisoft.ExamenPractico.model.Product;
import Arquisoft.ExamenPractico.model.Warehouse;
import Arquisoft.ExamenPractico.repository.ProductRepository;
import Arquisoft.ExamenPractico.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getProductsByWarehouse(@PathVariable Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));

        List<Product> products = warehouse.getProducts();
        products.forEach(product -> product.setWarehouse(null));
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Product> addProductToWarehouse(
            @PathVariable Long id,
            @RequestBody ProductDTO newProductRequest,
            @RequestParam(name = "productId") Long productId) {

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        existingProduct.setQuantity(existingProduct.getQuantity() + newProductRequest.getQuantity());
        existingProduct.setWarehouse(warehouse);
        Product savedProduct = productRepository.save(existingProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

}
