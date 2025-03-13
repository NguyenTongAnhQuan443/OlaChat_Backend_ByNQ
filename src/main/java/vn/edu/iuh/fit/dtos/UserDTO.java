package vn.edu.iuh.fit.dtos;

import lombok.*;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.enums.Role;
import vn.edu.iuh.fit.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String username;
    private String displayName;
    private String email;
    private String avatar;
    private String coverPhoto;
    private String phoneNumber;
    private AuthProvider authProvider;
    private String bio;
    private UserStatus status;
    private Role role;
    private LocalDateTime dob;
}
