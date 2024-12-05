package sk.stasko.ecomerce.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public Set<RoleEntity> determineRoles(Set<String> userRequestRoles) {
        // If no roles are provided, assign the default ROLE_USER
        if (userRequestRoles == null || userRequestRoles.isEmpty()) {
            return Set.of(findByRoleName(AppRole.ROLE_USER));
        }

        // Map requested roles to RoleEntity and collect them into a set
        return userRequestRoles.stream()
                .map(this::mapRoleNameToEntity)
                .collect(Collectors.toUnmodifiableSet());
    }

    private RoleEntity mapRoleNameToEntity(String roleName) {
        return switch (roleName.toLowerCase()) {
            case RoleConstants.ADMIN_ROLE_REQUEST -> findByRoleName(AppRole.ROLE_ADMIN);
            case RoleConstants.SELLER_ROLE_REQUEST -> findByRoleName(AppRole.ROLE_SELLER);
            default -> findByRoleName(AppRole.ROLE_USER);
        };
    }

    private RoleEntity findByRoleName(AppRole appRole) {
        log.info("Fetching user role {} from db ...", appRole.name());
        return roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new ResourceNotFoundException("RoleEntity", "roleName", appRole.name()));
    }
}
