package org.goorm.webide.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@NoArgsConstructor
//@RequiredArgsConstructor
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "project_id")
  private Long id;

  private String name;

  private String description;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  private Container container;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Column(insertable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User createdBy;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.REMOVE)
  private List<UserProject> userProjects = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    updatedAt = null;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  private Project(String name, Container container, User createdBy) {
    this.name = name;
    this.container = container;
    this.createdBy = createdBy;
  }

  public static Project createProject(String name, Container container, User createdBy) {
    return new Project(name, container, createdBy);
  }
}