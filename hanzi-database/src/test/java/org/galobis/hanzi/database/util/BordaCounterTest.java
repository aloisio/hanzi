package org.galobis.hanzi.database.util;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.stream.IntStream;

import org.galobis.hanzi.database.util.BordaCounter;
import org.junit.Test;

public class BordaCounterTest {
    private final BordaCounter<String> counter = new BordaCounter<>();

    @Test
    public void should_return_empty_for_no_votes() throws Exception {
        assertThat(counter.winners(), is(empty()));
    }

    @Test
    public void should_return_empty_for_empty_vote() throws Exception {
        counter.vote();
        assertThat(counter.winners(), is(empty()));
    }

    @Test
    public void should_return_vote_for_single_vote() throws Exception {
        counter.vote("a", "b", "c");
        assertThat(counter.winners(), contains("a", "b", "c"));
    }

    @Test
    public void should_rank_candidates_by_Borda_count_coherent_votes() throws Exception {
        counter.vote("a", "b")
                .vote()
                .vote("a", "b");
        assertThat(counter.winners(), contains("a", "b"));
    }

    @Test
    public void should_rank_candidates_by_Borda_count_conflicting_votes() throws Exception {
        counter.vote("x", "y")
                .vote("y");
        assertThat(counter.winners(), contains("y", "x"));
    }

    @Test
    public void should_rank_candidates_by_Borda_count_weighs_by_rank() {
        IntStream.rangeClosed(1, 51).forEach(i -> counter.vote("A", "C", "B", "D"));
        IntStream.rangeClosed(1, 5).forEach(i -> counter.vote("C", "B", "D", "A"));
        IntStream.rangeClosed(1, 23).forEach(i -> counter.vote("B", "C", "D", "A"));
        IntStream.rangeClosed(1, 21).forEach(i -> counter.vote("D", "C", "B", "A"));
        assertThat(counter.winners(), contains("C", "A", "B", "D"));
    }

    @Test
    public void should_decide_ties_by_order_of_candidate_appearance() throws Exception {
        counter.vote("3", "2", "1")
                .vote("3", "1", "2")
                .vote("2", "3", "1")
                .vote("2", "1", "3")
                .vote("1", "3", "2")
                .vote("1", "2", "3");
        assertThat(counter.winners(), contains("3", "2", "1"));
    }

    @Test
    public void should_rank_candidates_by_Borda_count() {
        counter.vote("qí")
                .vote("qí")
                .vote("qí", "jī", "jì", "zī", "zhāi", "jiǎn")
                .vote("jì", "qí", "zhāi");
        assertThat(counter.winners(), contains("qí", "jì", "zhāi", "jī", "zī", "jiǎn"));
    }

    @Test
    public void should_be_resettable() {
        counter.vote("x", "y")
                .vote("y")
                .reset()
                .vote("a", "b", "c");
        assertThat(counter.winners(), contains("a", "b", "c"));
    }
}
