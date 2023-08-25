package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
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
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("offer.http.client.config.uri", () -> WIRE_MOCK_HOST);
        registry.add("offer.http.client.config.port", () -> wireMockServer.getPort());
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
        //6. Serwer odpowiada kodem 201 i dodaje dane do bazy z ID = 3)
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


        //7. Użytkownik chce wyświetlić repo po ID=3. (zapytanie get na endpoint repos/3)
        //8. Serwer odpowiada kodem 200 i wyświetla repo z ID=3.
    }
}
