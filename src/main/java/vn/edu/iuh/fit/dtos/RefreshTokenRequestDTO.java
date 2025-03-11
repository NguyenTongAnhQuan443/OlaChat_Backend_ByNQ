package vn.edu.iuh.fit.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO {
    @NotBlank(message = "Device ID không được để trống")
    private String deviceId;
}
