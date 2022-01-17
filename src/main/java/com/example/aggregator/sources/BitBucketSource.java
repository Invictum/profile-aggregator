package com.example.aggregator.sources;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Provides repositories info for specific org by utilizing bitbucket REST api
 */
@Component
public class BitBucketSource implements RepositoriesDataSource {

    private final RestTemplate client;

    @Autowired
    public BitBucketSource(RestTemplate client) {
        this.client = client;
    }

    @Override
    public List<RepositoryData> loadForTeam(String name) {
        var repositories = new ArrayList<RepositoryData>();
        var url = "https://api.bitbucket.org/2.0/repositories/" + name;
        while (url != null) {
            var response = client.getForEntity(url, ObjectNode.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                break;
            }
            var body = response.getBody();
            // Proceed pagination
            var next = body.get("next");
            url = next == null ? null : next.asText(null);
            // Collect repositories info
            var values = body.get("values");
            if (values.isArray()) {
                for (JsonNode node : values) {
                    if (node.isObject()) {
                        repositories.add(fromNode((ObjectNode) node));
                    }
                }
            }
        }
        return repositories;
    }

    private RepositoryData fromNode(ObjectNode input) {
        var repo = new RepositoryData();
        repo.setOrigin("bitbucket");
        repo.setName(input.get("name").asText());
        repo.setLanguage(input.get("language").asText());
        repo.setForked(input.has("parent"));
        repo.setFollowersCount(extractFollowersCount(input));
        // Not available for Cloud BitBucket
        repo.setTopics(new HashSet<>());
        return repo;
    }

    private long extractFollowersCount(ObjectNode input) {
        return findNode(input, "links", "watchers", "href")
                .map(node -> {
                    var url = node.asText();
                    var response = client.getForEntity(url, ObjectNode.class);
                    return (long) findNode(response.getBody(), "values").map(JsonNode::size).orElse(0);
                }).orElse(0L);
    }

    private Optional<JsonNode> findNode(JsonNode context, String... path) {
        if (path.length == 0) {
            return Optional.empty();
        }
        JsonNode node = context;
        for (String segment : path) {
            node = node.get(segment);
            if (node == null) {
                return Optional.empty();
            }
        }
        return Optional.of(node);
    }
}
