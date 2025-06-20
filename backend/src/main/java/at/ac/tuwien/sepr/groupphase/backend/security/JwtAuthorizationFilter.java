package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SecurityProperties securityProperties;
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;

    public JwtAuthorizationFilter(SecurityProperties securityProperties, JwtTokenizer jwtTokenizer, UserService userService) {
        this.securityProperties = securityProperties;
        this.jwtTokenizer = jwtTokenizer;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header != null && !header.startsWith("Basic ")) {
            try {
                UsernamePasswordAuthenticationToken authToken = getAuthToken(request);
                if (authToken != null) {
                    UserDetails userDetails = userService.loadUserByUsername(authToken.getName());

                    // Generate a new Bearer Token for the response
                    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
                    String newToken = jwtTokenizer.getAuthToken(authToken.getName(), roles);
                    UsernamePasswordAuthenticationToken newAuthToken = getAuthTokenFromString(newToken);
                    response.setHeader(securityProperties.getAuthHeader(), newToken);

                    SecurityContextHolder.getContext().setAuthentication(newAuthToken);
                }
            } catch (IllegalArgumentException | JwtException e) {
                LOG.debug("Invalid authorization attempt: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid authorization header or token");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public UsernamePasswordAuthenticationToken getAuthTokenFromString(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        if (!token.startsWith(securityProperties.getAuthTokenPrefix())) {
            throw new IllegalArgumentException("Authorization header is malformed or missing");
        }

        byte[] signingKey = securityProperties.getJwtSecret().getBytes();

        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token must start with 'Bearer'");
        }
        Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(signingKey)).build()
            .parseSignedClaims(token.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getPayload();

        String username = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .toList();

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Token contains no user");
        }

        MDC.put("u", username);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private UsernamePasswordAuthenticationToken getAuthToken(HttpServletRequest request)
        throws JwtException, IllegalArgumentException {
        String token = request.getHeader(securityProperties.getAuthHeader());
        return getAuthTokenFromString(token);
    }
}
