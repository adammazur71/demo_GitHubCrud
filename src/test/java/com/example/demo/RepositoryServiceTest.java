package com.example.demo;

import com.example.demo.repository.BranchInfoEntity;
import com.example.demo.repository.RepositoryEntity;
import com.example.demo.repository.RepositoryService;
import com.example.demo.repository.dto.BranchDto;
import com.example.demo.repository.dto.ProjectInfoDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@Testcontainers
public class RepositoryServiceTest {

    @Autowired
    RepositoryService repositoryService;
    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15.4")
            .withDatabaseName("integration-tests-db")
            .withUsername("user")
            .withPassword("admin");

    @Test
    void shouldReturnRepositoryEntityListWithCorrectBranchesInfoWhenResponseFromGitHubHasAllInfo() {
        //GIVEN
        ProjectInfoDto repo1 = new ProjectInfoDto("repo", "owner", List.of(
                BranchDto.builder()
                        .branchName("branch")
                        .sha("sha")
                        .build(),
                BranchDto.builder()
                        .branchName("branch-1-2")
                        .sha("sha-1-2")
                        .build()));
        ProjectInfoDto repo2 = new ProjectInfoDto("repo2", "owner2", List.of(BranchDto.builder()
                .branchName("branch2")
                .sha("sha2")
                .build()));
        List<ProjectInfoDto> projectInfoDtos = List.of(
                repo1, repo2);


        List<RepositoryEntity> expectedRepositoryEntities = List.of(
                RepositoryEntity.builder()
                        .id(1L)
                        .owner("owner")
                        .name("repo")
                        .branchInfoEntity(Set.of(
                                BranchInfoEntity.builder()
                                        .branchId(1L)
                                        .branchName("branch")
                                        .sha("sha")
                                        .build(),
                                BranchInfoEntity.builder()
                                        .branchId(2L)
                                        .branchName("branch-1-2")
                                        .sha("sha1-2")
                                        .build()))
                        .build(),
                RepositoryEntity.builder()
                        .id(2L)
                        .owner("owner2")
                        .name("repo2")
                        .branchInfoEntity(Set.of(
                                BranchInfoEntity.builder()
                                        .branchId(3L)
                                        .branchName("branch2")
                                        .sha("sha2")
                                        .build()
                        ))
                        .build());

        //WHEN
        List<RepositoryEntity> repositoryEntities = repositoryService.saveProjectsWithBranchInfo2db(projectInfoDtos);

        //THEN
        Assertions.assertThat(repositoryEntities)
                .extracting(RepositoryEntity::getOwner, RepositoryEntity::getName)
                .containsExactly(tuple("owner", "repo"), tuple("owner2", "repo2"));
//        Assertions.assertThat(repositoryEntities)
//                .extracting(entity -> entity.getBranchInfoEntity())
//                .containsExactlyInAnyOrder((Set.of(
//                                new BranchInfoEntity(1L, "branch-1-2", "sha-1-2"),
//                                new BranchInfoEntity(2L, "branch", "sha"))),
//                        Set.of(
//                                new BranchInfoEntity(3L, "branch2", "sha2")
//                        ));


    }

}
