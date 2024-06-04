package Arquisoft.ExamenPractico.controller;

import Arquisoft.ExamenPractico.dto.ProductDTO;
import Arquisoft.ExamenPractico.model.Product;
import Arquisoft.ExamenPractico.model.Warehouse;
import Arquisoft.ExamenPractico.repository.ProductRepository;
import Arquisoft.ExamenPractico.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
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
    public ResponseEntity<CollectionModel<EntityModel<Product>>> getProductsByWarehouse(
            @RequestHeader(value = "API-VERSION", required = false, defaultValue = "1") String apiVersion,
            @PathVariable Long id) {

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        List<EntityModel<Product>> products = warehouse.getProducts().stream()
                .map(product -> EntityModel.of(product,
                        linkTo(methodOn(WarehouseController.class).getProductById(apiVersion, product.getId())).withSelfRel(),
                        linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(apiVersion, id)).withRel("products"),
                        linkTo(methodOn(WarehouseController.class).getWarehouseById(apiVersion, id)).withRel("warehouse")))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("API-VERSION", apiVersion)
                .body(CollectionModel.of(products,
                        linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(apiVersion, id)).withSelfRel()));
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<EntityModel<Product>> addProductToWarehouse(
            @RequestHeader(value = "API-VERSION", required = false, defaultValue = "1") String apiVersion,
            @PathVariable Long id,
            @RequestBody ProductDTO newProductRequest) {

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Product newProduct = new Product(newProductRequest.getName(), newProductRequest.getQuantity());
        newProduct.setWarehouse(warehouse);
        Product savedProduct = productRepository.save(newProduct);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("API-VERSION", apiVersion)
                .body(EntityModel.of(savedProduct,
                        linkTo(methodOn(WarehouseController.class).getProductById(apiVersion, savedProduct.getId())).withSelfRel(),
                        linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(apiVersion, id)).withRel("products"),
                        linkTo(methodOn(WarehouseController.class).getWarehouseById(apiVersion, id)).withRel("warehouse")));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<EntityModel<Product>> getProductById(
            @RequestHeader(value = "API-VERSION", required = false, defaultValue = "1") String apiVersion,
            @PathVariable Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ResponseEntity.ok()
                .header("API-VERSION", apiVersion)
                .body(EntityModel.of(product,
                        linkTo(methodOn(WarehouseController.class).getProductById(apiVersion, productId)).withSelfRel(),
                        linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(apiVersion, product.getWarehouse().getId())).withRel("products"),
                        linkTo(methodOn(WarehouseController.class).getWarehouseById(apiVersion, product.getWarehouse().getId())).withRel("warehouse")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Warehouse>> getWarehouseById(
            @RequestHeader(value = "API-VERSION", required = false, defaultValue = "1") String apiVersion,
            @PathVariable Long id) {

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        return ResponseEntity.ok()
                .header("API-VERSION", apiVersion)
                .body(EntityModel.of(warehouse,
                        linkTo(methodOn(WarehouseController.class).getWarehouseById(apiVersion, id)).withSelfRel(),
                        linkTo(methodOn(WarehouseController.class).getProductsByWarehouse(apiVersion, id)).withRel("products")));
    }
}
