package org.galobis.hanzi.domain.model;

import java.util.Objects;

public class Text {

    private final String content;

    private final Script script;

    public Text(String content, Script script) {
        this.content = content;
        this.script = script;
    }

    public Script script() {
        return script;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Text other = (Text) obj;
        return Objects.equals(content, other.content)
                && Objects.equals(script, other.script);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, script);
    }

    @Override
    public String toString() {
        return String.format("%1.1s:%s", script.name(), content);
    }
}
