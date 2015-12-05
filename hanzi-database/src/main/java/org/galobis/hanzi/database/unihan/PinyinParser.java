package org.galobis.hanzi.database.unihan;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;

public class PinyinParser {

    private static char COMBINING_GRAVE_ACCENT = 0x300;

    private static char COMBINING_ACUTE_ACCENT = 0x301;

    private static char COMBINING_MACRON = 0x304;

    private static char COMBINING_DIAERESIS = 0x308;

    private static char COMBINING_CARON = 0x30C;

    private static Map<Character, Integer> marksToTones = new LinkedHashMap<Character, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put(COMBINING_MACRON, 1);
            put(COMBINING_ACUTE_ACCENT, 2);
            put(COMBINING_CARON, 3);
            put(COMBINING_GRAVE_ACCENT, 4);
        }
    };

    public static String getSyllable(String pronunciation) {
        String normalized = normalize(pronunciation);
        if (normalized.indexOf(COMBINING_DIAERESIS) < 0) {
            return removeDiacritics(normalized);
        }
        return removeDiacritics(normalized).replace('u', 'ü');
    }

    public static Integer getTone(String pronunciation) {
        String normalized = normalize(pronunciation);
        for (Character mark : marksToTones.keySet()) {
            if (normalized.indexOf(mark) >= 0) {
                return marksToTones.get(mark);
            }
        }
        return 5;
    }

    private static String normalize(String pronunciation) {
        return Normalizer.normalize(pronunciation, Normalizer.Form.NFD).toLowerCase();
    }

    private static String removeDiacritics(String normalized) {
        return normalized.replaceAll("\\p{Block=CombiningDiacriticalMarks}", "");
    }
}
