package com.example.demo.repository;

import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.dto.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.dto.UserProjectsDataDto;
import com.example.demo.repository.dto.BranchDto;
import com.example.demo.repository.dto.BranchWithRepoNameDto;
import com.example.demo.repository.dto.ProjectInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class RepositoryService {

    private final GitHubProxy gitHubProxy;
    private final GitHubRepository gitHubRepository;

    public RepositoryService(GitHubProxy gitHubProxy, GitHubRepository gitHubRepository) {
        this.gitHubProxy = gitHubProxy;
        this.gitHubRepository = gitHubRepository;
    }

    public List<RepositoryEntity> findAll() {
        return gitHubRepository.findAll();

    }

    public Optional<RepositoryEntity> findById(Long id) {
        return gitHubRepository.findById(id);
    }

    public RepositoryEntity save(RepositoryEntity entity) {
        return gitHubRepository.save(entity);
    }

    public List<RepositoryEntity> saveAll(List<RepositoryEntity> entities) {
        return gitHubRepository.saveAll(entities);
    }

    public List<ProjectInfoDto> downloadNoForkProjectInfoDtos(String username) {
        List<UserProjectsDataDto> response = makeGitHubRequestForUserProjects(username);
        return generateNoForkProjectInfoDtos(response, username);
    }

    public List<UserProjectsDataDto> makeGitHubRequestForUserProjects(String userName) {
        return gitHubProxy.downloadUsersRepos(userName);
    }

    public List<RepositoryEntity> saveProjects2db(List<UserProjectsDataDto> userProjectsData) {
        List<RepositoryEntity> projects2save = generateRepositoryEntityList(userProjectsData);
        return saveAll(projects2save);
    }

    public List<RepositoryEntity> saveProjectsWithBranchInfo2db(List<ProjectInfoDto> projectInfoDtos) {
        List<RepositoryEntity> projects2save = generateRepositoryEntityListWithBranchesInfo(projectInfoDtos);
        return saveAll(projects2save);
    }


    private BranchWithRepoNameDto retrieveBranchesForProject(String userName, String projectName) {
        List<GitHubBranchInfoResponseDto> branchInfoResponse = gitHubProxy.downloadUsersReposBranchesInfo(userName, projectName);
        List<BranchDto> branchDtos = branchInfoResponse.stream()
                .map(branchInfo -> new BranchDto(branchInfo.name(), branchInfo.commit().sha()))
                .toList();

        return new BranchWithRepoNameDto(branchDtos, projectName);
    }

    private List<ProjectInfoDto> generateNoForkProjectInfoDtos(List<UserProjectsDataDto> userProjectsData, String username) {
        return userProjectsData.stream()
                .filter(projectsData -> !projectsData.fork())
                .map(projectsData -> retrieveBranchesForProject(username, projectsData.name()))
                .map(branchWithRepoNameDto -> new ProjectInfoDto(branchWithRepoNameDto.repoName(), username, branchWithRepoNameDto.branchesDto()))
                .collect(Collectors.toList());
    }

    private List<RepositoryEntity> generateRepositoryEntityList(List<UserProjectsDataDto> userProjectsData) {
        return userProjectsData.stream()
                .map(s -> new RepositoryEntity(s.owner().login(), s.name()))
                .collect(Collectors.toList());
    }

    private List<RepositoryEntity> generateRepositoryEntityListWithBranchesInfo(List<ProjectInfoDto> projectInfoDtos) {
        return projectInfoDtos.stream()
                .map(s -> new RepositoryEntity(s.ownerLogin(), s.repoName(), s.branchDto().stream()
                        .map(d -> new BranchInfoEntity(d.branchName(), d.sha()))
                        .collect(Collectors.toSet())))
                .toList();
    }

}
