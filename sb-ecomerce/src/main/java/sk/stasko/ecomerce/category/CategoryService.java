package sk.stasko.ecomerce.category;

import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;

public interface CategoryService {
    PaginationDto<CategoryDto> findAll(PaginationRequest paginationRequest);
    void save(CategoryDto entity);
    boolean update(Long id, CategoryDto entity);
    CategoryDto findById(Long id);
    boolean deleteById(Long id);
}
