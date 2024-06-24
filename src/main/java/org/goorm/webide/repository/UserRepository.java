package org.goorm.webide.repository;

import java.util.Optional;
import org.goorm.webide.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

    @Transactional
    Optional<User> findByRefreshToken(String refreshToken);
}
