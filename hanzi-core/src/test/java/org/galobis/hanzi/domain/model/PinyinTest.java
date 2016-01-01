package org.galobis.hanzi.domain.model;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.galobis.hanzi.domain.model.Pinyin.pinyin;
import static org.galobis.hanzi.util.UnicodeUtilities.composition;
import static org.galobis.hanzi.util.UnicodeUtilities.decomposition;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.galobis.hanzi.domain.model.Pinyin;
import org.galobis.hanzi.domain.model.Tone;
import org.junit.Before;
import org.junit.Test;

public class PinyinTest {

    private final Properties validUnihanReadings = new Properties();

    private final Properties invalidUnihanReadings = new Properties();

    @Before
    public void readProperties() throws Exception {
        validUnihanReadings.loadFromXML(getClass().getResourceAsStream("/valid_unihan_readings.xml"));
        invalidUnihanReadings.loadFromXML(getClass().getResourceAsStream("/invalid_unihan_readings.xml"));
    }

    @Test
    public void should_allow_creation_from_number_tone_string() {
        for (String toneMarkString : validUnihanReadings.stringPropertyNames()) {
            String numberToneString = validUnihanReadings.getProperty(toneMarkString);
            Pinyin pinyin = pinyin(numberToneString);
            String syllable = numberToneString.substring(0, numberToneString.length() - 1);
            Integer tone = Integer.valueOf(numberToneString.substring(numberToneString.length() - 1));
            assertThat(pinyin.syllable(), is(equalTo(syllable)));
            assertThat(pinyin.tone(), is(equalTo(Tone.values()[tone - 1])));
        }
    }

    @Test
    public void should_implement_equals() {
        Pinyin ma3 = pinyin("ma3");
        assertEquals(ma3, ma3);
        assertEquals(ma3, pinyin("ma3"));
        assertEquals(ma3, pinyin("Ma3"));
        assertNotEquals(ma3, pinyin("ma4"));
        assertNotEquals(ma3, null);
        assertNotEquals(ma3, "ma3");
    }

    @Test
    public void should_implement_hashcode() {
        Pinyin ma3a = pinyin("ma3");
        Pinyin ma3b = pinyin("ma3");
        Pinyin ma4 = pinyin("ma4");
        Map<Pinyin, String> map = new HashMap<>();
        map.put(ma3a, "1");
        map.put(ma3b, "2");
        map.put(ma4, "3");
        assertEquals("2", map.get(pinyin("ma3")));
        assertEquals("3", map.get(pinyin("ma4")));
    }

    @Test
    public void should_allow_creation_from_tone_mark_string() {
        for (String toneMarkString : validUnihanReadings.stringPropertyNames()) {
            String numberToneString = validUnihanReadings.getProperty(toneMarkString);
            Pinyin pinyin = pinyin(numberToneString);
            assertEquals(pinyin(toneMarkString), pinyin);
            assertEquals(pinyin(composition(toneMarkString)), pinyin);
            assertEquals(pinyin(decomposition(toneMarkString)), pinyin);
        }
    }

    @Test
    public void should_allow_creation_from_invalid_readings_with_correct_tone_mark() {
        for (String toneMarkString : invalidUnihanReadings.stringPropertyNames()) {
            String numberToneString = invalidUnihanReadings.getProperty(toneMarkString);
            assertEquals(pinyin(toneMarkString), pinyin(numberToneString));
        }
    }

    @Test
    public void should_use_canonical_composition_for_toString() {
        for (String toneMarkString : validUnihanReadings.stringPropertyNames()) {
            String numberToneString = validUnihanReadings.getProperty(toneMarkString);
            assertThat(pinyin(numberToneString).toString(), is(equalTo(composition(toneMarkString))));
        }
    }
}
