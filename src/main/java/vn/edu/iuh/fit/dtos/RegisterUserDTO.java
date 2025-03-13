package vn.edu.iuh.fit.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserDTO {
    private String phoneNumber;
    private String displayName;
    private String password;
}
