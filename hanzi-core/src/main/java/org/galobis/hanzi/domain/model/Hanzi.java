package org.galobis.hanzi.domain.model;

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

    private final Collection<Hanzi> traditional;

    private final Integer simplifiedRank;

    private final Integer traditionalRank;

    private Hanzi(Builder builder) {
        codePoint = builder.codePoint;
        definition = builder.definition;
        readings = builder.readings;
        simplified = builder.simplified;
        traditional = builder.traditional;
        simplifiedRank = builder.simplifiedRank;
        traditionalRank = builder.traditionalRank;
    }

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

    public Collection<Hanzi> traditional() {
        return traditional;
    }

    public Integer simplifiedRank() {
        return simplifiedRank;
    }

    public Integer traditionalRank() {
        return traditionalRank;
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

        private List<Hanzi> traditional = Arrays.asList();

        private Integer simplifiedRank;

        private Integer traditionalRank;

        public Builder(int codePoint) {
            this.codePoint = validate(codePoint);
        }

        public Builder(String ideogram) {
            this(validate(ideogram).codePointAt(0));
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

        public Builder simplified(String... ideograms) {
            return simplified(Arrays.stream(ideograms)
                    .map(c -> new Hanzi.Builder(c).build())
                    .toArray(Hanzi[]::new));
        }

        public Builder simplified(Integer... codepoints) {
            return simplified(Arrays.stream(codepoints)
                    .map(c -> new Hanzi.Builder(c).build())
                    .toArray(Hanzi[]::new));
        }

        public Builder traditional(Hanzi... hanzi) {
            this.traditional = Arrays.asList(hanzi);
            return this;
        }

        public Builder traditional(String... ideograms) {
            return traditional(Arrays.stream(ideograms)
                    .map(c -> new Hanzi.Builder(c).build())
                    .toArray(Hanzi[]::new));
        }

        public Builder traditional(Integer... codepoints) {
            return traditional(Arrays.stream(codepoints)
                    .map(c -> new Hanzi.Builder(c).build())
                    .toArray(Hanzi[]::new));
        }

        public Builder simplifiedRank(Integer rank) {
            this.simplifiedRank = rank;
            return this;
        }

        public Builder traditionalRank(Integer rank) {
            this.traditionalRank = rank;
            return this;
        }

        private static Integer validate(int codePoint) {
            if (Character.isValidCodePoint(codePoint)) {
                return codePoint;
            }
            throw new IllegalArgumentException();
        }

        private static String validate(String ideogram) {
            if (ideogram == null || ideogram.isEmpty()) {
                throw new IllegalArgumentException();
            }
            return ideogram;
        }
    }
}
