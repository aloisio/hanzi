package org.galobis.hanzi.infrastructure;

import java.util.Optional;
import java.util.Set;

import org.galobis.hanzi.domain.model.Script;
import org.galobis.hanzi.domain.model.Text;
import org.galobis.hanzi.domain.model.TextFactory;

public class DatabaseTextFactory implements TextFactory {

    private Set<Integer> simplifiedCodePoints;

    private Set<Integer> traditionalCodePoints;

    @Override
    public Text textFrom(String content) {
        return new Text(content, getScript(content));
    }

    private Script getScript(String content) {
        long simplifiedCount = numberOfCodePointsInSet(content, getSimplifiedCodePoints());
        long traditionalCount = numberOfCodePointsInSet(content, getTraditionalCodePoints());
        if (simplifiedCount > 0 && simplifiedCount > traditionalCount) {
            return Script.SIMPLIFIED;
        }
        if (traditionalCount > 0 && traditionalCount > simplifiedCount) {
            return Script.TRADITIONAL;
        }
        return Script.UNKNOWN;
    }

    private Set<Integer> getSimplifiedCodePoints() {
        if (simplifiedCodePoints == null) {
            simplifiedCodePoints = new HanziTableGateway().getSimplifiedOnlyCodePoints();
        }
        return simplifiedCodePoints;
    }

    private Set<Integer> getTraditionalCodePoints() {
        if (traditionalCodePoints == null) {
            traditionalCodePoints = new HanziTableGateway().getTraditionalOnlyCodePoints();
        }
        return traditionalCodePoints;
    }

    private long numberOfCodePointsInSet(String content, Set<Integer> codePoints) {
        return Optional.ofNullable(content).orElse("").codePoints()
                .filter(codePoints::contains)
                .count();
    }
}
