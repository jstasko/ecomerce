package sk.stasko.ecomerce.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.stasko.ecomerce.cart.CartService;
import sk.stasko.ecomerce.category.CategoryEntity;
import sk.stasko.ecomerce.category.CategoryService;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ProductNotAvailableException;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.common.service.ImageUploadService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static sk.stasko.ecomerce.common.constants.CommonConstants.PATH_TO_SAVE_IMAGE;
import static sk.stasko.ecomerce.common.util.DbUtil.getSortForPagination;
import static sk.stasko.ecomerce.product.ProductConstants.PERCENTAGE_OF_DISCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository iProductRepository;
    private final CategoryService categoryService;
    private final ImageUploadService imageUploadService;
    private final CartService cartService;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductEntity findById(Long id) {
        return iProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductEntity", "productId", id.toString()));
    }

    @Override
    public ProductEntity retrieveCorrectProduct(Long id, Integer quantity) {
        var productEntity = this.findById(id);

        if (!productEntity.isAvailable()) {
            throw new ProductNotAvailableException(productEntity.getProductName() + " is not available");
        }

        if (productEntity.getQuantity() < quantity) {
            throw new ProductNotAvailableException(productEntity.getProductName() + " is not available, in this quantity");
        }

        return productEntity;
    }

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
        // paging
        Pageable pageDetails = PageRequest.of(paginationRequest.page(), paginationRequest.limit(), getSortForPagination(paginationRequest));

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

        CategoryEntity category = categoryService.findEntityById(categoryId);

        log.info("Saving new product: {}", entity.getProductName());

        iProductRepository.save(createNewProduct(category, entity));
    }

    private ProductEntity createNewProduct(CategoryEntity category, ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(productDto.getProductName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setCategory(category);
        productEntity.setImage(null);
        productEntity.setDiscount(productDto.getDiscount());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setSpecialPrice(calculateSpecialPrice(productDto));
        return productEntity;
    }

    private BigDecimal calculateSpecialPrice(ProductDto productDto) {
        var discountPercentage = productDto.getDiscount().multiply(BigDecimal.valueOf(PERCENTAGE_OF_DISCOUNT));
        var discount = discountPercentage.multiply(productDto.getPrice());
        return productDto.getPrice().subtract(discount);
    }

    @Override
    public boolean update(Long productId, ProductDto productDto) {
        ProductEntity productEntity = this.findById(productId);

        log.info("Updating product with id: {}", productEntity.getId());

        productEntity.setProductName(productDto.getProductName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setDiscount(productDto.getDiscount());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setSpecialPrice(calculateSpecialPrice(productDto));

        ProductEntity savedProduct = iProductRepository.save(productEntity);
        cartService.updateProductInCarts(savedProduct.getId(), savedProduct.getPrice());
        return true;
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) {
        // get the product
        ProductEntity productEntity = findById(productId);
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
