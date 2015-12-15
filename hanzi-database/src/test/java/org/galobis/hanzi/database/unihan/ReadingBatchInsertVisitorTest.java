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

public class ReadingBatchInsertVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private ReadingBatchInsertVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("reading")));
                result = statement;
            }
        };

        visitor = new ReadingBatchInsertVisitor(connection);
    }

    @Test
    public void should_add_batch_for_each_reading() throws Exception {
        visitor.visit(new Hanzi.Builder("卤".codePointAt(0)).readings("lu3", "xi1").build());
        new VerificationsInOrder() {
            {
                statement.setInt(anyInt, 0x5364);
                statement.setString(anyInt, "lu");
                statement.setInt(anyInt, 3);
                statement.addBatch();
                statement.setInt(anyInt, 0x5364);
                statement.setString(anyInt, "xi");
                statement.setInt(anyInt, 1);
                statement.addBatch();
            }
        };
    }
}
