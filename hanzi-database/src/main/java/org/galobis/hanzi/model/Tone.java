package org.galobis.hanzi.model;

import static org.galobis.hanzi.util.UnicodeUtilities.composition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

public enum Tone {
    /**
     * First tone (flat and high).
     */
    FIRST(ToneMarks.MACRON),
    /**
     * Second tone (rising).
     */
    SECOND(ToneMarks.ACUTE_ACCENT),
    /**
     * Third tone (falling then rising).
     */
    THIRD(ToneMarks.CARON),
    /**
     * Fourth tone (falling).
     */
    FOURTH(ToneMarks.GRAVE_ACCENT),
    /**
     * Neutral tone (light).
     */
    NEUTRAL(null);

    private static final class ToneMarker implements IntConsumer {
        private static final Set<Integer> MEDIAL_VOWELS = "iuüIUÜ".codePoints().boxed().collect(Collectors.toSet());

        private static final Set<Integer> VOWELS = "aeêiouüAEÊIOUÜ".codePoints().boxed().collect(Collectors.toSet());

        private final StringBuilder builder = new StringBuilder();

        private final Integer mark;

        private final List<Integer> syllableVowels;

        boolean marked = false;

        private ToneMarker(String syllable, Integer mark) {
            this.mark = mark;
            this.syllableVowels = syllable.codePoints().boxed().filter(VOWELS::contains)
                    .collect(Collectors.toList());
        }

        @Override
        public void accept(int value) {
            builder.appendCodePoint(value);
            if (!marked && (noVowels() || isOnlyVowel(value)
                    || isFirstVowelAndNotMedial(value)
                    || isFirstVowelAfterMedial(value))) {
                builder.appendCodePoint(mark);
                marked = true;
            }
        }

        @Override
        public String toString() {
            return composition(builder);
        }

        private boolean isFirstVowelAfterMedial(int value) {
            return isVowel(value) && !isFirstVowel(value) && isFirstVowelMedial();
        }

        private boolean isFirstVowelAndNotMedial(int value) {
            return isVowel(value) && !isFirstVowelMedial();
        }

        private boolean isFirstVowelMedial() {
            return isMedialVowel(syllableVowels.get(0));
        }

        private boolean isOnlyVowel(int value) {
            return isVowel(value) && syllableVowels.size() == 1;
        }

        private boolean isVowel(int value) {
            return VOWELS.contains(value);
        }

        private boolean isFirstVowel(int value) {
            return syllableVowels.indexOf(value) == 0;
        }

        private boolean isMedialVowel(int value) {
            return MEDIAL_VOWELS.contains(value);
        }

        private boolean noVowels() {
            return syllableVowels.isEmpty();
        }
    }

    private static final Map<Integer, Tone> MARKS_TO_TONES = new HashMap<Integer, Tone>() {
        private static final long serialVersionUID = 1L;

        {
            put(ToneMarks.MACRON, FIRST);
            put(ToneMarks.ACUTE_ACCENT, SECOND);
            put(ToneMarks.CARON, THIRD);
            put(ToneMarks.GRAVE_ACCENT, FOURTH);
        }
    };

    private static final class ToneMarks {
        private static Integer ACUTE_ACCENT = 0x301;

        private static Integer CARON = 0x30C;

        private static Integer GRAVE_ACCENT = 0x300;

        private static Integer MACRON = 0x304;

        private ToneMarks() {
            throw new UnsupportedOperationException("Do not instantiate");
        }
    }

    private final Integer mark;

    private Tone(Integer mark) {
        this.mark = mark;
    }

    /**
     * Apply this tone to the specified syllable.
     * 
     * @param syllable
     *            without tone diacritics.
     * @return Normalization Form C of syllable with tone diacritics.
     */
    public String apply(String syllable) {
        String compositionSyllable = composition(syllable);
        if (this == Tone.NEUTRAL) {
            return compositionSyllable;
        }
        IntConsumer marker = new ToneMarker(compositionSyllable, mark());
        compositionSyllable.codePoints().sequential().forEach(marker);
        return marker.toString();
    }

    public Integer mark() {
        return mark;
    }

    public Integer number() {
        return ordinal() + 1;
    }

    public static Tone forMarkedSyllable(String markedSyllable) {
        for (Integer codePoint : markedSyllable.codePoints().boxed().collect(Collectors.toList())) {
            if (MARKS_TO_TONES.containsKey(codePoint)) {
                return MARKS_TO_TONES.get(codePoint);
            }
        }
        return NEUTRAL;
    }
}
