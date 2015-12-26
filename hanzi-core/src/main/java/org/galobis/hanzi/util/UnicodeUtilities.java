package org.galobis.hanzi.util;

import java.text.Normalizer;

public class UnicodeUtilities {
    public static String decomposition(CharSequence sequence) {
        return Normalizer.normalize(sequence, Normalizer.Form.NFD);
    }

    public static String composition(CharSequence sequence) {
        return Normalizer.normalize(sequence, Normalizer.Form.NFC);
    }
}
