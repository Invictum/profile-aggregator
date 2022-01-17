package com.example.aggregator;

import java.util.HashSet;
import java.util.Set;

/**
 * Unified representation of single repository. Assumed to be abstract enough to represent repository from any external
 * system.
 */
public class RepositoryData {

    private String origin;
    private String name;
    private String language;
    private long followersCount;
    private Set<String> topics = new HashSet<>();
    private boolean isForked;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public boolean isForked() {
        return isForked;
    }

    public void setForked(boolean forked) {
        isForked = forked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryData that = (RepositoryData) o;

        if (followersCount != that.followersCount) return false;
        if (isForked != that.isForked) return false;
        if (origin != null ? !origin.equals(that.origin) : that.origin != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        return topics != null ? topics.equals(that.topics) : that.topics == null;
    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (int) (followersCount ^ (followersCount >>> 32));
        result = 31 * result + (topics != null ? topics.hashCode() : 0);
        result = 31 * result + (isForked ? 1 : 0);
        return result;
    }
}
