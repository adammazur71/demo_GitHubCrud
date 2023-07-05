package com.example.demo.repository;

import com.example.demo.repository.client.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.GitHubResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class RepositoryController {

    private final GitHubProxy gitHubProxy;

    String username;


    @Autowired
    public RepositoryController(GitHubProxy gitHubProxy) {
        this.gitHubProxy = gitHubProxy;

    }

    List<ProjectInfoDto> projectInfoDtos = new ArrayList<>();
    List<BranchDto> branchDtoList = new ArrayList<>();

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<RepositoryResponseDto> hello(@PathVariable String username) {
        this.username = username;
        List<GitHubResponseDto> response = makeGitHubRequest(username);
        List<ProjectInfoDto> projectInfoDtos1 = projectInfoDtos(response);
        return ResponseEntity.ok(new RepositoryResponseDto(projectInfoDtos1));
    }

    private List<GitHubResponseDto> makeGitHubRequest(String userName) {
        List<GitHubResponseDto> response = gitHubProxy.downloadUsersRepos(userName);
        System.out.println(response);
        return response;


    }

    private List<BranchDto> makeGitHubRequest(String userName, String projectName) {
        List<GitHubBranchInfoResponseDto> branchInfoResponse = gitHubProxy.downloadUsersReposBranchesInfo(userName, projectName);
        for (int i = 0; i < branchInfoResponse.size(); i++) {
            branchDtoList.add(new BranchDto(branchInfoResponse.get(i).name(), branchInfoResponse.get(i).commit().sha()));
        }
        return branchDtoList;
    }


    private List<ProjectInfoDto> projectInfoDtos(List<GitHubResponseDto> gitHubResponseDto) {

        for (int i = 0; i < gitHubResponseDto.size(); i++) {

            projectInfoDtos.add(new ProjectInfoDto(gitHubResponseDto.get(i).name(), gitHubResponseDto.get(i).owner().login(), makeGitHubRequest(gitHubResponseDto.get(i).owner().login(),
                    gitHubResponseDto.get(i).name())));
        }
        return projectInfoDtos;
    }
}
