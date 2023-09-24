package com.example.demo.repository.dto;

import lombok.Builder;

@Builder
public record BranchDto(String branchName, String sha) {
}
