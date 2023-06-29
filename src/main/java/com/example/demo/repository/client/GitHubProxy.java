package com.example.demo.repository.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
public class GitHubProxy {
    private final RestTemplate restTemplate;

    @Autowired
    public GitHubProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<GitHubResponseDto> downloadUsersRepos(String username) {
        String uri = "https://api.github.com/users/" + username + "/repos";
        ResponseEntity<List<GitHubResponseDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }
}
