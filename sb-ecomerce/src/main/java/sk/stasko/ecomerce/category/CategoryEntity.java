package sk.stasko.ecomerce.category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import sk.stasko.ecomerce.common.entity.BaseEntity;

@Entity(name = "categories")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class CategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(min = 5, message = "Category name must contain at least 5 characters")
    private String categoryName;
}
