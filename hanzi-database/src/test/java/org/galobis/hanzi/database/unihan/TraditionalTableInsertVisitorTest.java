package org.galobis.hanzi.database.unihan;

import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.database.HanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.VerificationsInOrder;

public class TraditionalTableInsertVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private HanziVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("traditional")));
                result = statement;
            }
        };

        visitor = new TraditionalTableInsertVisitor(connection);
    }

    @Test
    public void should_add_batch_for_each_traditional_variant() throws Exception {
        visitor.visit(new Hanzi.Builder("钟")
                .traditional("鍾", "鐘").build());
        new VerificationsInOrder() {
            {
                statement.setInt(anyInt, 0x949F);
                statement.setInt(anyInt, 0x937E);
                statement.addBatch();
                statement.setInt(anyInt, 0x949F);
                statement.setInt(anyInt, 0x9418);
                statement.addBatch();
            }
        };
    }
}
