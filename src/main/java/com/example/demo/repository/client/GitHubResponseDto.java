package com.example.demo.repository.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubResponseDto(GitHubRepositoryOwner owner) {
    }
