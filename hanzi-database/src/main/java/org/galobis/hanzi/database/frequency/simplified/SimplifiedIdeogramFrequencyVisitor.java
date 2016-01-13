package org.galobis.hanzi.database.frequency.simplified;

import org.galobis.hanzi.domain.model.Hanzi;

public interface SimplifiedIdeogramFrequencyVisitor {
    void visit(Hanzi hanzi);

    void close();
}
