package org.galobis.hanzi.domain.model;

import static org.galobis.hanzi.util.UnicodeUtilities.decomposition;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pinyin {

    private static final String COMPOSITE_U_WITH_DIAERESIS = "ü";

    private static final String COMPOSITE_E_WITH_CIRCUMFLEX = "ê";

    private static final String DECOMPOSED_E_WITH_CIRCUMFLEX = decomposition(COMPOSITE_E_WITH_CIRCUMFLEX);

    private static final String DECOMPOSED_U_WITH_DIAERESIS = decomposition(COMPOSITE_U_WITH_DIAERESIS);

    private static final Pattern NUMBERED_TONE = Pattern.compile("^(.+)([1-5])$");

    private final String syllable;

    private final Tone tone;

    private final String pronunciation;

    private Pinyin(String syllable, Tone tone) {
        this.syllable = syllable;
        this.tone = tone;
        this.pronunciation = tone.apply(syllable);
    }

    public String syllable() {
        return syllable;
    }

    public Tone tone() {
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

    public static Pinyin pinyin(String pronunciation) {
        String normalizedPronunciation = decomposition(pronunciation).toLowerCase();
        Matcher numberedToneMatcher = NUMBERED_TONE.matcher(normalizedPronunciation);
        if (numberedToneMatcher.matches()) {
            Integer toneNumber = Integer.valueOf(numberedToneMatcher.group(2));
            return new Pinyin(getSyllable(numberedToneMatcher.group(1)),
                    Tone.values()[toneNumber - 1]);
        } else {
            return new Pinyin(getSyllable(normalizedPronunciation),
                    Tone.forMarkedSyllable(normalizedPronunciation));
        }
    }

    private static String getSyllable(String normalized) {
        return normalized
                .replace(DECOMPOSED_U_WITH_DIAERESIS, COMPOSITE_U_WITH_DIAERESIS)
                .replace(DECOMPOSED_E_WITH_CIRCUMFLEX, COMPOSITE_E_WITH_CIRCUMFLEX)
                .replaceAll("\\p{Block=CombiningDiacriticalMarks}", "");
    }
}
