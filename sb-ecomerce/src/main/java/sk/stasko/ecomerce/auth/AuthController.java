package sk.stasko.ecomerce.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sk.stasko.ecomerce.common.dto.ResponseDto;
import sk.stasko.ecomerce.common.security.service.UserDetailsImpl;
import sk.stasko.ecomerce.user.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticationOfUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticationOfUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        userService.createUserEntity(signupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("User registered successfully"));
    }

    @GetMapping("/username")
    public ResponseEntity<String> currentUserName(Authentication authentication) {
        return authentication != null
                ? ResponseEntity.ok(authentication.getName())
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserInfoResponse> getUserDetailsInfo(Authentication authentication) {
        var userDetails = this.authService.getUserInfoResponseFromAuthentication(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        var cookie = this.authService.clearCookie();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ResponseDto("Signed out successfully"));
    }
}
