package com.example.demo.repository.dto;

import java.util.List;

public record BranchWithRepoNameDto(List<BranchDto> branchesDto, String repoName) {
}
