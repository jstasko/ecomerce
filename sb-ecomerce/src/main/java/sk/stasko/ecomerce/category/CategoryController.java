package sk.stasko.ecomerce.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.dto.ResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long categoryId) {
        CategoryDto entity = categoryService.findById(categoryId);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<PaginationDto<CategoryDto>> getAll(
            @RequestParam(defaultValue = CategoryConstants.PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = CategoryConstants.PAGE_LIMIT, required = false) int limit,
            @RequestParam(defaultValue = CategoryConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = CategoryConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        var request = new PaginationRequest(page, limit, sortBy, sortOrder);
        PaginationDto<CategoryDto> paginationDto = categoryService.findAll(request);
        return ResponseEntity.ok(paginationDto);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<ResponseDto> create(@Valid @RequestBody CategoryDto entity) {
        categoryService.save(entity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CategoryConstants.MESSAGE_201));
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<ResponseDto> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto entity) {
        boolean isUpdated = categoryService.update(categoryId, entity);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CategoryConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CategoryConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<ResponseDto> delete(@PathVariable Long categoryId) {
        boolean isDeleted = categoryService.deleteById(categoryId);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CategoryConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CategoryConstants.MESSAGE_417_DELETE));
        }
    }
}
