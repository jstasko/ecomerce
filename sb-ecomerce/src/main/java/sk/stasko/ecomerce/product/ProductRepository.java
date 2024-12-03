package sk.stasko.ecomerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAllByCategory_Id(Long categoryId, Pageable pageable);
    Page<ProductEntity> findAllByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<ProductEntity> findByProductName(String productName);
}
