package org.galobis.hanzi.infrastructure;

import java.util.Optional;
import java.util.Set;

import org.galobis.hanzi.domain.model.Script;
import org.galobis.hanzi.domain.model.Text;
import org.galobis.hanzi.domain.model.TextFactory;

public class DatabaseTextFactory implements TextFactory {

    private Set<Integer> simplifiedCodepoints;

    private Set<Integer> getSimplifiedCodepoints() {
        if (simplifiedCodepoints == null) {
            simplifiedCodepoints = new HanziTableGateway().getSimpifiedOnlyCodepoints();
        }
        return simplifiedCodepoints;
    }

    @Override
    public Text textFrom(String content) {
        long simplifiedCount = Optional.ofNullable(content).orElse("").codePoints()
                .filter(getSimplifiedCodepoints()::contains).count();
        return new Text(content, getScript(simplifiedCount));
    }

    private Script getScript(long simplifiedCount) {
        if (simplifiedCount > 0) {
            return Script.SIMPLIFIED;
        }
        return Script.UNKNOWN;
    }
}
