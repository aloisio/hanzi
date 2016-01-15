package org.galobis.hanzi.database;

import org.galobis.hanzi.domain.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Injectable;
import mockit.Verifications;

public class CompositeHanziVisitorTest {
    private HanziVisitor visitor;

    @Injectable
    private HanziVisitor visitor1;

    @Injectable
    private HanziVisitor visitor2;

    @Before
    public void createComposite() throws Exception {
        visitor = new CompositeHanziVisitor(visitor1, visitor2);
    }

    @Test
    public void should_visit_children_passed_in_constructor() throws Exception {
        Hanzi hanzi = new Hanzi.Builder(0x3400).build();
        visitor.visit(hanzi);

        new Verifications() {
            {
                visitor1.visit(hanzi);
                visitor2.visit(hanzi);
            }
        };
    }

    @Test
    public void should_close_children_passed_in_constructor() throws Exception {
        visitor.close();

        new Verifications() {
            {
                visitor1.close();
                visitor2.close();
            }
        };
    }
}
