package org.galobis.hanzi.database.ideogram.frequency;

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

public class HanziTableSimplifiedRankUpdateVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private HanziVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("simplified_rank")));
                result = statement;
            }
        };

        visitor = new HanziTableSimplifiedRankUpdateVisitor(connection);
    }

    @Test
    public void should_update_hanzi_table_with_ideogram_simplified_rank() throws Exception {
        visitor.visit(new Hanzi.Builder("的").simplifiedRank(1).build());
        visitor.visit(new Hanzi.Builder("是").simplifiedRank(3).build());

        new VerificationsInOrder() {
            {
                statement.setInt(anyInt, 1);
                statement.setInt(anyInt, 3);
                statement.addBatch();
            }
        };
    }
}
