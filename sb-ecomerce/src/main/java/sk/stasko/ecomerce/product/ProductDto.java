package sk.stasko.ecomerce.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long productId;
    private String description;
    private Double discount;
    private String image;
    private BigDecimal price;
    private String productName;
    private Integer quantity;
    private BigDecimal specialPrice;
}
