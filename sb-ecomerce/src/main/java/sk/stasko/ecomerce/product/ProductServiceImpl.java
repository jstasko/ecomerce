package sk.stasko.ecomerce.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.stasko.ecomerce.category.CategoryEntity;
import sk.stasko.ecomerce.category.CategoryRepository;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.common.service.ImageUploadService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static sk.stasko.ecomerce.common.constants.CommonConstants.PATH_TO_SAVE_IMAGE;
import static sk.stasko.ecomerce.product.ProductConstants.PERCENTAGE_OF_DISCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository iProductRepository;
    private final CategoryRepository iCategoryRepository;
    private final ImageUploadService imageUploadService;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    @Override
    public PaginationDto<ProductDto> findByCategoryId(Long categoryId, PaginationRequest paginationRequest) {
        return findAllProducts(paginationRequest,
                pageable -> iProductRepository.findAllByCategory_Id(categoryId, pageable));
    }

    @Override
    public PaginationDto<ProductDto> findByKeyword(String keyword, PaginationRequest paginationRequest) {
        return findAllProducts(paginationRequest,
                pageable -> iProductRepository.findAllByProductNameContainingIgnoreCase(keyword, pageable));
    }

    @Override
    public PaginationDto<ProductDto> findAll(PaginationRequest paginationRequest) {
        return findAllProducts(paginationRequest, iProductRepository::findAll);
    }

    private PaginationDto<ProductDto> findAllProducts(
            PaginationRequest paginationRequest,
            Function<Pageable, Page<ProductEntity>> repositoryCall
    ) {
        // sorting
        Sort sortAndOrderBy = paginationRequest.sortOrder().equalsIgnoreCase("asc")
                ? Sort.by(paginationRequest.sortBy()).ascending() : Sort.by(paginationRequest.sortBy()).descending();

        // paging
        Pageable pageDetails = PageRequest.of(paginationRequest.page(), paginationRequest.limit(), sortAndOrderBy);

        log.info("Fetching products from db ... ");
        Page<ProductEntity> entities = repositoryCall.apply(pageDetails);
        List<ProductDto> responseContent = entities.getContent()
                .stream()
                .map(productMapper::toDto)
                .toList();

        return new PaginationDto<>(
                responseContent,
                entities.getNumber(),
                entities.getSize(),
                entities.getTotalElements(),
                entities.getTotalPages(),
                entities.isLast()
        );
    }

    @Override
    public void save(Long categoryId, ProductDto entity) {
        Optional<ProductEntity> productEntity = iProductRepository.findByProductName(entity.getProductName());

        if (productEntity.isPresent()) {
            throw new EntityAlreadyExists("Product already registered with given productName "+ entity.getProductName());
        }

        CategoryEntity category = iCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId.toString()));


        log.info("Saving new product: {}", entity.getProductName());

        iProductRepository.save(createNewProduct(category, entity));
    }

    private ProductEntity createNewProduct(CategoryEntity categoryDto, ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(productDto.getProductName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setCategory(categoryDto);
        productEntity.setImage(null);
        productEntity.setDiscount(productDto.getDiscount());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setSpecialPrice(calculateSpecialPrice(productDto));
        return productEntity;
    }

    private BigDecimal calculateSpecialPrice(ProductDto productDto) {
        var discountPercentage = BigDecimal.valueOf(productDto.getDiscount()).multiply(BigDecimal.valueOf(PERCENTAGE_OF_DISCOUNT));
        var discount = discountPercentage.multiply(productDto.getPrice());
        return productDto.getPrice().subtract(discount);
    }

    @Override
    public boolean update(Long productId, ProductDto productDto) {
        ProductEntity productEntity = iProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId.toString()));

        log.info("Updating product with id: {}", productEntity.getProductId());

        productEntity.setProductName(productDto.getProductName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setDiscount(productDto.getDiscount());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setSpecialPrice(calculateSpecialPrice(productDto));
        productEntity.setPrice(productDto.getPrice());

        iProductRepository.save(productEntity);
        return true;
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) {
        // get the product
        ProductEntity productEntity = iProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId.toString()));
        // Upload image on server
        String fileName = null;
        try {
            fileName = imageUploadService.uploadImage(PATH_TO_SAVE_IMAGE, image);
        } catch (IOException exception) {
            log.warn("Error while uploading image {}", exception.getMessage(), exception);
        }
        // updating the new file name to product
        productEntity.setImage(fileName);
        var updatedProduct = iProductRepository.save(productEntity);

        return productMapper.toDto(updatedProduct);
    }
}
