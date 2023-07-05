package com.example.demo.repository.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

@JsonIgnoreProperties(ignoreUnknown = true)

public record GitHubRepositoryOwner(String login) {
}
