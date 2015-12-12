package org.galobis.hanzi.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class BordaCounter<T> {
    private static final class ScoreComparator<T> implements Comparator<Entry<T, Integer>> {
        @Override
        public int compare(Entry<T, Integer> obj1, Entry<T, Integer> obj2) {
            return obj2.getValue().compareTo(obj1.getValue());
        }
    }

    private final Collection<T> candidates = new LinkedHashSet<>();

    private final Map<T, Integer> scores = new LinkedHashMap<>();

    private final Map<List<T>, Integer> votes = new LinkedHashMap<>();

    private final ScoreComparator<T> comparator = new ScoreComparator<>();

    public BordaCounter<T> reset() {
        candidates.clear();
        votes.clear();
        scores.clear();
        return this;
    }

    public BordaCounter<T> vote(List<T> vote) {
        candidates.addAll(vote);
        votes.put(vote, votes.getOrDefault(vote, 0) + 1);
        return this;
    }

    @SafeVarargs
    public final BordaCounter<T> vote(T... vote) {
        return this.vote(Arrays.asList(vote));
    }

    public List<T> winners() {
        votes.forEach((vote, count) -> candidates.stream().filter(vote::contains).forEach(
                candidate -> incrementScore(candidate, count * computeBordaWeight(vote, candidate))));
        return candidatesSortedByDescendingScore();
    }

    private List<T> candidatesSortedByDescendingScore() {
        return scores.entrySet().stream()
                .sorted(comparator)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Integer computeBordaWeight(List<T> vote, T candidate) {
        return candidates.size() - vote.indexOf(candidate);
    }

    private void incrementScore(T candidate, Integer points) {
        scores.put(candidate, scores.getOrDefault(candidate, 0) + points);
    }
}
