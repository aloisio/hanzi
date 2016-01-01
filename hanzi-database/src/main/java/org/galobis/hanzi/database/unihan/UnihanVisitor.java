package org.galobis.hanzi.database.unihan;

import org.galobis.hanzi.domain.model.Hanzi;

public interface UnihanVisitor {
    void visit(Hanzi hanzi) throws Exception;

    void close() throws Exception;
}