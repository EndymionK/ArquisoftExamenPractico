package Arquisoft.ExamenPractico.dto;

public class ProductDTO {
    private Long productId; // Nuevo campo
    private String name;
    private int quantity;

    // Getters y Setters

    public ProductDTO() {
    }

    public ProductDTO(Long productId, String name, int quantity) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
