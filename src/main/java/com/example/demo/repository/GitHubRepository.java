package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface GitHubRepository extends ListCrudRepository <RespositoryEntity, Long> {

}
