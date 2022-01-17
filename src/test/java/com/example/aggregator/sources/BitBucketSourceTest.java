package com.example.aggregator.sources;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
class BitBucketSourceTest {

    @MockBean
    private RestTemplate client;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void loadForTeamTest() {
        // Prepare mocks
        ResponseEntity<ObjectNode> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(client.getForEntity(Mockito.anyString(), Mockito.eq(ObjectNode.class))).thenReturn(response);
        Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(response.getHeaders()).thenReturn(new HttpHeaders());
        var root = mapper.createObjectNode();
        var values = mapper.createArrayNode();
        var item = mapper.createObjectNode();
        item.put("name", "repo");
        item.put("language", "java");
        item.put("parent", "");
        values.add(item);
        root.set("values", values);
        Mockito.when(response.getBody()).thenReturn(root);
        // Prepare SUT
        var source = new BitBucketSource(client);
        var actual = source.loadForTeam("name");
        var expected = new RepositoryData();
        expected.setOrigin("bitbucket");
        expected.setName("repo");
        expected.setFollowersCount(0L);
        expected.setForked(true);
        expected.setLanguage("java");
        expected.setTopics(Set.of());
        // Check
        Assertions.assertThat(actual.get(0)).isEqualTo(expected);
    }
}
