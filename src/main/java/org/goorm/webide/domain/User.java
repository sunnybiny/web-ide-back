package org.goorm.webide.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Builder
  public User(String name, String password, UserRole role){
    this.name = name;
    this.password = password;
    this.role = role;

  }

  public String getRoleKey(){
    return this.role.getKey();
  }

  @PrePersist
  public void createdAt() {
    this.createdAt = LocalDateTime.now();
  }
}