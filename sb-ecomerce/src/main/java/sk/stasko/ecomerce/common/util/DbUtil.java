package sk.stasko.ecomerce.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.payment.PaymentEntity;
import sk.stasko.ecomerce.payment.PaymentMethod;
import sk.stasko.ecomerce.payment.PaymentRepository;
import sk.stasko.ecomerce.role.AppRole;
import sk.stasko.ecomerce.role.RoleEntity;
import sk.stasko.ecomerce.role.RoleRepository;
import sk.stasko.ecomerce.user.UserEntity;
import sk.stasko.ecomerce.user.UserRepository;

import java.util.Set;

@Slf4j
public class DbUtil {
    private DbUtil() {}

    public static Sort getSortForPagination(PaginationRequest paginationRequest) {
        // sorting
        return paginationRequest.sortOrder().equalsIgnoreCase("asc")
                ? Sort.by(paginationRequest.sortBy()).ascending() : Sort.by(paginationRequest.sortBy()).descending();
    }

    private static RoleEntity getOrCreateRole(RoleRepository roleRepository, AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity(roleName);
                    log.info("Creating role: {}", roleName);
                    return roleRepository.save(role);
                });
    }

    private static void createUserIfNotExists(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                       String username, String email, String password, Set<RoleEntity> roles) {
        if (!userRepository.existsByUsername(username)) {
            UserEntity user = new UserEntity(email, passwordEncoder.encode(password), username);
            user.setRoles(roles);
            userRepository.save(user);
            log.info("Created user: {}", username);
        } else {
            log.info("User already exists: {}", username);
        }
    }

    private static void createPaymentMethodIfNotExists(PaymentRepository paymentRepository, PaymentMethod paymentMethod) {
        if (!paymentRepository.existsByPaymentMethod(paymentMethod)) {
            PaymentEntity paymentEntity = new PaymentEntity();
            paymentEntity.setPaymentMethod(paymentMethod);
            paymentRepository.save(paymentEntity);
            log.info("Created paymentMethod: {}", paymentMethod.getDescription());
        } else {
            log.info("PaymentMethod already exists: {}", paymentMethod.getDescription());
        }
    }

    public static void init(RoleRepository roleRepository, UserRepository userRepository, PaymentRepository paymentRepository, PasswordEncoder passwordEncoder) {
        try {
            // Initialize roles
            RoleEntity userRole = getOrCreateRole(roleRepository, AppRole.ROLE_USER);
            RoleEntity sellerRole = getOrCreateRole(roleRepository, AppRole.ROLE_SELLER);
            RoleEntity adminRole = getOrCreateRole(roleRepository, AppRole.ROLE_ADMIN);

            Set<RoleEntity> userRoles = Set.of(userRole);
            Set<RoleEntity> sellerRoles = Set.of(sellerRole, userRole);
            Set<RoleEntity> adminRoles = Set.of(adminRole, sellerRole, userRole);

            // Initialize users
            createUserIfNotExists(userRepository, passwordEncoder, "user1", "user1@gmail.com", "passwordUser", userRoles);
            createUserIfNotExists(userRepository, passwordEncoder, "seller1", "seller1@gmail.com", "passwordSeller", sellerRoles);
            createUserIfNotExists(userRepository, passwordEncoder, "admin1", "admin1@gmail.com", "passwordAdmin", adminRoles);


            createPaymentMethodIfNotExists(paymentRepository, PaymentMethod.P);
            createPaymentMethodIfNotExists(paymentRepository, PaymentMethod.B);
            createPaymentMethodIfNotExists(paymentRepository, PaymentMethod.D);
            createPaymentMethodIfNotExists(paymentRepository, PaymentMethod.C);
            log.info("Initialization completed successfully.");

        } catch (Exception ex) {
            log.error("Error during initialization: {}", ex.getMessage(), ex);
        }
    }
}
