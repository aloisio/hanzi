package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

public class BatchUnihanVisitorTest {

    @Mocked
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private BatchUnihanVisitor visitor;

    @Before
    public void initializeVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(Matchers.containsString("hanzi")));
                result = statement;
            }
        };
        visitor = new BatchUnihanVisitor(connection, 2);
    }

    @After
    public void verifyClose() throws Exception {
        new Verifications() {
            {
                statement.close();
                times = 1;
                connection.close();
                times = 0;
            }
        };
    }

    @Test
    public void should_execute_batch_periodically() throws Exception {
        visitor.visit(new Hanzi(0x3400));
        visitor.visit(new Hanzi(0x3401));
        visitor.visit(new Hanzi(0x3402));
        visitor.visit(new Hanzi(0x3403));
        visitor.close();
        new Verifications() {
            {
                statement.addBatch();
                times = 4;
                statement.executeBatch();
                times = 2;
                connection.commit();
                times = 2;
            }
        };
    }

    @Test
    public void should_execute_batch_on_close_if_remaining_batches() throws Exception {
        visitor.visit(new Hanzi(0x3400));
        visitor.visit(new Hanzi(0x3401));
        visitor.visit(new Hanzi(0x3401));
        visitor.close();
        new Verifications() {
            {
                statement.addBatch();
                times = 3;
                statement.executeBatch();
                times = 2;
                connection.commit();
                times = 2;
            }
        };
    }
}
