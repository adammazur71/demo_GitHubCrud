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

        public List<UserProjectsData> downloadUsersRepos(String userName) {
        String uri = "https://api.github.com/users/" + userName + "/repos";
        ResponseEntity<List<UserProjectsData>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }
//    public GitHubResponseDto downloadUsersRepos(String userName) {
//        String uri = "https://api.github.com/users/" + userName + "/repos";
//        ResponseEntity<GitHubResponseDto> response = restTemplate.exchange(
//                uri,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {
//                }
//        );
//        return response.getBody();
//    }
//    public GitHubResponseDto downloadUsersRepos(String userName) throws JsonProcessingException {
//        String uri = "https://api.github.com/users/" + userName + "/repos";
//        ResponseEntity<String> response = restTemplate.exchange(
//                uri,
//                HttpMethod.GET,
//                null,
//                String.class
//        );
//        String json = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        GitHubResponseDto gitHubResponseDto = objectMapper.readValue(json, GitHubResponseDto.class);
//        return gitHubResponseDto;
//    }

    public List<GitHubBranchInfoResponseDto> downloadUsersReposBranchesInfo (String userName, String projectName) {
        String uri = "https://api.github.com/repos/" + userName + "/" + projectName + "/branches";
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
