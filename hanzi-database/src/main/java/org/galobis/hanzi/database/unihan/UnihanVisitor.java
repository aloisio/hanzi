package org.galobis.hanzi.database.unihan;

import org.galobis.hanzi.model.Hanzi;

public interface UnihanVisitor {
    void visit(Hanzi hanzi) throws Exception;
}