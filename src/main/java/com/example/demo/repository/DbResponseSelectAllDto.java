package com.example.demo.repository;

import com.example.demo.repository.DbProjectsInfoDto;
import com.example.demo.repository.RepositoryEntity;

import java.util.List;

public record DbResponseSelectAllDto(List<DbProjectsInfoDto> projectsInfoDtos) {
}
