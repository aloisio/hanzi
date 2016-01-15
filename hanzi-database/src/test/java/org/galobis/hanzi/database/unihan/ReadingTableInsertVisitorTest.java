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

public class ReadingTableInsertVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private HanziVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("reading")));
                result = statement;
            }
        };

        visitor = new ReadingTableInsertVisitor(connection);
    }

    @Test
    public void should_add_batch_for_each_reading() throws Exception {
        visitor.visit(new Hanzi.Builder("卤").readings("lu3", "xi1").build());
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
