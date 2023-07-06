package com.example.demo.repository;

import java.util.List;

public record BranchWithRepoNameDto(List<BranchDto> branchesDto, String repoName) {
}
