package vibhuvi.oio.inframirror.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AGENT_PATH_PREFIX = "/api/agent/";

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        
        if (!request.getRequestURI().startsWith(AGENT_PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            LOG.warn("Missing API key for agent endpoint: {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API Key required");
            return;
        }

        try {
            String hashedKey = hashApiKey(apiKey);
            apiKeyRepository.findFirstByKeyHashAndActiveTrue(hashedKey)
                .ifPresentOrElse(
                    key -> authenticateAgent(key),
                    () -> LOG.warn("Invalid API key attempt for: {}", request.getRequestURI())
                );

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }
        } catch (Exception e) {
            LOG.error("Error during API key authentication", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateAgent(ApiKey apiKey) {
        var authentication = new UsernamePasswordAuthenticationToken(
            apiKey.getName(),
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_AGENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String hashApiKey(String plainTextKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainTextKey.getBytes(StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash API key", e);
        }
    }
}
