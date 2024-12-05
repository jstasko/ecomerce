package sk.stasko.ecomerce.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.stasko.ecomerce.common.dto.ResponseDto;
import sk.stasko.ecomerce.user.UserService;


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
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("User registered successfully"));
    }
}
