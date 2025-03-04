package vn.edu.iuh.fit.seeders;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.enums.Role;
import vn.edu.iuh.fit.enums.UserStatus;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user1 = User.builder()
                    .username("john_doe")
                    .password(passwordEncoder.encode("password123"))
                    .displayName("John Doe")
                    .email("john.doe@example.com")
                    .avatar("https://i.pravatar.cc/150?img=1")
                    .coverPhoto("https://source.unsplash.com/random/800x600")
                    .bio("Software Engineer & AI Enthusiast")
                    .dob(LocalDateTime.of(1995, 5, 20, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .build();

            User user2 = User.builder()
                    .username("jane_smith")
                    .password(passwordEncoder.encode("password123"))
                    .displayName("Jane Smith")
                    .email("jane.smith@example.com")
                    .avatar("https://i.pravatar.cc/150?img=2")
                    .coverPhoto("https://source.unsplash.com/random/800x601")
                    .bio("Lover of books and coffee ☕📖")
                    .dob(LocalDateTime.of(1998, 8, 15, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .build();

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .displayName("Admin User")
                    .email("admin@example.com")
                    .avatar("https://i.pravatar.cc/150?img=3")
                    .coverPhoto("https://source.unsplash.com/random/800x602")
                    .bio("System administrator")
                    .dob(LocalDateTime.of(1990, 1, 1, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.ADMIN)
                    .build();

            userRepository.saveAll(List.of(user1, user2, admin));
            System.out.println("✅ Users seeded successfully!");
        } else {
            System.out.println("✅ Users already exist. Skipping seeding.");
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; // Tạo khóa 256-bit
        secureRandom.nextBytes(key);
        String secretKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated Secret Key: " + secretKey);
    }
}
