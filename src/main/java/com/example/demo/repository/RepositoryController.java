package com.example.demo.repository;

import com.example.demo.repository.client.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.UserProjectsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class RepositoryController {

    private final GitHubProxy gitHubProxy;

    @Autowired
    public RepositoryController(GitHubProxy gitHubProxy) {
        this.gitHubProxy = gitHubProxy;

    }

    List<ProjectInfoDto> projectInfoDtos = new ArrayList<>();

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<RepositoryResponseDto> hello(@PathVariable String username) {
        List<UserProjectsData> response = makeGitHubRequestForUserProjects(username);
        List<UserProjectsData> responseWithNoForks = removeForks(response);
        List<ProjectInfoDto> projectInfoDtos = generateProjectInfoDtos(responseWithNoForks);
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos));
    }

    private List<UserProjectsData> removeForks(List<UserProjectsData> userProjectsData) {
        userProjectsData.removeIf(UserProjectsData::fork);
        return userProjectsData;
    }

    private List<UserProjectsData> makeGitHubRequestForUserProjects(String userName) {
        return gitHubProxy.downloadUsersRepos(userName);
    }

    private List<BranchDto> makeGitHubRequestForProjectsBranches(String userName, String projectName) {
        List<GitHubBranchInfoResponseDto> branchInfoResponse = gitHubProxy.downloadUsersReposBranchesInfo(userName, projectName);
        List<BranchDto> branchDtoList = new ArrayList<>();
        for (GitHubBranchInfoResponseDto gitHubBranchInfoResponseDto : branchInfoResponse) {
            branchDtoList.add(new BranchDto(gitHubBranchInfoResponseDto.name(), gitHubBranchInfoResponseDto.commit().sha()));
        }
        return branchDtoList;
    }


    private List<ProjectInfoDto> generateProjectInfoDtos(List<UserProjectsData> userProjectsData) {
        projectInfoDtos.clear();
        for (UserProjectsData projectsData : userProjectsData) {
            List<BranchDto> branchDtos;
            branchDtos = makeGitHubRequestForProjectsBranches(projectsData.owner().login(), projectsData.name());
            projectInfoDtos.add(new ProjectInfoDto(projectsData.name(), projectsData.owner().login(), branchDtos));
        }
        return projectInfoDtos;
    }
}
