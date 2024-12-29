package com.example.UnitTest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String repoName;

    @Column(nullable = true)
    private String userEmail;

    @Column(nullable = false)
    private String githubUrl;

    @Column(nullable = false)
    private String localPath;

    @Column(nullable = false)
    private LocalDateTime clonedAt;
}
