package vn.edu.iuh.fit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.iuh.fit.utils.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex, Model model) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(ex.getCode());
        response.setMessage(ex.getMessage());
        response.setData(ex.getData());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
