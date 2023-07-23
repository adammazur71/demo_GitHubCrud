package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    GitHubRepository gitHubRepository;

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<RepositoryResponseDto> hello(@PathVariable String username) {
        if (!cache.isEmpty()) {
            return ResponseEntity.ok(new RepositoryResponseDto(cache));
        }
        List<ProjectInfoDto> projectInfoDtos = repositoryService.projectInfoDtos(username);
        cache = projectInfoDtos;
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos));
    }
    @GetMapping(value = "/repos", produces = MediaType.APPLICATION_JSON_VALUE)
    public void showRepos(){
        List<RepositoryEntity> all = repositoryService.findAll();
        System.out.println(all);
    }

}
