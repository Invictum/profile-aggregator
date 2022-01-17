package com.example.aggregator;

import com.example.aggregator.merge.MergeStrategy;
import com.example.aggregator.sources.RepositoriesDataSource;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interconnects all other parts of the application: controller, repo data sources and merge strategy
 */
@Service
public class ProfileService {

    private final List<RepositoriesDataSource> sources;
    private final MergeStrategy strategy;

    @Autowired
    public ProfileService(List<RepositoriesDataSource> sources, MergeStrategy strategy) {
        this.sources = sources;
        this.strategy = strategy;
    }

    public JsonNode mergedProfile(String name) {
        var repos = sources.stream().flatMap(source -> source.loadForTeam(name).stream()).collect(Collectors.toList());
        return strategy.merge(repos);
    }
}
