package com.example.demo.repository.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record GitHubRepositoryOwner(String login) {
}
