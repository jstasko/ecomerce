package sk.stasko.ecomerce.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.auth.SignupRequest;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.role.RoleService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public void createUserEntity(SignupRequest signupRequest) {
        if (userRepository.existsByEmailOrUsername(signupRequest.email(), signupRequest.username())) {
            throw new EntityAlreadyExists("Username or email already exists");
        }

        UserEntity entity = new UserEntity(
                signupRequest.email(),
                passwordEncoder.encode(signupRequest.password()),
                signupRequest.username()
        );

        entity.setRoles(roleService.determineRoles(signupRequest.roles()));
        userRepository.save(entity);
        log.info("Created user entity for {} with role {}", signupRequest.username(), signupRequest.roles());
    }

    public UserEntity fetchLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user =  userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("UserEntity", "username", authentication.getName()));
        log.info("UserEntity fetched {}", user.getEmail());
        return user;
    }
}
