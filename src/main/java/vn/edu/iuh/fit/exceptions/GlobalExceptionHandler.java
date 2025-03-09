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
    private final ExceptionResponseFactory responseFactory;

    public GlobalExceptionHandler(ExceptionResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    // 400 Bad Request - Yêu cầu không hợp lệ
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex, Model model) {
        return responseFactory.create(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getData());
    }

    // 401 Unauthorized - Token thiếu, sai hoặc hết hạn
    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class, UnsupportedJwtException.class, MalformedJwtException.class})
    public ResponseEntity<ApiResponse<Object>> handleJwtExceptions(Exception ex) {
        return responseFactory.create(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn!", null);
    }

    // 403 Forbidden - Không có quyền truy cập
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return responseFactory.create(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập tài nguyên này!", null);
    }

    // 404 Not Found - Không tìm thấy tài nguyên
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UsernameNotFoundException ex) {
        return responseFactory.create(HttpStatus.NOT_FOUND, "Người dùng không tồn tại!", null);
    }

    // 500 Internal Server Error - Lỗi không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
        return responseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra trên máy chủ: " + ex.getMessage(), null);
    }
}
