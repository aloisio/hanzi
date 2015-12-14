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

public class HanziBatchInsertVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private HanziBatchInsertVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("hanzi")));
                result = statement;
            }
        };

        visitor = new HanziBatchInsertVisitor(connection);
    }

    @Test
    public void should_insert_each_visited_Hanzi() throws Exception {
        visitor.visit(new Hanzi.Builder(0x4FBF).definition("cheap").build());
        visitor.visit(new Hanzi.Builder(0x53D8).definition("change").build());
        new VerificationsInOrder() {
            {
                statement.setString(anyInt, "cheap");
                statement.addBatch();
                statement.setString(anyInt, "change");
                statement.addBatch();
            }
        };
    }
}
