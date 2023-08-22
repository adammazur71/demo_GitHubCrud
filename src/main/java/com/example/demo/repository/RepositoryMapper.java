package com.example.demo.repository;

import com.example.demo.repository.dto.ProjectRequestDto;

public class RepositoryMapper {
    public static RepositoryEntity mapFromProjectRequestDtoToRepositoryEntity(ProjectRequestDto request) {
        return new RepositoryEntity(request.owner(), request.name());
    }
}
