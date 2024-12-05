package sk.stasko.ecomerce.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sk.stasko.ecomerce.common.security.jwt.AuthEntryPointJwt;
import sk.stasko.ecomerce.common.security.jwt.AuthTokenFilter;
import sk.stasko.ecomerce.common.security.jwt.JwtUtils;
import sk.stasko.ecomerce.role.AppRole;
import sk.stasko.ecomerce.role.RoleEntity;
import sk.stasko.ecomerce.role.RoleRepository;
import sk.stasko.ecomerce.user.UserEntity;
import sk.stasko.ecomerce.user.UserRepository;

import java.util.Set;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
//                                .requestMatchers("/api/admin/**").permitAll()
//                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api-test/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .anyRequest().authenticated()
                );
        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
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

                log.info("Initialization completed successfully.");

            } catch (Exception ex) {
                log.error("Error during initialization: " + ex.getMessage(), ex);
            }
        };
    }

    private RoleEntity getOrCreateRole(RoleRepository roleRepository, AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity(roleName);
                    log.info("Creating role: {}", roleName);
                    return roleRepository.save(role);
                });
    }

    private void createUserIfNotExists(UserRepository userRepository, PasswordEncoder passwordEncoder,
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
}
