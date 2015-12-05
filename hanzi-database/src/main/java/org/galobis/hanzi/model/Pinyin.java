package org.galobis.hanzi.model;

import java.util.Objects;

import org.galobis.hanzi.database.unihan.PinyinParser;

public class Pinyin {

    private final String syllable;

    private final Integer tone;

    public Pinyin(String pronunciation) {
        this(PinyinParser.getSyllable(pronunciation), PinyinParser.getTone(pronunciation));
    }

    public Pinyin(String syllable, Integer tone) {
        this.syllable = syllable;
        this.tone = tone;
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
}
