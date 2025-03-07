package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.User;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findUserByPhoneNumber(phoneNumber);
//
//        if (user.isEmpty()) {
//            throw new UsernameNotFoundException("User không tồn tại với số điện thoại: " + phoneNumber);
//        }
//
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.get().getPhoneNumber())
//                .password(user.get().getPassword())
//                .roles(user.get().getRole().name())
//                .build();
//    }
@Override
public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findUserByPhoneNumber(identifier);
    if (user.isEmpty()) {
        user = userRepository.findUserByEmail(identifier); // Tìm bằng email nếu không thấy phoneNumber
    }
    if (user.isEmpty()) {
        throw new UsernameNotFoundException("User không tồn tại với số điện thoại hoặc email: " + identifier);
    }

    return org.springframework.security.core.userdetails.User.builder()
            .username(user.get().getEmail() != null ? user.get().getEmail() : user.get().getPhoneNumber())
            .password(user.get().getPassword() != null ? user.get().getPassword() : "")
            .roles(user.get().getRole().name())
            .build();
}

}