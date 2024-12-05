package sk.stasko.ecomerce.auth;

import java.util.List;

public record UserInfoResponse(
       Long id, String jwtToken, String username, List<String> roles
) { }


