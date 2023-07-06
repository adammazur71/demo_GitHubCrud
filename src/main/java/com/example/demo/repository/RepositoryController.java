package com.example.demo.repository;

import com.example.demo.repository.client.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.UserProjectsData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


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
        List<UserProjectsData> responseWithNoForks = removeForks(response);
        List<ProjectInfoDto> projectInfoDtos = generateProjectInfoDtos(responseWithNoForks, username);
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos));
    }

    private List<UserProjectsData> removeForks(List<UserProjectsData> userProjectsData) {
        userProjectsData.removeIf(UserProjectsData::fork);
        return userProjectsData;
    }

    private List<UserProjectsData> makeGitHubRequestForUserProjects(String userName) {
        return gitHubProxy.downloadUsersRepos(userName);
    }

    private BranchWithRepoNameDto retrieveBranchesForProject(String userName, String projectName) {
        List<GitHubBranchInfoResponseDto> branchInfoResponse = gitHubProxy.downloadUsersReposBranchesInfo(userName, projectName);
        List<BranchDto> branchesDto = new ArrayList<>();
        for (GitHubBranchInfoResponseDto gitHubBranchInfoResponseDto : branchInfoResponse) {
            branchesDto.add(new BranchDto(gitHubBranchInfoResponseDto.name(), gitHubBranchInfoResponseDto.commit().sha()));
        }
        return new BranchWithRepoNameDto(branchesDto, projectName);
    }

    private List<ProjectInfoDto> generateProjectInfoDtos(List<UserProjectsData> userProjectsData, String username) {
//        List<ProjectInfoDto> projectInfoDtos = new ArrayList<>();
//        for (UserProjectsData projectsData : userProjectsData) {
//            List<BranchWithRepoNameDto> branchDtos = makeGitHubRequestForProjectsBranches(projectsData.owner().login(), projectsData.name());
//            projectInfoDtos.add(new ProjectInfoDto(projectsData.name(), projectsData.owner().login(), branchDtos));
//        }
        return userProjectsData.stream()
                .map(projectsData -> retrieveBranchesForProject(username, projectsData.name()))
                .map(branchWithRepoNameDto -> new ProjectInfoDto(branchWithRepoNameDto.repoName(), username, branchWithRepoNameDto.branchesDto()))
                .collect(Collectors.toList());
    }
}
