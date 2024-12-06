package sk.stasko.ecomerce.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.common.dto.ErrorResponseDto;
import sk.stasko.ecomerce.common.security.jwt.JwtUtils;
import sk.stasko.ecomerce.common.security.service.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> authenticationOfUser(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );

            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ResponseCookie responseCookie = jwtUtils.generateJwtCookie(userDetails);

            // Create response
            UserInfoResponse response = createUserInfoResponse(userDetails);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(response);

        } catch (AuthenticationException ex) {
            // Handle authentication failures
            return handleAuthenticationFailure(ex);
        }
    }

    public UserInfoResponse getUserInfoResponseFromAuthentication(Authentication authentication) {
        return authentication != null ?  this.createUserInfoResponse((UserDetailsImpl) authentication.getPrincipal()) : null;
    }

    public ResponseCookie clearCookie() {
        return this.jwtUtils.getCleanJwtCookie();
    }

    private ResponseEntity<ErrorResponseDto> handleAuthenticationFailure(AuthenticationException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "/signin",
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    private UserInfoResponse createUserInfoResponse(UserDetailsImpl userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );
    }


}
