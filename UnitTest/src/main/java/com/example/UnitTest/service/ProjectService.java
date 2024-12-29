package com.example.UnitTest.service;

import com.example.UnitTest.dao.ProjectDAO;
import com.example.UnitTest.entity.Project;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final GenerateUnitTestService generateUnitTestService;
    @Value("${app.clone.dir}")
    private String BASE_CLONE_DIR;

    @Autowired
    public ProjectService(ProjectDAO projectDAO, GenerateUnitTestService generateUnitTestService) {
        this.projectDAO = projectDAO;
        this.generateUnitTestService = generateUnitTestService;
    }

    @PostConstruct
    public void init() {
        File baseDir = new File(BASE_CLONE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdir();
            log.info("Created base directory for cloned repositories: {}", BASE_CLONE_DIR);
        }
    }

    private String extractRepoName(String githubUrl) {

        String url = githubUrl.endsWith(".git")
                ? githubUrl.substring(0, githubUrl.length() - 4)
                : githubUrl;

        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private List<Path> findJavaFiles(String repoPath) throws IOException {
        Path sourcePath = Paths.get(repoPath, "src");
        if (!Files.exists(sourcePath)) {
            log.warn("No src directory found in {}", repoPath);
            return Collections.emptyList();
        }

        log.info("Searching for Java files in: {}", sourcePath);
        List<Path> javaFiles = new ArrayList<>();

        // First collect all files for debugging
        List<Path> allFiles = Files.walk(sourcePath)
                .filter(path -> path.toString().endsWith(".java"))
                .collect(Collectors.toList());

        log.info("Found {} total Java files before filtering", allFiles.size());

        // Now apply filters one by one
        javaFiles = allFiles.stream()
                .filter(path -> {
                    boolean isMain = path.toString().contains("/main/");
                    if (!isMain) {
                        log.info("Skipping non-main file: {}", path);
                    }
                    return isMain;
                })
                .filter(path -> {
                    boolean notApplication = !path.getFileName().toString().contains("Application");
                    if (!notApplication) {
                        log.info("Skipping Application file: {}", path);
                    }
                    return notApplication;
                })
                .peek(path -> log.info("Including file: {}", path))
                .collect(Collectors.toList());

        log.info("Final Java files to process: {}", javaFiles.size());
        return javaFiles;
    }

    private String constructGithubUrl(String githubUrl, String repoPath, Path javaFile) {
        if (githubUrl == null || repoPath == null || javaFile == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }
        githubUrl = githubUrl.endsWith(".git") ? githubUrl.substring(0, githubUrl.length() - 4) : githubUrl;
        Path relativePath = Paths.get(repoPath).relativize(javaFile);
        String pathInRepo = relativePath.toString().replace('\\', '/');
        return String.format("%s/blob/main/%s", githubUrl, pathInRepo).replace("//blob", "/blob");
    }

    private void processJavaFile(String githubUrl, String repoPath, Path javaFile) {
        try {
            String fileUrl = constructGithubUrl(githubUrl, repoPath, javaFile);
            log.info("Processing Java file: {}", fileUrl);

            String testCode = generateUnitTestService.generateResponse(fileUrl);
            log.info("Received test code response, length: {}", testCode != null ? testCode.length() : 0);

            if (testCode != null && !testCode.isEmpty()) {
                log.info("Generating test file for: {}", javaFile.getFileName());
                generateTestFile(javaFile, testCode);
                log.info("Test file generation completed");
            } else {
                log.warn("No test code generated for: {}", javaFile.getFileName());
            }

        } catch (Exception e) {
            log.error("Error processing file {}: {}", javaFile, e.getMessage());
        }
    }

    private void generateTestFile(Path sourceFile, String testCode) throws IOException {
        String originalName = sourceFile.getFileName().toString();
        String testFileName = originalName.replace(".java", "Test.java");

        Path srcDir = sourceFile;
        while (srcDir != null && !srcDir.getFileName().toString().equals("src")) {
            srcDir = srcDir.getParent();
        }

        if (srcDir == null) {
            throw new IOException("Could not find 'src' directory");
        }

        Path mainJavaPath = srcDir.resolve("main").resolve("java");
        Path packagePath = mainJavaPath.relativize(sourceFile.getParent());

        Path testDir = srcDir.resolve("test").resolve("java").resolve(packagePath);

        Files.createDirectories(testDir);

        Path testFile = testDir.resolve(testFileName);
        Files.write(testFile, testCode.getBytes());
        log.info("Generated test file at: {}", testFile);
    }

    @Transactional
    public Project cloneAndSaveRepository(String githubUrl) {

        try {
            String repoName = extractRepoName(githubUrl);
            String clonePath = BASE_CLONE_DIR + File.separator + repoName;
            File directory = new File(clonePath);
            if (directory.exists()) {
                FileUtils.deleteDirectory(directory);
            }

            Git.cloneRepository()
                    .setURI(githubUrl)
                    .setDirectory(new File(clonePath))
                    .call();

            Project project = new Project();
            project.setRepoName(repoName);
            project.setGithubUrl(githubUrl);
            project.setLocalPath(clonePath);
            project.setClonedAt(LocalDateTime.now());

            Project savedProject = projectDAO.save(project);
            log.info("Repository cloned and saved successfully: {}", repoName);

            List<Path> javaFiles = findJavaFiles(clonePath);
            log.info("Found {} Java files to process", javaFiles.size());

            // Using CompletableFuture to process files asynchronously but wait for all to complete
            List<CompletableFuture<Void>> futures = javaFiles.stream()
                    .map(javaFile -> CompletableFuture.runAsync(() -> {
                        try {
                            processJavaFile(githubUrl, clonePath, javaFile);
                        } catch (Exception e) {
                            log.error("Error processing file {}: {}", javaFile, e.getMessage());
                        }
                    }))
                    .collect(Collectors.toList());

            // Wait for all files to be processed
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("All files processed successfully");

            return project;

        } catch (GitAPIException e) {
            log.error("Failed to clone repository: {}", e.getMessage());
            throw new RuntimeException("Failed to clone repository", e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Project findById(Long id) {
        return projectDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Repository not found"));
    }

    public List<Project> findByRepoName(String repoName) {
        return projectDAO.findByRepoName(repoName);
    }

    public Optional<Project> findByGithubUrl(String githubUrl) {
        return projectDAO.findByGithubUrl(githubUrl);
    }

    public List<Project> findAllRepositories() {
        return projectDAO.findAll();
    }

    public void deleteRepository(Long id) {
        Project project = projectDAO.findById(id).orElseThrow();

        try {
            FileUtils.deleteDirectory(new File(project.getLocalPath()));
        } catch (IOException e) {
            log.error("Failed to delete local repository files: {}", e.getMessage());
        }

        projectDAO.deleteById(id);
        log.info("Repository deleted: {}", project.getRepoName());
    }
}
