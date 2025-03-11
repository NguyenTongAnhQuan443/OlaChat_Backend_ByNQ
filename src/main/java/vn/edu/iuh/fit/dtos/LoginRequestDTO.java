package vn.edu.iuh.fit.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "idToken không được để trống")
    private String idToken;

    @NotBlank(message = "deviceId không được để trống")
    private String deviceId;
}
