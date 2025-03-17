package vn.edu.iuh.fit.seeders;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.enums.AuthProvider;
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
                    .username("Bảo Thông")
                    .displayName("Bảo Thông")
                    .email("baothong15082003@gmail.com")
                    .avatar("https://scontent.fsgn2-9.fna.fbcdn.net/v/t1.6435-9/51231801_125970705121677_7602358096949673984_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=a5f93a&_nc_ohc=eOkiPCSJOocQ7kNvgHLgcDV&_nc_oc=Adi4D6Jvq_8j3rIiiHDcswVkVUo5kJ5RA8pgj4FJ9YUGPfDJuw6OFVew7YNuo8FKor5hL_-JThK64JraSw2LNDRK&_nc_zt=23&_nc_ht=scontent.fsgn2-9.fna&_nc_gid=IbhDt0bU4HCXPnod77Dssw&oh=00_AYFJNP0YXSHIkl0KbsrhxJWzns79TCkU4WeEXW0S5nIx_Q&oe=67FF648D")
                    .coverPhoto("https://scontent.fsgn2-7.fna.fbcdn.net/v/t39.30808-6/471191493_1261778521540884_288494701577274627_n.jpg?_nc_cat=108&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=gITzWKiAMHwQ7kNvgE1cbvC&_nc_oc=Adh4m_FfmuiU7RqfkBrfXsYBRFDq4Xv4drAGkv8l1-se_0Ap4t5twR-mZBlIiI2JkrgsBW8wAxzCYrSdWantnUOi&_nc_zt=23&_nc_ht=scontent.fsgn2-7.fna&_nc_gid=IbhDt0bU4HCXPnod77Dssw&oh=00_AYH-MuI26WEcvjWjhdDXBRErZv4jyBlca8ERrNaaTSiJWA&oe=67DDBB62")
                    .bio("Software Engineer & AI Enthusiast")
                    .dob(LocalDateTime.of(1995, 5, 20, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .phoneNumber("1234567890")
                    .password(passwordEncoder.encode("1234567890"))
                    .authProvider(AuthProvider.LOCAL)
                    .sex(true)
                    .build();

            User user2 = User.builder()
                    .username("Lê Tấn Phát")
                    .displayName("Lê Tấn Phát")
                    .email("phat172003@gmail.com")
                    .avatar("https://scontent.fsgn2-6.fna.fbcdn.net/v/t39.30808-1/475344951_1687528955532241_2352397331145455260_n.jpg?stp=c7.0.705.705a_dst-jpg_s200x200_tt6&_nc_cat=111&ccb=1-7&_nc_sid=e99d92&_nc_ohc=l4CXaZcL2qUQ7kNvgES-bFW&_nc_oc=AdhY14q-ZuiaUGuKDALbwQ8wQL3zw5L1jpRjRt4cI4kWMqASuwXTVHSKiIiyCpw8gjX3zqBjNFMkDJLyp3XSj2Zj&_nc_zt=24&_nc_ht=scontent.fsgn2-6.fna&_nc_gid=VEhEYwJ5A9InUQ6oxeECYA&oh=00_AYEX49Ass5lRxxgrmXabwX1UiN32EyTaXBU0SM9s13khJg&oe=67DDCFEE")
                    .coverPhoto("https://scontent.fsgn2-8.fna.fbcdn.net/v/t39.30808-6/483472387_1717843609167442_2186663003666647125_n.jpg?_nc_cat=100&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=RC2Sr9YpucIQ7kNvgF64IHT&_nc_oc=AdgUOdfJA6Wa4nEJ3wbFzmU0HKtSqxNc5Byk-wxUEJJSd1EPDnarT742Wssz7UY6vplTkkQm6l1zT8XZmLf_Ty7l&_nc_zt=23&_nc_ht=scontent.fsgn2-8.fna&_nc_gid=xDCINn7Z72kJXCCukPYRUQ&oh=00_AYG9J3kmtZ2hZXQXG-TaNTCD9MT_mVSXyMImZyjDRdJI8w&oe=67DDD475")
                    .bio("Lover of books and coffee ☕📖")
                    .dob(LocalDateTime.of(1998, 8, 15, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .phoneNumber("1234567891")
                    .password(passwordEncoder.encode("1234567891"))
                    .authProvider(AuthProvider.LOCAL)
                    .sex(true)
                    .build();

            User user3 = User.builder()
                    .username("Thúy Vy")
                    .password(passwordEncoder.encode("1234567892"))
                    .displayName("Thúy Vy")
                    .email("tranvy.art@gmail.com")
                    .avatar("https://scontent.fsgn2-4.fna.fbcdn.net/v/t39.30808-1/465620127_122191532150211022_4962425294516127781_n.jpg?stp=dst-jpg_s200x200_tt6&_nc_cat=101&ccb=1-7&_nc_sid=e99d92&_nc_ohc=g7_Z1uiFwQMQ7kNvgGfTNQ4&_nc_oc=Adgc3r1BTGuvS3ZlpramnmKPL45OUjHm1jc_DvQ23GcZXhtG3A7h9NWd5wWPdpQ3b2Fcc6a9nqjbVVEWGIrlqRfi&_nc_zt=24&_nc_ht=scontent.fsgn2-4.fna&_nc_gid=YQEfQmmDSTDcX1y97TJ-dA&oh=00_AYGhtk2OTu3WFXbKCwd_AcyXSvX1cbkEa5lxAziHLhauXA&oe=67DDD7B4")
                    .coverPhoto("https://scontent.fsgn2-9.fna.fbcdn.net/v/t39.30808-6/472839876_122204993672211022_6470176401216928553_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=127cfc&_nc_ohc=dwfn7ujlI_YQ7kNvgGQFkNo&_nc_oc=AdiUDw4zO0Cdd4Md6LP0plU-RWBJRSMDrbI9WoqZUG0geKoDtru0uH6KFw1cF-NqzEi-hTC7imrrU9h4lK8CDWOJ&_nc_zt=23&_nc_ht=scontent.fsgn2-9.fna&_nc_gid=_3kX8Oj2NgZbE7J7dpbNyw&oh=00_AYEGo3CsKYu6eJ7J-xbIelxeztjDfnKJwpi3xoWCdVw3Lg&oe=67DDBF49")
                    .bio("System administrator")
                    .dob(LocalDateTime.of(1990, 1, 1, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .phoneNumber("1234567892")
                    .authProvider(AuthProvider.LOCAL)
                    .sex(false)
                    .build();

            User user4 = User.builder()
                    .username("Thanh Nhứt")
                    .password(passwordEncoder.encode("1234567893"))
                    .displayName("Thanh Nhứt")
                    .email("thanhnhutcu@gmail.com")
                    .avatar("https://scontent.fsgn2-7.fna.fbcdn.net/v/t1.6435-1/60564298_104100867490327_3051374517763964928_n.jpg?stp=dst-jpg_s200x200_tt6&_nc_cat=108&ccb=1-7&_nc_sid=e99d92&_nc_ohc=B2TxWEFLMHAQ7kNvgGByJx6&_nc_oc=AdiG1WuGGQq-3TLdl54n3uJE-YZJ3jhanWukqoHlvdBYMmolZ33BldZvWMkxV_JLlfCmI2vgjk3bfsuOSudoHD5n&_nc_zt=24&_nc_ht=scontent.fsgn2-7.fna&_nc_gid=Z4bA-ND_qM89vh9PlaAsNg&oh=00_AYEWjatqTx8LloUplb_zBYH7CdUgFB7K49DJILRLJgNYUw&oe=67FF62BA")
                    .coverPhoto("https://scontent.fsgn2-6.fna.fbcdn.net/v/t1.6435-9/87390080_199672424599837_9146923517361520640_n.jpg?_nc_cat=110&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=pa21MnfFt4cQ7kNvgGH1v_5&_nc_oc=Adg6HoQeiYnFrBSYeT3Y272DwRLkt99wR7eoGVIkTznLlgAYj41VQdz5u2Cs4VMgP6Y_DH1S4slB9Vn-YOMfofUF&_nc_zt=23&_nc_ht=scontent.fsgn2-6.fna&_nc_gid=Z4bA-ND_qM89vh9PlaAsNg&oh=00_AYFPsqkvaRtk6ZYzh4WURi57EK_z_6AlezfuQjPCLCEeMw&oe=67FF662D")
                    .bio("System administrator")
                    .dob(LocalDateTime.of(1990, 1, 1, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .phoneNumber("1234567893")
                    .authProvider(AuthProvider.LOCAL)
                    .sex(true)
                    .build();

            User user5 = User.builder()
                    .username("Nguyễn Quân")
                    .password(passwordEncoder.encode("1234567894"))
                    .displayName("Nguyễn Quân")
                    .email("ntanhquan.sly@gmail.com")
                    .avatar("https://scontent.fsgn2-9.fna.fbcdn.net/v/t39.30808-6/470178018_963334848994533_4665347559148070661_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=1hiCy_RcNSMQ7kNvgHAmOea&_nc_oc=AdjQLZglSIpAL16ACR12a6IIkJWjI397-KNek1EBgbnFunSRbF-osXqrF8-rAl7Srq0TLhU_ZPhyczV6uKnJGUA-&_nc_zt=23&_nc_ht=scontent.fsgn2-9.fna&_nc_gid=4h8oWnhLK250iIa8HcdIiQ&oh=00_AYFT6eij1Eyg8MCK0nvohI29eTg42AuyYdQJG8m17qOfTg&oe=67DDD424")
                    .coverPhoto("https://scontent.fsgn2-6.fna.fbcdn.net/v/t1.6435-9/87390080_199672424599837_9146923517361520640_n.jpg?_nc_cat=110&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=pa21MnfFt4cQ7kNvgGH1v_5&_nc_oc=Adg6HoQeiYnFrBSYeT3Y272DwRLkt99wR7eoGVIkTznLlgAYj41VQdz5u2Cs4VMgP6Y_DH1S4slB9Vn-YOMfofUF&_nc_zt=23&_nc_ht=scontent.fsgn2-6.fna&_nc_gid=Z4bA-ND_qM89vh9PlaAsNg&oh=00_AYFPsqkvaRtk6ZYzh4WURi57EK_z_6AlezfuQjPCLCEeMw&oe=67FF662D")
                    .bio("System administrator")
                    .dob(LocalDateTime.of(1990, 1, 1, 0, 0))
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .phoneNumber("1234567894")
                    .authProvider(AuthProvider.LOCAL)
                    .build();

            userRepository.saveAll(List.of(user1, user2, user3, user4, user5));
            System.out.println("✅ Users seeded successfully!");
        } else {
            System.out.println("✅ Users already exist. Skipping seeding.");
        }

    }
}
