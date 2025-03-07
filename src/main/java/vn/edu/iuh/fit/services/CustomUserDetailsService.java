package vn.edu.iuh.fit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findUserByPhoneNumber(phoneNumber)
                .map(this::buildUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại với số điện thoại: " + phoneNumber));
    }

    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        return userRepository.findById(userId)
                .map(this::buildUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại với ID: " + userId));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .map(this::buildUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại với email: " + email));
    }

    private UserDetails buildUserDetails(vn.edu.iuh.fit.models.User user) {
        return User.builder()
                .username(user.getId().toString())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
