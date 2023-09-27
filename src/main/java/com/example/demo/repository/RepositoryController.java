package com.example.demo.repository;

import com.example.demo.repository.client.dto.UserProjectsDataDto;
import com.example.demo.repository.dto.*;
import com.example.demo.repository.exceptions.IdNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.repository.RepositoryMapper.mapFromProjectRequestDtoToRepositoryEntity;

@RestController
@Log4j2
public class RepositoryController {

    RepositoryService repositoryService;

    @Autowired

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;

    }

    @GetMapping(value = "/no-forks/{username}", produces = "application/json")
    public ResponseEntity<RepositoryResponseDto> showUserNoForksReposWithBranchesInfo(@PathVariable String username) {
        List<ProjectInfoDto> projectInfoDtos = repositoryService.downloadNoForkProjectsWithBranchesInfoDtos(username);
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos));
    }

    @GetMapping(value = "/save-no-forks-with-branches-info/{username}", produces = "application/json")
    public ResponseEntity<List<RepositoryEntity>> saveProjectsWithBranchesInfo2db(@PathVariable String username) {
        log.info("saved " + username + "'s no forks with branches to DB");
        List<ProjectInfoDto> projectInfoDtos = repositoryService.downloadNoForkProjectsWithBranchesInfoDtos(username);
        List<RepositoryEntity> savedRequest = repositoryService.saveProjectsWithBranchInfo2db(projectInfoDtos);
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping(value = "/save-no-forks/{username}", produces = "application/json")
    public ResponseEntity<List<RepositoryEntity>> saveRepos2db(@PathVariable String username) {
        log.info("saved " + username + "'s no forks to DB");
        List<UserProjectsDataDto> projectInfoDtos = repositoryService.downloadNoForkProjects(username);
        List<RepositoryEntity> savedRequests = repositoryService.saveProjects2db(projectInfoDtos);
        return ResponseEntity.ok(savedRequests);
    }


    @GetMapping(value = "/repos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RepositoryEntity>> showRepos() {
        List<RepositoryEntity> all = repositoryService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping(value = "/repos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoryResponseByIdDto> showById(@PathVariable Long id) throws IdNotFoundException {
        RepositoryEntity resultById = repositoryService.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Result with id " + id + " not found"));
        RepositoryResponseByIdDto response = new RepositoryResponseByIdDto(resultById.getId(), resultById.getOwner(), resultById.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/repos")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RepositoryPostRequestDto> saveProject(@RequestBody @Valid ProjectRequestDto request) {
//       RepositoryEntity repositoryEntity = mapFromProjectRequestDtoToRepositoryEntity(request);
        RepositoryEntity savedProject = repositoryService.save(mapFromProjectRequestDtoToRepositoryEntity(request));
        RepositoryPostRequestDto postRequest = new RepositoryPostRequestDto(savedProject.getId(), savedProject.owner, savedProject.name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postRequest);
    }

}
