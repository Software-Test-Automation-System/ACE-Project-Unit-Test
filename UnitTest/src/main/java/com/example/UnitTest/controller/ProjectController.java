package com.example.UnitTest.controller;

import com.example.UnitTest.dto.CloneRequestDTO;
import com.example.UnitTest.entity.Project;
import com.example.UnitTest.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/clone")
    public ResponseEntity<Project> cloneRepository(@RequestBody CloneRequestDTO cloneRequestDTO) {
        try {
            Project project = projectService.cloneAndSaveRepository(cloneRequestDTO.getGithubURL());
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            log.error("Error cloning repository: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.findById(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{repoName}")
    public ResponseEntity<List<Project>> getProjectsByName(@PathVariable String repoName) {
        List<Project> projects = projectService.findByRepoName(repoName);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/url")  // Simplified the path
    public ResponseEntity<Project> getProjectByUrl(@RequestParam String githubUrl) {
        Optional<Project> project = projectService.findByGithubUrl(githubUrl);
        return project.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.findAllRepositories();
        return ResponseEntity.ok(projects);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteRepository(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
