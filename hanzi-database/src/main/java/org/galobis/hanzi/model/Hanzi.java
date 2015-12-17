package org.galobis.hanzi.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Hanzi {
    private final Integer codePoint;

    private final String definition;

    private final Collection<Pinyin> readings;

    private final Collection<Hanzi> simplified;

    public Integer codePoint() {
        return codePoint;
    }

    public String definition() {
        return definition;
    }

    public Collection<Pinyin> readings() {
        return readings;
    }

    public Collection<Hanzi> simplified() {
        return simplified;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Hanzi other = (Hanzi) obj;
        return Objects.equals(codePoint(), other.codePoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(codePoint());
    }

    @Override
    public String toString() {
        return String.valueOf(Character.toChars(codePoint));
    }

    public static class Builder {
        private final Integer codePoint;

        private String definition;

        private List<Pinyin> readings = Arrays.asList();

        private List<Hanzi> simplified = Arrays.asList();

        public Builder(Integer codePoint) {
            this.codePoint = codePoint;
        }

        public Builder definition(String definition) {
            this.definition = definition;
            return this;
        }

        public Builder readings(String... pinyin) {
            return readings(Arrays.stream(pinyin).map(Pinyin::pinyin).collect(Collectors.toList()));
        }

        public Builder readings(List<Pinyin> pinyin) {
            this.readings = Collections.unmodifiableList(pinyin);
            return this;
        }

        public Hanzi build() {
            return new Hanzi(this);
        }

        public Builder simplified(Hanzi... hanzi) {
            this.simplified = Arrays.asList(hanzi);
            return this;
        }

        public Builder simplified(Integer... codepoints) {
            simplified(Arrays.stream(codepoints)
                    .map(c -> new Hanzi.Builder(c).build())
                    .toArray(Hanzi[]::new));
            return this;
        }
    }

    private Hanzi(Builder builder) {
        codePoint = builder.codePoint;
        definition = builder.definition;
        readings = builder.readings;
        simplified = builder.simplified;
    }
}
