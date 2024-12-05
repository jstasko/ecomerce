package sk.stasko.ecomerce.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.app")
@Setter @Getter
public class JwtProperties {
    private String jwtSecret;
    private int jwtExpirationMs;
}
