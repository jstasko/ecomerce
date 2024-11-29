package sk.stasko.ecomerce.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CategoryDto {
    @NotBlank
    private String categoryName;
}
