package org.goorm.webide.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.goorm.webide.entity.Members;

public interface UserRepository extends JpaRepository<Members, Long> {

    Optional<Members> findByusername(String username);
}
