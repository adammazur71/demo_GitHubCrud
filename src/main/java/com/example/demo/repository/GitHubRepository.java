package com.example.demo.repository;

import org.springframework.data.repository.ListCrudRepository;

public interface GitHubRepository extends ListCrudRepository<RepositoryEntity, Long> {

}
