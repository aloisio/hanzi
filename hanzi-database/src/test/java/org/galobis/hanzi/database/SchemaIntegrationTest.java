package org.galobis.hanzi.database;

import static java.util.Arrays.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.sql.Connection;
import java.util.List;

import org.galobis.test.annotation.category.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SchemaIntegrationTest {
    private static final String SQL_TABLE_NAMES = "SELECT tablename FROM sys.systables WHERE tabletype = '%s'";

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
    public void database_should_contain_system_tables() throws Exception {
        List<String> systemTables = asList(connection, String.format(SQL_TABLE_NAMES, "S"), String.class);
        assertThat(systemTables, hasSize(greaterThan(0)));
    }

    @Test
    public void database_should_contain_user_tables() throws Exception {
        List<String> userTables = asList(connection, String.format(SQL_TABLE_NAMES, "T"), String.class);
        assertThat(userTables, containsInAnyOrder(asList(
                equalToIgnoringCase("hanzi"), equalToIgnoringCase("pinyin"),
                equalToIgnoringCase("reading"), equalToIgnoringCase("simplified"),
                equalToIgnoringCase("traditional"))));
    }
}
