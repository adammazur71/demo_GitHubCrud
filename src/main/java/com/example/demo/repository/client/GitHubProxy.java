package com.example.demo.repository.client;

import com.example.demo.repository.client.dto.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.dto.UserProjectsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
public class GitHubProxy {
    private final RestTemplate restTemplate;
    @Value("${gitHubUrl}")
    private String url;

    @Autowired
    public GitHubProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<UserProjectsData> downloadUsersRepos(String userName) {
        String uri = url + "/users/" + userName + "/repos";
        ResponseEntity<List<UserProjectsData>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    public List<GitHubBranchInfoResponseDto> downloadUsersReposBranchesInfo(String userName, String projectName) {
        String uri = url + "/repos/" + userName + "/" + projectName + "/branches";
        ResponseEntity<List<GitHubBranchInfoResponseDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }
}
