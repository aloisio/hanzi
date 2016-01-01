package org.galobis.hanzi.database.unihan;

import org.galobis.hanzi.domain.model.Hanzi;

public class CompositeUnihanVisitor implements UnihanVisitor {

    private final UnihanVisitor[] childVisitors;

    public CompositeUnihanVisitor(UnihanVisitor... visitors) {
        this.childVisitors = visitors;
    }

    @Override
    public void visit(Hanzi hanzi) throws Exception {
        for (UnihanVisitor visitor : childVisitors) {
            visitor.visit(hanzi);
        }
    }

    @Override
    public void close() throws Exception {
        for (UnihanVisitor visitor : childVisitors) {
            visitor.close();
        }
    }
}
