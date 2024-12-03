package sk.stasko.ecomerce.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import sk.stasko.ecomerce.category.CategoryEntity;
import sk.stasko.ecomerce.common.entity.BaseEntity;

import java.math.BigDecimal;

@Entity(name = "products")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank()
    @Size(min = 3, message = "Product description must contain at least 3 characters")
    private String description;
    private Double discount;
    private String image;
    private BigDecimal price;

    @NotBlank()
    @Size(min = 3, message = "Product name must contain at least 3 characters")
    private String productName;
    private Integer quantity;
    private BigDecimal specialPrice;

    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne()
    @ToString.Exclude
    private CategoryEntity category;
}
