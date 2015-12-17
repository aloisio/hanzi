package org.galobis.hanzi.database.unihan;

import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;
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

    private UnihanVisitor visitor;

    @Before
    public void initializeVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("test")));
                result = statement;
            }
        };
        visitor = new BatchUnihanVisitor(connection, 2) {

            @Override
            protected String getSQL() {
                return "test";
            }

            @Override
            protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
                statement.addBatch();
            }
        };
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
        visitor.visit(new Hanzi.Builder(0x3400).build());
        visitor.visit(new Hanzi.Builder(0x3401).build());
        visitor.visit(new Hanzi.Builder(0x3402).build());
        visitor.visit(new Hanzi.Builder(0x3403).build());
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
        visitor.visit(new Hanzi.Builder(0x3400).build());
        visitor.visit(new Hanzi.Builder(0x3401).build());
        visitor.visit(new Hanzi.Builder(0x3401).build());
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
