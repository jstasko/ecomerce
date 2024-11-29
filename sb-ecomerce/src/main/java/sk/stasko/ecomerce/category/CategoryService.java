package sk.stasko.ecomerce.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.common.service.CrudService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements CrudService<CategoryDto, Long> {

    private final CategoryRepository iCategoryRepository;
    private final CategoryMapper userMapper = CategoryMapper.INSTANCE;


    @Override
    public PaginationDto<CategoryDto> findAll(PaginationRequest paginationRequest) {
        Sort sortAndOrderBy = paginationRequest.sortOrder().equalsIgnoreCase("asc")
                ? Sort.by(paginationRequest.sortBy()).ascending() : Sort.by(paginationRequest.sortBy()).descending();
        Pageable pageDetails = PageRequest.of(paginationRequest.page(), paginationRequest.limit(), sortAndOrderBy);

        log.info("Fetching all users");
        Page<CategoryEntity> categoryEntities = iCategoryRepository.findAll(pageDetails);
        List<CategoryDto> responseCategoryList = categoryEntities.getContent()
                .stream()
                .map(userMapper::toDto)
                .toList();

        return new PaginationDto<>(
                responseCategoryList,
                categoryEntities.getNumber(),
                categoryEntities.getSize(),
                categoryEntities.getTotalElements(),
                categoryEntities.getTotalPages(),
                categoryEntities.isLast()
        );
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        log.info("Fetching user with id: {}", categoryId);
        CategoryEntity category = iCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId.toString()));
        return CategoryMapper.INSTANCE.toDto(category);
    }

    @Override
    public void save(CategoryDto entity) {
        Optional<CategoryEntity> optionalCards = iCategoryRepository.findByCategoryName(entity.getCategoryName());

        if (optionalCards.isPresent()) {
            throw new EntityAlreadyExists("Category already registered with given categoryName "+ entity.getCategoryName());
        }

        log.info("Saving new category: {}", entity);

        iCategoryRepository.save(createNewCategory(entity));
    }

    private CategoryEntity createNewCategory(CategoryDto categoryDto) {
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setCategoryName(categoryDto.getCategoryName());
        return newCategory;
    }

    @Override
    public boolean update(Long categoryId, CategoryDto entity) {
        log.info("Updating user with id: {}", categoryId);
        CategoryEntity category = iCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId.toString()));

        category.setCategoryName(entity.getCategoryName());

        iCategoryRepository.save(category);
        return true;
    }

    @Override
    public boolean deleteById(Long categoryId) {
        log.info("Deleting user with id: {}", categoryId);
        CategoryEntity category = iCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId.toString()));
        iCategoryRepository.delete(category);
        return true;
    }
}
