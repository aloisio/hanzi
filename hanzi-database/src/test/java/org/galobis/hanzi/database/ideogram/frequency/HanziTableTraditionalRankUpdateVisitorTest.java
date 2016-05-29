package org.galobis.hanzi.database.ideogram.frequency;

import static com.jcabi.matchers.RegexMatchers.matchesPattern;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.domain.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Injectable;
import mockit.Verifications;
import mockit.VerificationsInOrder;

public class HanziTableTraditionalRankUpdateVisitorTest {

    private static final Hanzi HANZI_DE = new Hanzi.Builder("的").traditionalRank(1).build();

    private static final Hanzi HANZI_ZHE = new Hanzi.Builder("這").traditionalRank(10).build();

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private HanziTableTraditionalRankUpdateVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        visitor = new HanziTableTraditionalRankUpdateVisitor(connection);
    }

    @Test
    public void should_take_two_prepared_statement_parameters() throws Exception {
        visitor.visit(HANZI_DE);
        new Verifications() {
            {
                connection.prepareStatement(
                        withArgThat(matchesPattern("(?i)^[^\\?]*=\\s*\\?.*=\\s*\\?[^\\?]*$")));
            }
        };
    }

    @Test
    public void should_add_multiple_traditionalRank_updates_to_a_single_batch() throws Exception {
        visitor.visit(HANZI_DE);
        visitor.visit(HANZI_ZHE);
        new VerificationsInOrder() {
            {
                statement.setInt(anyInt, HANZI_DE.traditionalRank());
                statement.setInt(anyInt, HANZI_ZHE.traditionalRank());
                statement.addBatch();
            }
        };
    }
}
