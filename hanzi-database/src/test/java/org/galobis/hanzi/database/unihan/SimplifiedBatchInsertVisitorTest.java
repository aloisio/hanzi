package org.galobis.hanzi.database.unihan;

import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.VerificationsInOrder;

public class SimplifiedBatchInsertVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private UnihanVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("simplified")));
                result = statement;
            }
        };

        visitor = new SimplifiedBatchInsertVisitor(connection);
    }

    @Test
    public void should_add_batch_for_each_simplified_variant() throws Exception {
        visitor.visit(new Hanzi.Builder("鍾".codePointAt(0)).simplified("钟".codePointAt(0), "锺".codePointAt(0)).build());
        new VerificationsInOrder() {
            {
                statement.setInt(anyInt, 0x937E);
                statement.setInt(anyInt, 0x949F);
                statement.addBatch();
                statement.setInt(anyInt, 0x937E);
                statement.setInt(anyInt, 0x953A);
                statement.addBatch();
            }
        };
    }
}
