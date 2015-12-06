package org.galobis.hanzi.model;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Pinyin {

    private static char COMBINING_ACUTE_ACCENT = 0x301;

    private static char COMBINING_CARON = 0x30C;

    private static char COMBINING_DIAERESIS = 0x308;

    private static char COMBINING_GRAVE_ACCENT = 0x300;

    private static char COMBINING_MACRON = 0x304;

    private static Map<Character, Integer> MARKS_TO_TONES = new LinkedHashMap<Character, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put(COMBINING_MACRON, 1);
            put(COMBINING_ACUTE_ACCENT, 2);
            put(COMBINING_CARON, 3);
            put(COMBINING_GRAVE_ACCENT, 4);
        }
    };

    private static Map<Integer, String> TONES_TO_MARKS = new LinkedHashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(1, String.valueOf(COMBINING_MACRON));
            put(2, String.valueOf(COMBINING_ACUTE_ACCENT));
            put(3, String.valueOf(COMBINING_CARON));
            put(4, String.valueOf(COMBINING_GRAVE_ACCENT));
            put(5, "");
        }
    };

    private final String pronunciation;

    private final String syllable;

    private final Integer tone;

    public Pinyin(String pronunciation) {
        String decomposed = decompose(pronunciation);
        this.syllable = getSyllable(decomposed);
        this.tone = getTone(decomposed);
        this.pronunciation = compose(decomposed);
    }

    public Pinyin(String syllable, Integer tone) {
        this(compose(syllable + TONES_TO_MARKS.get(tone)));
    }

    public String syllable() {
        return syllable;
    }

    public Integer tone() {
        return tone;
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pinyin other = (Pinyin) obj;
        return Objects.equals(syllable, other.syllable)
                && Objects.equals(tone, other.tone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(syllable, tone);
    }

    @Override
    public String toString() {
        return pronunciation;
    }

    private static String compose(String pronunciation) {
        return Normalizer.normalize(pronunciation, Normalizer.Form.NFC).toLowerCase();
    }

    private static String decompose(String pronunciation) {
        return Normalizer.normalize(pronunciation, Normalizer.Form.NFD).toLowerCase();
    }

    private static String getSyllable(String normalized) {
        if (normalized.indexOf(COMBINING_DIAERESIS) < 0) {
            return removeDiacritics(normalized);
        }
        return removeDiacritics(normalized).replace('u', 'ü');
    }

    private static Integer getTone(String normalized) {
        for (Character mark : MARKS_TO_TONES.keySet()) {
            if (normalized.indexOf(mark) >= 0) {
                return MARKS_TO_TONES.get(mark);
            }
        }
        return 5;
    }

    private static String removeDiacritics(String normalized) {
        return normalized.replaceAll("\\p{Block=CombiningDiacriticalMarks}", "");
    }
}
