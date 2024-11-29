package sk.stasko.ecomerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PaginationDto<T> {
    List<T> content;
    Integer pageNumber;
    Integer pageSize;
    Long totalElements;
    Integer totalPages;
    Boolean lastPage;
}
