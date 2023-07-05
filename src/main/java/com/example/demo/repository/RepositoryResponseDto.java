package com.example.demo.repository;

import org.springframework.stereotype.Component;

import java.util.List;

public record RepositoryResponseDto(List<ProjectInfoDto> projectInfoDto) {
}
