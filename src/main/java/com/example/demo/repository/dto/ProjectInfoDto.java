package com.example.demo.repository.dto;

import java.util.List;

public record ProjectInfoDto(String repoName, String ownerLogin, List<BranchDto> branchDto) {

}
