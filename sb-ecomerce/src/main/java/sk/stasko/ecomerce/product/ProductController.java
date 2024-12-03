package sk.stasko.ecomerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.stasko.ecomerce.common.constants.CommonConstants;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.dto.ResponseDto;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<PaginationDto<ProductDto>> getAllByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = ProductConstants.PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = ProductConstants.PAGE_LIMIT, required = false) int limit,
            @RequestParam(defaultValue = ProductConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = ProductConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        var request = new PaginationRequest(page, limit, sortBy, sortOrder);
        PaginationDto<ProductDto> entity = productService.findByCategoryId(categoryId, request);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/public/products")
    public ResponseEntity<PaginationDto<ProductDto>> getAll(
            @RequestParam(defaultValue = ProductConstants.PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = ProductConstants.PAGE_LIMIT, required = false) int limit,
            @RequestParam(defaultValue = ProductConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = ProductConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        var request = new PaginationRequest(page, limit, sortBy, sortOrder);
        PaginationDto<ProductDto> paginationDto = productService.findAll(request);
        return ResponseEntity.ok(paginationDto);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<PaginationDto<ProductDto>> getAllByKeyword(
            @PathVariable String keyword,
            @RequestParam(defaultValue = ProductConstants.PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = ProductConstants.PAGE_LIMIT, required = false) int limit,
            @RequestParam(defaultValue = ProductConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = ProductConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        var request = new PaginationRequest(page, limit, sortBy, sortOrder);
        PaginationDto<ProductDto> paginationDto = productService.findByKeyword(keyword, request);
        return ResponseEntity.ok(paginationDto);
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ResponseDto> create(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDto entity
    ) {
        productService.save(categoryId, entity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CommonConstants.MESSAGE_201));
    }

    @PutMapping("/public/products/{productId}")
    public ResponseEntity<ResponseDto> update(@PathVariable Long productId, @Valid @RequestBody ProductDto entity) {
        boolean isUpdated = productService.update(productId, entity);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CommonConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CommonConstants.MESSAGE_417_UPDATE));
        }
    }

    @PutMapping("/public/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image)
    {
        var response = productService.updateProductImage(productId, image);
        if (response != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(null);
        }
    }
}
