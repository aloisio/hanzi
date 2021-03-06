package org.galobis.hanzi.domain.model;

import static java.util.Arrays.asList;
import static org.galobis.hanzi.domain.model.Pinyin.pinyin;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterableOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

public class HanziTest {

    @Test
    public void should_implement_equals_contract() {
        Hanzi hanzi = new Hanzi.Builder(0x6797).build();
        assertEquals(hanzi, hanzi);
        assertEquals(hanzi, new Hanzi.Builder(0x6797).build());
        assertNotEquals(hanzi, new Hanzi.Builder(0x2072F).build());
        assertNotEquals(hanzi, null);
        assertNotEquals(hanzi, Integer.valueOf(0x6797));
    }

    @Test
    public void should_implement_hashCode_contract() {
        Hanzi hanzi1 = new Hanzi.Builder(0x68A6).build();
        Hanzi hanzi2 = new Hanzi.Builder(0x68A6).build();
        Hanzi hanzi3 = new Hanzi.Builder(0x68A7).build();
        HashMap<Hanzi, Integer> map = new HashMap<>();
        map.put(hanzi1, 1);
        map.put(hanzi2, 2);
        map.put(hanzi3, 3);
        assertThat(map.entrySet(), hasSize(2));
        assertThat(map.get(hanzi1), is(equalTo(2)));
        assertThat(map.get(hanzi2), is(equalTo(2)));
        assertThat(map.get(hanzi3), is(equalTo(3)));
    }

    @Test
    public void should_implement_toString() {
        assertEquals(new Hanzi.Builder(0x2072e).build().toString(), "𠜮");
        assertEquals(new Hanzi.Builder(0x798F).build().toString(), "福");
    }

    @Test
    public void should_have_builder_with_reasonable_defaults() throws Exception {
        Hanzi hanzi = new Hanzi.Builder(0x9999).build();
        assertThat(hanzi.codePoint(), is(equalTo(0x9999)));
        assertThat(hanzi.definition(), is(nullValue()));
        assertThat(hanzi.readings(), is(emptyIterableOf(Pinyin.class)));
        try {
            hanzi.readings().add(pinyin("xiang1"));
            fail("Should be immutable");
        } catch (Exception expected) {
            // Expected
        }
    }

    @Test
    public void should_allow_passing_parameters_to_builder() {
        Hanzi hanzi = new Hanzi.Builder(0x82F9)
                .definition("artemisia; duckweed; apple")
                .readings("ping2", "peng1")
                .build();
        assertThat(hanzi.codePoint(), is(equalTo(0x82F9)));
        assertThat(hanzi.definition(), is("artemisia; duckweed; apple"));
        assertThat(hanzi.readings(), contains(pinyin("ping2"), pinyin("peng1")));
        try {
            hanzi.readings().add(pinyin("pong1"));
            fail("Should be immutable");
        } catch (Exception expected) {
            // Expected
        }
    }

    @Test
    public void should_allow_passing_Pinyin_list_to_builder() {
        Hanzi hanzi = new Hanzi.Builder(0x4E50)
                .definition("happy, glad; enjoyable; music")
                .readings(asList(pinyin("le4"), pinyin("yue4")))
                .build();
        assertThat(hanzi.readings(), contains(pinyin("le4"), pinyin("yue4")));
    }

    @Test
    public void should_allow_passing_simplified_variants_to_builder() {
        Hanzi hanzi = new Hanzi.Builder(0x937E)
                .simplified(0x949F, 0x953A)
                .build();
        assertThat(hanzi.simplified(), contains(
                new Hanzi.Builder(0x949F).build(),
                new Hanzi.Builder(0x953A).build()));
    }

    @Test
    public void should_allow_passing_traditional_variants_to_builder() {
        Hanzi hanzi = new Hanzi.Builder(0x949F)
                .traditional(0x937E, 0x9418)
                .build();
        assertThat(hanzi.traditional(), contains(
                new Hanzi.Builder(0x937E).build(),
                new Hanzi.Builder(0x9418).build()));
    }

    @Test
    public void should_allow_passing_string_to_builder_instead_of_codepoint() throws Exception {
        assertEquals(new Hanzi.Builder("福").build(), new Hanzi.Builder(0x798F).build());
        assertEquals(new Hanzi.Builder("福利").build(), new Hanzi.Builder(0x798F).build());
    }

    @Test
    public void should_not_build_with_invalid_codepoint_value() {
        assertBuildingHanziFailsWith(null);
        assertBuildingHanziFailsWith("");
        assertBuildingHanziFailsWith(-1);
        assertBuildingHanziFailsWith(Character.MAX_CODE_POINT + 1);
    }

    @Test
    public void should_allow_passing_simplified_character_rank_to_builder() {
        assertEquals(Integer.valueOf(1),
                new Hanzi.Builder("的").simplifiedRank(1).build().simplifiedRank());
    }

    @Test
    public void should_allow_passing_traditional_character_rank_to_builder() {
        assertEquals(Integer.valueOf(1),
                new Hanzi.Builder("的").traditionalRank(1).build().traditionalRank());
    }

    private void assertBuildingHanziFailsWith(String invalidInput) {
        try {
            new Hanzi.Builder(invalidInput).build();
            fail("Expected exception");
        } catch (IllegalArgumentException exc) {
            return;
        }
    }

    private void assertBuildingHanziFailsWith(int invalidInput) {
        try {
            new Hanzi.Builder(invalidInput).build();
            fail("Expected exception");
        } catch (IllegalArgumentException exc) {
            return;
        }
    }
}
