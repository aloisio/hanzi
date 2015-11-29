package org.galobis.hanzi.database;

import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
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
public class InitialStateIntegrationTest {
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
    public void database_should_contain_unihan_codepoints() throws Exception {
        List<Integer> codepoints = asList(connection,
                "select codepoint from hanzi where codepoint in (25165, 30340, 132913)",
                Integer.class);
        assertThat(codepoints, contains(asInt("才"), asInt("的"), asInt("𠜱")));
    }

    public static Integer asInt(String hanzi) {
        return hanzi.codePointAt(0);
    }

    public static String asString(int codePoint) {
        return String.valueOf(Character.toChars(codePoint));
    }
}
