package sk.stasko.ecomerce.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import sk.stasko.ecomerce.common.entity.BaseEntity;
import sk.stasko.ecomerce.product.ProductEntity;

import java.util.List;


@Entity()
@Table(name = "categories")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class CategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotBlank
    @Size(min = 5, message = "Category name must contain at least 5 characters")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProductEntity> products;
}
