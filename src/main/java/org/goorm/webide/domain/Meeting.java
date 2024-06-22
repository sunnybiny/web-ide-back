package org.goorm.webide.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Meeting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "meeting_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  @OneToMany(mappedBy = "meeting")
  private List<ChatMessage> chatMessages = new ArrayList<>();

  private String title;

  private String description;

  private LocalDateTime createdAt;

  private LocalDateTime endedAt;

  public Meeting(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public Boolean isEnded() {
    return endedAt != null;
  }

  @PrePersist
  public void createdAt() {
    this.createdAt = LocalDateTime.now();
  }

}