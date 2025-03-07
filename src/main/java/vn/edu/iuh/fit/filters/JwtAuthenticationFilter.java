package vn.edu.iuh.fit.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.iuh.fit.constants.AuthConstants;
import vn.edu.iuh.fit.services.CustomUserDetailsService;
import vn.edu.iuh.fit.services.TokenBlacklistService;
import vn.edu.iuh.fit.utils.JwtUtil;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static vn.edu.iuh.fit.utils.sendErrorResponse.sendErrorResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);
        // Kiểm tra xem token có bị blacklist không
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, AuthConstants.MESSAGE_TOKEN_HAS_BEEN_DISABLED);
            return;
        }

        try {
            final String phoneNumber = jwtUtil.extractPhoneNumber(token);
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(phoneNumber);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, AuthConstants.MESSAGE_TOKEN_IS_INVALID);
        } catch (MalformedJwtException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, AuthConstants.MESSAGE_INVALID_ACCESS_TOKEN);
        } catch (SignatureException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, AuthConstants.MESSAGE_INVALID_SIGNATURE);
        } catch (UsernameNotFoundException e) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, AuthConstants.MESSAGE_USER_NOT_FOUND);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JwtAuthenticationFilter Lỗi xử lý xác thực");
        }

    }
}
