package com.example.aggregator.sources;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@SpringBootTest
class GitHubSourceTest {

    @MockBean
    private RestTemplate client;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void loadForTeamTest() {
        // Prepare mocks
        ResponseEntity<JsonNode> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(client.getForEntity(
                        Mockito.any(),
                        Mockito.eq(JsonNode.class),
                        Mockito.eq("name")))
                .thenReturn(response);
        Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(response.getHeaders()).thenReturn(new HttpHeaders());
        var node = mapper.createObjectNode();
        node.put("name", "repo");
        node.put("watchers_count", 42L);
        node.put("fork", true);
        node.put("language", "php");
        var topics = mapper.createArrayNode();
        topics.add("1");
        topics.add("2");
        node.set("topics", topics);
        var array = mapper.createArrayNode();
        array.add(node);
        Mockito.when(response.getBody()).thenReturn(array);
        // Prepare SUT
        var source = new GitHubSource(client);
        var actual = source.loadForTeam("name");
        var expected = new RepositoryData();
        expected.setOrigin("github");
        expected.setName("repo");
        expected.setFollowersCount(42);
        expected.setForked(true);
        expected.setLanguage("php");
        expected.setTopics(Set.of("1", "2"));
        // Check
        Assertions.assertThat(actual.get(0)).isEqualTo(expected);
    }
}
