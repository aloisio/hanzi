package org.galobis.hanzi.model;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
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
        Map<String, Pinyin> syllables = new HashMap<>();
        syllables.put("wǔ", new Pinyin("wu", 3));
        syllables.put("pián", new Pinyin("pian", 2));
        syllables.put("biàn", new Pinyin("bian", 4));
        syllables.put("bia\u0300n", new Pinyin("bian", 4));
        syllables.put("Chūn", new Pinyin("chun", 1));
        syllables.put("Lǚ", new Pinyin("lü", 3));
        syllables.put("lǘ", new Pinyin("lü", 2));
        syllables.put("wu\u0300", new Pinyin("wu", 4));
        syllables.put("mǎ", new Pinyin("ma", 3));
        syllables.put("ma", new Pinyin("ma", 5));
        for (String pronunciation : syllables.keySet()) {
            Pinyin expected = syllables.get(pronunciation);
            assertThat(pronunciation, new Pinyin(pronunciation), is(equalTo(expected)));
        }
    }

    @Test
    public void should_use_canonical_composition_for_toString() {
        assertThat(new Pinyin("ma", 3).toString(), Matchers.is(Matchers.equalTo("mǎ")));
        assertThat(new Pinyin("nü", 4).toString(), Matchers.is(Matchers.equalTo("nǜ")));
    }
}
