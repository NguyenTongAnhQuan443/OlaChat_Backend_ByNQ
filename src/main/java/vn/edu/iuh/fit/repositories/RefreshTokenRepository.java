package vn.edu.iuh.fit.repositories;

import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.models.RefreshToken;
import vn.edu.iuh.fit.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId);

    void deleteByUser(User user);

    void deleteByUserAndDeviceId(User user, String deviceId);
}
