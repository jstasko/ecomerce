package sk.stasko.ecomerce.user;

import java.util.List;

public record UserInfoResponse(
       Long id, String jwtToken, String username, List<String> roles
) {
    public UserInfoResponse(Long id, String username, List<String> roles) {
        this(id, null, username, roles);
    }
}


