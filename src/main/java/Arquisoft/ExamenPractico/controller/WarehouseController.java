package Arquisoft.ExamenPractico.controller;

import Arquisoft.ExamenPractico.dto.ProductDTO;
import Arquisoft.ExamenPractico.model.Product;
import Arquisoft.ExamenPractico.model.Warehouse;
import Arquisoft.ExamenPractico.repository.ProductRepository;
import Arquisoft.ExamenPractico.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v3/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{id}/products")
    public ResponseEntity<List<EntityModel<Product>>> getProductsByWarehouse(@PathVariable Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));

        List<EntityModel<Product>> products = warehouse.getProducts().stream()
                .map(product -> EntityModel.of(product,
                        linkTo(methodOn(WarehouseController.class).getProductById(product.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<EntityModel<Product>> addProductToWarehouse(@PathVariable Long id, @RequestBody ProductDTO newProductRequest) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));

        Product newProduct = new Product(newProductRequest.getName(), newProductRequest.getQuantity());
        newProduct.setWarehouse(warehouse);

        Product savedProduct = productRepository.save(newProduct);

        EntityModel<Product> entityModel = EntityModel.of(savedProduct,
                linkTo(methodOn(WarehouseController.class).getProductById(savedProduct.getId())).withSelfRel(),
                linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(id)).withRel("products"));

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<EntityModel<Product>> getProductById(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Link selfLink = linkTo(methodOn(WarehouseController.class).getProductById(productId)).withSelfRel();
        Link productsLink = linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(product.getWarehouse().getId())).withRel("products");

        return ResponseEntity.ok(EntityModel.of(product, selfLink, productsLink));
    }

}
