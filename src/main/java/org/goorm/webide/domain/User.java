package org.goorm.webide.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@NoArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<UserProject> userProjects = new ArrayList<>();

  private String name;

  private String password;

  private String email;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  private Long profileImageId;


  private String refreshToken;

  private boolean isRefreshTokenExpired = false;

  public boolean getIsRefreshTokenExpired() {
    return isRefreshTokenExpired;
  }
  public void setIsRefreshTokenExpired(boolean refreshTokenExpired){
    isRefreshTokenExpired = refreshTokenExpired;
  }

  @Enumerated(EnumType.STRING)
  private ProjectRole projectRole;

  @Enumerated(EnumType.STRING)
  private UserRole userRole;

  @PrePersist
  public void createdAt() {
    this.createdAt = LocalDateTime.now();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority(userRole.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}