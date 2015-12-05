package org.galobis.hanzi.database.unihan;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PinyinParserTest {
    @Test
    public void should_extract_raw_syllable_from_toned_syllable() throws Exception {
        Map<String, String> syllables = new HashMap<>();
        syllables.put("wǔ", "wu");
        syllables.put("pián", "pian");
        syllables.put("biàn", "bian");
        syllables.put("bia\u0300n", "bian");
        syllables.put("Chūn", "chun");
        syllables.put("Lǚ", "lü");
        syllables.put("lǘ", "lü");

        for (String pronunciation : syllables.keySet()) {
            String expectedSyllable = syllables.get(pronunciation);
            assertThat(PinyinParser.getSyllable(pronunciation), is(equalTo(expectedSyllable)));
        }
    }

    @Test
    public void should_extract_tone_from_toned_syllable() throws Exception {
        Map<String, Integer> tones = new HashMap<>();
        tones.put("wǔ", 3);
        tones.put("pián", 2);
        tones.put("biàn", 4);
        tones.put("bia\u0300n", 4);
        tones.put("Chūn", 1);
        tones.put("Lǚ", 3);
        tones.put("lǘ", 2);
        tones.put("lü", 5);
        tones.put("Ma", 5);
        tones.put("jiang", 5);

        for (String pronunciation : tones.keySet()) {
            Integer expectedTone = tones.get(pronunciation);
            assertThat(PinyinParser.getTone(pronunciation), is(equalTo(expectedTone)));
        }
    }
}
