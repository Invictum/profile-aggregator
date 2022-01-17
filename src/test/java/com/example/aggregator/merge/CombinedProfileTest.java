package com.example.aggregator.merge;

import com.example.aggregator.RepositoryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
class CombinedProfileTest {

    @Autowired
    private CombinedProfile strategy;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void mergeTest() throws Exception {
        // Prepare data
        var one = new RepositoryData();
        one.setOrigin("bit");
        one.setName("py-lib");
        one.setLanguage("python");
        one.setForked(true);
        one.setFollowersCount(5L);
        one.setTopics(Set.of());
        var two = new RepositoryData();
        two.setOrigin("bit");
        two.setName("java-lib");
        two.setLanguage("java");
        two.setForked(false);
        two.setFollowersCount(1L);
        two.setTopics(Set.of("test", "good"));
        var three = new RepositoryData();
        three.setOrigin("bit");
        three.setName("C-lib");
        three.setLanguage("C");
        three.setForked(false);
        three.setFollowersCount(10L);
        three.setTopics(Set.of("fast", "good"));
        var repos = List.of(one, two, three);
        // Mocks setup
        var actual = mapper.writeValueAsString(strategy.merge(repos));
        var expected = "{\"repos\":{\"forked\":1,\"original\":2},\"watchers\":16,\"languages\":{\"python\":1,\"C\":1,\"java\":1},\"topics\":{\"fast\":1,\"test\":1,\"good\":2}}";
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
