package com.example.demo;

import com.example.demo.repository.BranchInfoEntity;
import com.example.demo.repository.GitHubRepository;
import com.example.demo.repository.RepositoryEntity;
import com.example.demo.repository.RepositoryService;
import com.example.demo.repository.client.GitHubProxy;
import com.example.demo.repository.client.dto.CommitDto;
import com.example.demo.repository.client.dto.GitHubBranchInfoResponseDto;
import com.example.demo.repository.client.dto.GitHubRepositoryOwnerDto;
import com.example.demo.repository.client.dto.UserProjectsDataDto;
import com.example.demo.repository.dto.BranchDto;
import com.example.demo.repository.dto.ProjectInfoDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class RepositoryServiceTest {


    GitHubProxy gitHubProxyMock = Mockito.mock(GitHubProxy.class);
    GitHubRepository gitHubRepositoryMock = Mockito.mock(GitHubRepository.class);
    RepositoryService repositoryService = new RepositoryService(gitHubProxyMock, gitHubRepositoryMock);

    @Test
    void shouldReturnRepositoryEntityListWithCorrectBranchesInfoWhenResponseFromGitHubHasBranchesInfo() {
        //GIVEN
        ProjectInfoDto repo1 = new ProjectInfoDto("repo", "owner", List.of(BranchDto.builder().branchName("branch").sha("sha").build(), BranchDto.builder().branchName("branch-1-2").sha("sha-1-2").build()));
        ProjectInfoDto repo2 = new ProjectInfoDto("repo2", "owner2", List.of(BranchDto.builder().branchName("branch2").sha("sha2").build()));
        List<ProjectInfoDto> projectInfoDtos = List.of(repo1, repo2);

        when(gitHubRepositoryMock.saveAll(anyList())).thenAnswer(returnsFirstArg());


        //WHEN
        List<RepositoryEntity> repositoryEntities = repositoryService.saveProjectsWithBranchInfo2db(projectInfoDtos);

        //THEN
        Assertions.assertThat(repositoryEntities).containsExactlyInAnyOrder(RepositoryEntity.builder().owner("owner").name("repo").branchInfoEntity(Set.of(new BranchInfoEntity("branch-1-2", "sha-1-2"), new BranchInfoEntity("branch", "sha"))).build(), RepositoryEntity.builder().owner("owner2").name("repo2").branchInfoEntity(Set.of(new BranchInfoEntity("branch2", "sha2")

        )).build());
    }

    @Test
    void shouldReturnRepositoryEntityListWhenResponseFromGitHubIsWithoutBranchesInfo() {
        //GIVEN
        UserProjectsDataDto userProjectsDataDto1 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner1"), "name1", false);
        UserProjectsDataDto userProjectsDataDto2 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner2"), "name2", true);
        UserProjectsDataDto userProjectsDataDto3 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner3"), "name3", false);
        UserProjectsDataDto userProjectsDataDto4 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner4"), "name4", true);

        List<UserProjectsDataDto> userProjectsDataDtos = List.of(userProjectsDataDto1, userProjectsDataDto2, userProjectsDataDto3, userProjectsDataDto4);
        when(gitHubRepositoryMock.saveAll(anyList())).thenAnswer(returnsFirstArg());

        //WHEN
        List<RepositoryEntity> repositoryEntities = repositoryService.saveProjects2db(userProjectsDataDtos);

        //THEN
        Assertions.assertThat(repositoryEntities).extracting(RepositoryEntity::getOwner, RepositoryEntity::getName).containsExactly(tuple("owner1", "name1"), tuple("owner2", "name2"), tuple("owner3", "name3"), tuple("owner4", "name4"));
        Assertions.assertThat(repositoryEntities).extracting(RepositoryEntity::getBranchInfoEntity).containsOnlyNulls();
    }

    @Test
    void shouldGetNoForkProjectsFromGitHub() {
        //GIVEN
        String userName = "owner";
        UserProjectsDataDto userProjectsDataDto1 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name1", false);
        UserProjectsDataDto userProjectsDataDto2 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name2", true);
        UserProjectsDataDto userProjectsDataDto3 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name3", false);
        UserProjectsDataDto userProjectsDataDto4 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name4", true);
        List<UserProjectsDataDto> responseFromGitHub = List.of(userProjectsDataDto1, userProjectsDataDto2, userProjectsDataDto3, userProjectsDataDto4);
        when(gitHubProxyMock.downloadUsersRepos(userName)).thenReturn(responseFromGitHub);

        //WHEN
        List<UserProjectsDataDto> userProjectsDataDtos = repositoryService.downloadNoForkProjects(userName);

        //THEN
        Assertions.assertThat(userProjectsDataDtos).extracting(UserProjectsDataDto::name).containsExactlyInAnyOrder("name1", "name3");

    }

    @Test
    void shouldGetNoForkProjectsWithBranchesInfoFromGitHub() {
        //GIVEN
        String userName = "owner";
        UserProjectsDataDto userProjectsDataDto1 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name1", false);
        UserProjectsDataDto userProjectsDataDto2 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name2", true);
        UserProjectsDataDto userProjectsDataDto3 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name3", false);
        UserProjectsDataDto userProjectsDataDto4 = new UserProjectsDataDto(new GitHubRepositoryOwnerDto("owner"), "name4", true);
        List<UserProjectsDataDto> responseFromGitHub = List.of(userProjectsDataDto1, userProjectsDataDto2, userProjectsDataDto3, userProjectsDataDto4);

        List<GitHubBranchInfoResponseDto> gitHubBranchesInfoResponse1 = List.of(new GitHubBranchInfoResponseDto("branch1", new CommitDto("sha1")));
        List<GitHubBranchInfoResponseDto> gitHubBranchesInfoResponse3 = List.of(new GitHubBranchInfoResponseDto("branch3", new CommitDto("sha3")));

        when(gitHubProxyMock.downloadUsersRepos(userName)).thenReturn(responseFromGitHub);
        when(gitHubProxyMock.downloadUsersReposBranchesInfo(userName, "name1")).thenReturn(gitHubBranchesInfoResponse1);
        when(gitHubProxyMock.downloadUsersReposBranchesInfo(userName, "name3")).thenReturn(gitHubBranchesInfoResponse3);


        //WHEN
        List<ProjectInfoDto> userProjectsDataDtos = repositoryService.downloadNoForkProjectsWithBranchesInfoDtos(userName);

        //THEN
        Assertions.assertThat(userProjectsDataDtos).extracting(ProjectInfoDto::repoName).containsExactlyInAnyOrder("name1", "name3");
        Assertions.assertThat(userProjectsDataDtos).extracting(ProjectInfoDto::branchDto).containsOnly(List.of(new BranchDto("branch1", "sha1")), (List.of(new BranchDto("branch3", "sha3"))));


    }
}

