package za.co.pacifish.registration_service.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;
import za.co.pacifish.registration_service.dto.FirebaseUserDetailsDto;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseIdTokenFilter extends OncePerRequestFilter {

    private final JsonMapper objectMapper;
    private final FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("=== REGISTRATION FIREBASE FILTER ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Method: {}", request.getMethod());

        String authorizationHeader = request.getHeader("Authorization");
        log.info("Authorization header present: {}", authorizationHeader != null);

        // No token → continue (Spring Security will decide)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("No Bearer token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        log.info("Token extracted, length: {}", token.length());

        try {
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);
            log.info("Token verified successfully");
            log.info("User email: {}", firebaseToken.getEmail());
            log.info("User UID: {}", firebaseToken.getUid());

            FirebaseUserDetailsDto userDetails =
                    new FirebaseUserDetailsDto(
                            firebaseToken.getEmail(),
                            firebaseToken.getUid()
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            List.of()
                    );

            authentication.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Authentication set in SecurityContext");
            filterChain.doFilter(request, response);

        } catch (FirebaseAuthException ex) {
            log.error("Firebase token verification failed: {}", ex.getMessage());
            log.error("Error code: {}", ex.getAuthErrorCode());
            setAuthErrorDetails(response);
        }
    }

    private void setAuthErrorDetails(HttpServletResponse response) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                "Authentication failure: Token missing, invalid or expired"
        );

        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}