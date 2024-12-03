package sk.stasko.ecomerce.product;

import org.springframework.web.multipart.MultipartFile;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;

public interface ProductService {
    PaginationDto<ProductDto> findAll(PaginationRequest paginationRequest);
    void save(Long categoryId, ProductDto entity);
    boolean update(Long productId, ProductDto productDto);
    PaginationDto<ProductDto> findByCategoryId(Long categoryId, PaginationRequest paginationRequest);
    PaginationDto<ProductDto> findByKeyword(String keyword, PaginationRequest paginationRequest);
    ProductDto updateProductImage(Long productId, MultipartFile image);
}
