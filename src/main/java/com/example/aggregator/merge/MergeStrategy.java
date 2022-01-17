package com.example.aggregator.merge;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Defines the approach for repository data representation and merging
 */
public interface MergeStrategy {
    JsonNode merge(List<RepositoryData> repositories);
}
