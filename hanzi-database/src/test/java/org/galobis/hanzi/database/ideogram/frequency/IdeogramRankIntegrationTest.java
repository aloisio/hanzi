package org.galobis.hanzi.database.ideogram.frequency;

import static java.util.Arrays.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.galobis.hanzi.database.ideogram.IdeogramIntegrationTest;
import org.galobis.test.annotation.category.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IdeogramRankIntegrationTest {
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
                String.format("SELECT simplified_rank FROM hanzi WHERE codepoint IN (%s) ORDER BY codepoint",
                        String.join(",",
                                asList("的", "狐", "鴒").stream().sequential()
                                        .map(IdeogramIntegrationTest::asInt)
                                        .map(Object::toString)
                                        .collect(Collectors.toList())
                                        .toArray(new String[0]))),
                Integer.class);
        assertThat(ranks, Matchers.contains(2321, 1, 9933));
    }
}
