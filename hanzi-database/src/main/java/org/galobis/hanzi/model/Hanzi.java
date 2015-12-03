package org.galobis.hanzi.model;

import java.util.Objects;

public class Hanzi {
    private final Integer codePoint;

    private final String definition;

    public Hanzi(Integer codePoint) {
        this(codePoint, null);
    }

    public Hanzi(Integer codePoint, String definition) {
        this.codePoint = codePoint;
        this.definition = definition;
    }

    public Integer codePoint() {
        return codePoint;
    }

    public String defintion() {
        return definition;
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
}
