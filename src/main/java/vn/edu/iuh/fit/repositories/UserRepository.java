package vn.edu.iuh.fit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.models.User;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
