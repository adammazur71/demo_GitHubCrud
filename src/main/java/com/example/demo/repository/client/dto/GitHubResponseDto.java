package com.example.demo.repository.client.dto;

import java.util.List;

public record GitHubResponseDto(List<UserProjectsData> userProjectsData) {
}
