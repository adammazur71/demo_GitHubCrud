package com.example.demo.repository;

import com.example.demo.repository.client.GitHubResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

public record ProjectInfoDto(String repoName, String ownerLogin, List<BranchDto> branchDto){

}
