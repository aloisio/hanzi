package org.galobis.hanzi.database;

import org.galobis.hanzi.domain.model.Hanzi;

public interface HanziVisitor {
    void visit(Hanzi hanzi);

    void close();
}