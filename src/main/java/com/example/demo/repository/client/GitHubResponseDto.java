package com.example.demo.repository.client;

import java.util.List;

public record GitHubResponseDto(List<UserProjectsData> userProjectsData) {
}
