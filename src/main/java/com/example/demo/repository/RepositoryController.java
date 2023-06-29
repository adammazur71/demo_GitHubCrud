package com.example.demo.repository;

import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.GitHubResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RepositoryController {

    private final GitHubProxy gitHubProxy;

    @Autowired
    public RepositoryController(GitHubProxy gitHubProxy) {
        this.gitHubProxy = gitHubProxy;
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public RepositoryResponseDto hello(@PathVariable String username) {
        makeGitHubRequest(username);
        return new RepositoryResponseDto("name", "owner", List.of(new BranchDto("branchName", "sfs3qewre")));
    }

    private void makeGitHubRequest(String username) {
        List<GitHubResponseDto> response = gitHubProxy.downloadUsersRepos(username);
        System.out.println(response);
    }
}
