package com.example.aggregator;

import com.example.aggregator.merge.MergeStrategy;
import com.example.aggregator.sources.BitBucketSource;
import com.example.aggregator.sources.GitHubSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
class ProfileServiceTest {

    @MockBean
    private MergeStrategy strategy;

    @MockBean
    private BitBucketSource bitBucketSource;

    @MockBean
    private GitHubSource gitHubSource;

    @Test
    void mergedProfileTest() {
        // Mocks setup
        var bitBucketRepo = new RepositoryData();
        bitBucketRepo.setOrigin("bitbucket");
        Mockito.when(bitBucketSource.loadForTeam("name")).thenReturn(List.of(bitBucketRepo));
        var gitHubRepo = new RepositoryData();
        bitBucketRepo.setOrigin("github");
        Mockito.when(gitHubSource.loadForTeam("name")).thenReturn(List.of(gitHubRepo));
        // Check
        var service = new ProfileService(List.of(bitBucketSource, gitHubSource), strategy);
        service.mergedProfile("name");
        Mockito.verify(strategy).merge(List.of(bitBucketRepo, gitHubRepo));
    }
}
