package com.example.demo.repository;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface GitHubRepository extends ListCrudRepository <RepositoryEntity, Long> {
List<RepositoryEntity> findAll();
}
