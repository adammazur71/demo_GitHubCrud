package com.example.demo.repository;

import com.example.demo.repository.client.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.UserProjectsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class RepositoryController {

    private final GitHubProxy gitHubProxy;

    @Autowired
    public RepositoryController(GitHubProxy gitHubProxy) {
        this.gitHubProxy = gitHubProxy;
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<RepositoryResponseDto> hello(@PathVariable String username) {
        List<UserProjectsData> response = makeGitHubRequestForUserProjects(username);
        List<ProjectInfoDto> projectInfoDtos = generateProjectInfoDtos(response, username);
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos));
    }

    private List<UserProjectsData> makeGitHubRequestForUserProjects(String userName) {
        return gitHubProxy.downloadUsersRepos(userName);
    }

    private BranchWithRepoNameDto retrieveBranchesForProject(String userName, String projectName) {
        List<GitHubBranchInfoResponseDto> branchInfoResponse = gitHubProxy.downloadUsersReposBranchesInfo(userName, projectName);
        List<BranchDto> branchDtos = branchInfoResponse.stream().map(branchInfo -> new BranchDto(branchInfo.name(), branchInfo.commit().sha())).toList();

        return new BranchWithRepoNameDto(branchDtos, projectName);
    }

    private List<ProjectInfoDto> generateProjectInfoDtos(List<UserProjectsData> userProjectsData, String username) {
        return userProjectsData.stream().filter(projectsData -> !projectsData.fork())
                .map(projectsData -> retrieveBranchesForProject(username, projectsData.name()))
                .map(branchWithRepoNameDto -> new ProjectInfoDto(branchWithRepoNameDto.repoName(), username, branchWithRepoNameDto.branchesDto()))
                .collect(Collectors.toList());
    }
}
