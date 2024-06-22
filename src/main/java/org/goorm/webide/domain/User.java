package org.goorm.webide.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<UserProject> userProjects = new ArrayList<>();

  private String name;

  private String password;

  private String email;

  private LocalDateTime createdAt;

  private Long profileImageId;

  @PrePersist
  public void createdAt() {
    this.createdAt = LocalDateTime.now();
  }
}