package org.galobis.hanzi.model;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PinyinTest {

    @Test
    public void should_allow_creation_from_syllable_and_tone() {
        Pinyin wu3 = new Pinyin("wu", 3);
        assertThat(wu3.syllable(), is(equalTo("wu")));
        assertThat(wu3.tone(), is(equalTo(3)));
    }

    @Test
    public void should_implement_equals() {
        Pinyin ma3 = new Pinyin("ma", 3);
        assertEquals(ma3, ma3);
        assertEquals(ma3, new Pinyin("ma", 3));
        assertNotEquals(ma3, new Pinyin("ma", 4));
        assertNotEquals(ma3, null);
        assertNotEquals(ma3, new Pinyin("ma", 3) {
        });
    }

    @Test
    public void should_implement_hashcode() {
        Pinyin ma3a = new Pinyin("ma", 3);
        Pinyin ma3b = new Pinyin("ma", 3);
        Pinyin ma4 = new Pinyin("ma", 4);
        Map<Pinyin, String> map = new HashMap<>();
        map.put(ma3a, "1");
        map.put(ma3b, "2");
        map.put(ma4, "3");
        assertEquals("2", map.get(new Pinyin("ma", 3)));
        assertEquals("3", map.get(new Pinyin("ma", 4)));
    }

    @Test
    public void should_allow_creation_from_toned_string() {
        assertThat(new Pinyin("wu\u0300"), is(sameBeanAs(new Pinyin("wu", 4))));
        assertThat(new Pinyin("mǎ"), is(sameBeanAs(new Pinyin("ma", 3))));
    }
}
