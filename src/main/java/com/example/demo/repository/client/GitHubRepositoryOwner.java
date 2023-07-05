package com.example.demo.repository.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record GitHubRepositoryOwner(String login) {
}
