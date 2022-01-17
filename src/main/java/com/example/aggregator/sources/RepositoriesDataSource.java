package com.example.aggregator.sources;

import com.example.aggregator.RepositoryData;

import java.util.List;

/**
 * Abstraction that provides repositories info by given org or account name.
 * All the implementation of this interface will be automatically considered for collecting repos info. Extension point
 * for repos info collection.
 */
public interface RepositoriesDataSource {
    List<RepositoryData> loadForTeam(String name);
}
