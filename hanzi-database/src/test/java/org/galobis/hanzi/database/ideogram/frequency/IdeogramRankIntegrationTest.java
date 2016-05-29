package org.galobis.hanzi.database.ideogram.frequency;

import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
import static org.galobis.hanzi.database.ideogram.IdeogramIntegrationTest.codePointsOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.sql.Connection;
import java.util.List;

import org.galobis.test.annotation.category.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IdeogramRankIntegrationTest {
    private static final String SQL_SIMPLIFIED_RANK = "SELECT simplified_rank "
            + "FROM hanzi WHERE codepoint IN (%s) ORDER BY simplified_rank";

    private static final String SQL_TRADITIONAL_RANK = "SELECT traditional_rank "
            + "FROM hanzi WHERE codepoint IN (%s) ORDER BY traditional_rank";

    private static Connection connection;

    @BeforeClass
    public static void connectToDatabase() throws Exception {
        connection = getConnection();
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        connection.close();
    }

    @Test
    public void database_should_contain_simplified_ideogram_rank() throws Exception {
        List<Integer> ranks = asList(connection,
                String.format(SQL_SIMPLIFIED_RANK,
                        codePointsOf("的", "狐", "鴒", "畫")),
                Integer.class);
        assertThat(ranks, contains(1, 2321, 9933, null));
    }

    @Test
    public void database_should_contain_traditional_ideogram_rank() throws Exception {
        List<Integer> ranks = asList(connection,
                String.format(SQL_TRADITIONAL_RANK,
                        codePointsOf("的", "誇", "呱", "驪", "艽", "鷍", "门")),
                Integer.class);
        assertThat(ranks, contains(1, 1570, 3000, 5000, 10000, 13060, null));
    }
}
