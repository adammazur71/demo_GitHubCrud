package com.example.demo.repository;

import com.example.demo.repository.client.dto.UserProjectsDataDto;
import com.example.demo.repository.dto.ProjectInfoDto;
import com.example.demo.repository.dto.ProjectRequestDto;
import com.example.demo.repository.dto.RepositoryResponseDto;
import com.example.demo.repository.exceptions.IdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RepositoryController {

    RepositoryService repositoryService;
    List<ProjectInfoDto> cache = new ArrayList<>();

    @Autowired

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;

    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<RepositoryResponseDto> hello(@PathVariable String username) {
        if (!cache.isEmpty()) {
            return ResponseEntity.ok(new RepositoryResponseDto(cache));
        }
        List<ProjectInfoDto> projectInfoDtos = repositoryService.projectInfoDtos(username);
        cache = projectInfoDtos;
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos));
    }

    @GetMapping(value = "/save2db/{username}", produces = "application/json")
    public ResponseEntity<List<RepositoryEntity>> saveRepos2db(@PathVariable String username) {
        List<UserProjectsDataDto> projectInfoDtos = repositoryService.makeGitHubRequestForUserProjects(username);
        List<RepositoryEntity> savedRequests = repositoryService.saveProjectInfo2DB(projectInfoDtos);
        return ResponseEntity.ok(savedRequests);
    }


    @GetMapping(value = "/repos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RepositoryEntity>> showRepos() {
        List<RepositoryEntity> all = repositoryService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping(value = "/repos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoryEntity> showById(@PathVariable Long id) throws IdNotFoundException {
        RepositoryEntity resultById = repositoryService.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Result with id " + id + " not found"));
        return ResponseEntity.ok(resultById);
    }

    @PostMapping(value = "/repos")
    public ResponseEntity<RepositoryEntity> saveProject(@RequestBody ProjectRequestDto request) {
        RepositoryEntity savedProject = repositoryService.save(new RepositoryEntity(request.owner(), request.name()));
        return ResponseEntity.ok(savedProject);
    }

}
