package vn.edu.iuh.fit.models;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.enums.AuthProvider;
import vn.edu.iuh.fit.enums.Role;
import vn.edu.iuh.fit.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String username;
    private String password;
    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String displayName;
    private String email;
    @Column(length = 1000)
    private String avatar;
    @Column(length = 1000)
    private String coverPhoto;
    private boolean sex;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; // LOCAL, GOOGLE, FACEBOOK

    @Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String bio;

    private LocalDateTime dob;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void prePersist() {
        super.prePersist();
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }
}
