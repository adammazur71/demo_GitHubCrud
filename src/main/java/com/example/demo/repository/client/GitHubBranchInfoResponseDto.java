package com.example.demo.repository.client;

import org.springframework.stereotype.Component;


public record GitHubBranchInfoResponseDto(String name, Commit commit) {
}
