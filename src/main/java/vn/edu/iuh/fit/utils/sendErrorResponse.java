package vn.edu.iuh.fit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class sendErrorResponse {
    public static void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF8");
        response.setStatus(status);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", status);
        errorResponse.put("message", message);
        errorResponse.put("data", null);

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
