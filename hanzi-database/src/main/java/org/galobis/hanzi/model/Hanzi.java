package org.galobis.hanzi.model;

import java.util.Objects;

public class Hanzi {
    private final Integer codePoint;

    public Hanzi(Integer codePoint) {
        this.codePoint = codePoint;
    }

    public Integer codePoint() {
        return codePoint;
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
