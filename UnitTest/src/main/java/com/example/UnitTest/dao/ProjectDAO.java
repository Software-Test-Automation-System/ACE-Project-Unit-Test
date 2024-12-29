package com.example.UnitTest.dao;

import com.example.UnitTest.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectDAO extends JpaRepository<Project,Long> {
    Optional<Project> findByGithubUrl(String githubUrl);
    List<Project> findByRepoName(String repoName);
    List<Project> findByUserEmail(String email);
}
