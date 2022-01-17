package com.example.aggregator.merge;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Merge strategy that combines info about repos into one unified team / account profile
 */
@Component
public class CombinedProfile implements MergeStrategy {

    private final ObjectMapper mapper;

    @Autowired
    public CombinedProfile(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public JsonNode merge(List<RepositoryData> repositories) {
        var root = mapper.createObjectNode();
        root.set("repos", forksPart(repositories));
        root.put("watchers", watchersPart(repositories));
        root.putPOJO("languages", languagesPart(repositories));
        root.putPOJO("topics", topicsPart(repositories));
        return root;
    }

    private JsonNode forksPart(List<RepositoryData> repositories) {
        var forkPartitions = repositories.stream().collect(Collectors.groupingBy(
                RepositoryData::isForked,
                Collectors.counting()
        ));
        var repos = mapper.createObjectNode();
        repos.put("forked", forkPartitions.getOrDefault(true, 0L));
        repos.put("original", forkPartitions.getOrDefault(false, 0L));
        return repos;
    }

    private long watchersPart(List<RepositoryData> repositories) {
        return repositories.stream()
                .reduce(0L, (count, repo) -> count + repo.getFollowersCount(), Long::sum);
    }

    private Map<String, Long> languagesPart(List<RepositoryData> repositories) {
        return repositories.stream().collect(Collectors.groupingBy(
                RepositoryData::getLanguage,
                Collectors.counting()
        ));
    }

    private Map<String, Long> topicsPart(List<RepositoryData> repositories) {
        return repositories.stream().flatMap(repo -> repo.getTopics().stream()).collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        ));
    }
}
