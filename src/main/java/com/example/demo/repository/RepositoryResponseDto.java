package com.example.demo.repository;

import java.util.List;

public record RepositoryResponseDto(String repoName, String ownerLogin, List<BranchDto> branchDtos){}
