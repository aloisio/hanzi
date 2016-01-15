package org.galobis.hanzi.database;

import org.galobis.hanzi.domain.model.Hanzi;

public class CompositeHanziVisitor implements HanziVisitor {

    private final HanziVisitor[] childVisitors;

    public CompositeHanziVisitor(HanziVisitor... visitors) {
        this.childVisitors = visitors;
    }

    @Override
    public void visit(Hanzi hanzi) {
        for (HanziVisitor visitor : childVisitors) {
            visitor.visit(hanzi);
        }
    }

    @Override
    public void close() {
        for (HanziVisitor visitor : childVisitors) {
            visitor.close();
        }
    }
}
