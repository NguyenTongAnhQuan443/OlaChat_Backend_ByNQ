package vn.edu.iuh.fit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.utils.ApiResponse;

@Component
public class ExceptionResponseFactory {
    public ResponseEntity<ApiResponse<Object>> create(HttpStatus status, String message, Object data) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(status.value());
        response.setMessage(message);
        response.setData(data);
        return new ResponseEntity<>(response, status);
    }
}
