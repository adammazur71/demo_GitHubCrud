package com.example.demo;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
@WireMockTest(httpPort = 8081)
class RepositoryIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15.4")
            .withDatabaseName("integration-tests-db")
            .withUsername("user")
            .withPassword("admin");


    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("gitHubUrl", () -> "http://localhost:8081");
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    void happyPathScenario() throws Exception {
        //1. Użytkownik chce wyświetlić repozytoria. (zapytanie GET na endpoint /repos)
        //2. Serwer odpowiada pustą listą repozytoriów.

        //WHEN
        ResultActions resultActions = mockMvc.perform(get("/repos"));
        //THEN
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().json("[]"));


        //3. Użytkownik chce wyświetlić repo po ID=3. (zapytanie get na endpoint repos/3)
        //4. Serwer odpowiada kodem 404.
        //WHEN
        ResultActions resultByID = mockMvc.perform(get("/repos/3"));
        //THEN
        resultByID.andExpect(status().isNotFound());
        resultByID.andExpect(content().json("""
                    {
                    "message": "Result with id 3 not found",
                    "status": "NOT_FOUND"
                }
                """.trim()));

        //5. Użytkownikchce dodać własne repozytorium. (zapytanie post z owner=owner i name=name)
        //6. Serwer odpowiada kodem 201 i dodaje dane do bazy z ID = 1)
        //WHEN
        ResultActions resultByPost = mockMvc.perform(
                post("/repos")
                        .content("""
                                {
                                	"owner": "owner",
                                		"name": "name"
                                }
                                """.trim()
                        ).contentType(MediaType.APPLICATION_JSON_VALUE));
        //THEN
        resultByPost.andExpect(status().isCreated());
        resultByPost.andExpect(content().json("""
                  {
                  "id": 1,
                  "owner": "owner",
                  "name": "name"
                }
                """.trim()));


        //7. Użytkownik chce wyświetlić repo po ID=1. (zapytanie get na endpoint repos/1)
        //8. Serwer odpowiada kodem 200 i wyświetla repo z ID=1.
        //WHEN
        ResultActions resultByGetFromDB = mockMvc.perform(get("/repos/1"));
        //THEN
        resultByGetFromDB.andExpect(status().isOk());
        resultByPost.andExpect(content().json("""
                  {
                  "id": 1,
                  "owner": "owner",
                  "name": "name"
                }
                """.trim()));
        //9. Użytkownik chce pobrać informacje o repozytoriach użytkownika "adammazur71" z GitHuba i zapisać je do bazy
        // danych. (zapytanie GET na endpoint /save2db/adammazur71)

        //GIVEN
        String responseFromGitHub = Files.readString(Path.of("src/test/resources/responseFromGitHub.json"));
        stubFor(WireMock.get(urlEqualTo("/users/adammazur71/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseFromGitHub)
                ));

        //WHEN
        ResultActions resultByGetFromGitHub = mockMvc.perform(get("/save-all-projects/adammazur71"));
        //THEN
        resultByGetFromGitHub.andExpect(status().isOk());
        resultByGetFromGitHub.andExpect(jsonPath("$[0]['id']").value(2));
        resultByGetFromGitHub.andExpect(jsonPath("$[0]['owner']").value("adammazur71"));
        resultByGetFromGitHub.andExpect(jsonPath("$[0]['name']").value("demo_GitHubCrud"));
    }

    @Test
    void shouldGetUserNoForkProjectsWithBranchInfo() throws Exception {
        // 10. Użytkownik chce wyświetlić listę repozytoriów, które nie są forkami
        // wraz z branchami i SHA ostatniego commita. (GET na /projects-info/adammazur

        //GIVEN
        String responseFromGitHub2 = Files.readString(Path.of("src/test/resources/responseFromGitHubWith1ForkAnd1NoFork.json"));
        String responseFromGitHubWithBranchesInfo = Files.readString(Path.of("src/test/resources/responseFromGitHubWithBranchesInfo.json"));
        stubFor(WireMock.get(urlEqualTo("/users/adammazur/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseFromGitHub2)
                ));
        stubFor(WireMock.get(urlEqualTo("/repos/adammazur/demo_GitHubCrud/branches"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseFromGitHubWithBranchesInfo)
                ));
        //WHEN
        ResultActions resultByGetBranchesInfoFromGitHub = mockMvc.perform(get("/no-forks/adammazur"));

        //THEN
        resultByGetBranchesInfoFromGitHub.andExpect(status().isOk());
        resultByGetBranchesInfoFromGitHub.andExpect(jsonPath("$['projectInfoDto'][0]['branchDto'][0]['branchName']").value("holidays"));
        resultByGetBranchesInfoFromGitHub.andExpect(jsonPath("$['projectInfoDto'][0]['branchDto'][0]['sha']").value("2a034cbecd36c23d9afe735520856b4b11024881"));
        resultByGetBranchesInfoFromGitHub.andExpect(jsonPath("$['projectInfoDto'][0]['branchDto'][1]['branchName']").value("master"));
        resultByGetBranchesInfoFromGitHub.andExpect(jsonPath("$['projectInfoDto'][0]['branchDto'][1]['sha']").value("c163b39eefc1f10242c3f83916850a7726666e4b"));
        resultByGetBranchesInfoFromGitHub.andExpect(jsonPath("$['projectInfoDto'][0]['repoName']").value("demo_GitHubCrud"));
        boolean isForkInResponse = resultByGetBranchesInfoFromGitHub.andReturn().getResponse().getContentAsString().contains("FORK_demo_GitHubCrud");
        Assertions.assertThat(isForkInResponse).isFalse();

    }
}
