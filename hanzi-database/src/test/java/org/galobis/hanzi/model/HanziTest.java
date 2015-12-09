package org.galobis.hanzi.model;

import static org.galobis.hanzi.model.Pinyin.pinyin;
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
    public void should_implement_asString() {
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
                .readings(pinyin("ping2"), pinyin("peng1"))
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
}
