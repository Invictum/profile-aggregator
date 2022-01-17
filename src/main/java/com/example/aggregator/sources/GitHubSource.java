package com.example.aggregator.sources;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides repositories info for specific org by utilizing github REST api
 */
@Component
public class GitHubSource implements RepositoriesDataSource {

    private final RestTemplate client;

    @Autowired
    public GitHubSource(RestTemplate client) {
        this.client = client;
    }

    @Override
    public List<RepositoryData> loadForTeam(String name) {
        var repositories = new ArrayList<RepositoryData>();
        var url = "https://api.github.com/orgs/" + name + "/repos?per_page=50";
        while (url != null) {
            var response = client.getForEntity(url, ArrayNode.class, name);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                break;
            }
            // Handle pagination
            url = next(response.getHeaders());
            // Collect repos
            var body = response.getBody();
            if (body.isArray()) {
                for (JsonNode node : body) {
                    if (node.isObject()) {
                        repositories.add(fromNode(node));
                    }
                }
            }
        }
        return repositories;
    }

    private RepositoryData fromNode(JsonNode node) {
        var data = new RepositoryData();
        data.setOrigin("github");
        data.setName(node.get("name").asText());
        data.setFollowersCount(node.get("watchers_count").asLong());
        data.setForked(node.get("fork").asBoolean());
        data.setLanguage(node.get("language").asText(""));
        var topics = new HashSet<String>();
        var topicsNode = node.get("topics");
        if (topicsNode != null && topicsNode.isArray()) {
            for (JsonNode topic : topicsNode) {
                topics.add(topic.asText());
            }
        }
        data.setTopics(topics);
        return data;
    }

    private String next(HttpHeaders headers) {
        if (!headers.containsKey("Link")) {
            return null;
        }
        var value = headers.get("Link").get(0);
        var matcher = Pattern.compile("^.*<(.+?)>; rel=\"next\".*$").matcher(value);
        return matcher.find() ? matcher.group(1) : null;
    }
}
