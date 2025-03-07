package vn.edu.iuh.fit.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.iuh.fit.utils.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request - Yêu cầu không hợp lệ
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex, Model model) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ex.getCode());
        response.setMessage(ex.getMessage());
        response.setData(ex.getData());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 401 Unauthorized - Token thiếu, sai hoặc hết hạn
    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class, UnsupportedJwtException.class, MalformedJwtException.class})
    public ResponseEntity<ApiResponse<Object>> handleJwtExceptions(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Token không hợp lệ hoặc đã hết hạn!");
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 403 Forbidden - Không có quyền truy cập
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(HttpStatus.FORBIDDEN.value());
        response.setMessage("Bạn không có quyền truy cập tài nguyên này!");
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 500 Internal Server Error - Lỗi không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("Có lỗi xảy ra trên máy chủ: " + ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
