package org.goorm.webide.repository;

import java.util.List;
import java.util.Optional;
import org.goorm.webide.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.userProjects up WHERE up.project.id = :projectId")
    List<User> findAllByProjectId(Long projectId);
}
