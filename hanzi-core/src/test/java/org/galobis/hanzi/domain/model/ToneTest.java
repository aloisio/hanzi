package org.galobis.hanzi.domain.model;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.galobis.hanzi.util.UnicodeUtilities.composition;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.galobis.hanzi.domain.model.Tone;
import org.junit.Test;

public class ToneTest {

    @Test
    public void should_have_five_values() {
        assertThat(Tone.values(), is(arrayWithSize(5)));
    }

    @Test
    public void should_have_usual_names_for_Mandarin_tones() {
        assertThat(Tone.valueOf("FIRST"), is(equalTo(Tone.values()[0])));
        assertThat(Tone.valueOf("SECOND"), is(equalTo(Tone.values()[1])));
        assertThat(Tone.valueOf("THIRD"), is(equalTo(Tone.values()[2])));
        assertThat(Tone.valueOf("FOURTH"), is(equalTo(Tone.values()[3])));
        assertThat(Tone.valueOf("NEUTRAL"), is(equalTo(Tone.values()[4])));
    }

    @Test
    public void should_have_a_mark_character_for_each_tone_except_neutral() {
        assertEquals("COMBINING MACRON", Character.getName(Tone.FIRST.mark()));
        assertEquals("COMBINING ACUTE ACCENT", Character.getName(Tone.SECOND.mark()));
        assertEquals("COMBINING CARON", Character.getName(Tone.THIRD.mark()));
        assertEquals("COMBINING GRAVE ACCENT", Character.getName(Tone.FOURTH.mark()));
        assertThat(Tone.NEUTRAL.mark(), is(nullValue()));
    }

    @Test
    public void should_have_a_canonical_number_for_each_tone() {
        assertEquals(1, Tone.FIRST.number().intValue());
        assertEquals(2, Tone.SECOND.number().intValue());
        assertEquals(3, Tone.THIRD.number().intValue());
        assertEquals(4, Tone.FOURTH.number().intValue());
        assertEquals(5, Tone.NEUTRAL.number().intValue());
    }

    @Test
    public void forMarkedSyllable_should_return_Tone_given_diacritical_mark() {
        assertEquals(Tone.FIRST, Tone.forMarkedSyllable(String.format("ma%c", Tone.FIRST.mark())));
        assertEquals(Tone.SECOND, Tone.forMarkedSyllable(String.format("ma%c", Tone.SECOND.mark())));
        assertEquals(Tone.THIRD, Tone.forMarkedSyllable(String.format("ma%c", Tone.THIRD.mark())));
        assertEquals(Tone.FOURTH, Tone.forMarkedSyllable(String.format("ma%c", Tone.FOURTH.mark())));
        assertEquals(Tone.NEUTRAL, Tone.forMarkedSyllable("ma"));
    }

    @Test
    public void should_be_able_to_apply_itself_to_a_syllable() throws Exception {
        Properties allUnihanReadings = new Properties();
        allUnihanReadings.loadFromXML(getClass().getResourceAsStream("/valid_unihan_readings.xml"));
        for (String pinyin : allUnihanReadings.stringPropertyNames()) {
            String toneNumberPinyin = allUnihanReadings.getProperty(pinyin);
            String syllable = toneNumberPinyin.substring(0, toneNumberPinyin.length() - 1);
            int toneOrdinal = Integer.valueOf(toneNumberPinyin.substring(toneNumberPinyin.length() - 1)) - 1;
            String normalizedPinyin = composition(pinyin);
            assertEquals(normalizedPinyin, Tone.values()[toneOrdinal].apply(syllable));
        }
    }
}
