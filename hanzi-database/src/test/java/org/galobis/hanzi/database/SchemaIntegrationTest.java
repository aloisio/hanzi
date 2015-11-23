package org.galobis.hanzi.database;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
        System.setProperty("derby.stream.error.field", String.format("%s.DEV_NULL", DatabaseUtilities.class.getName()));
        connection = DriverManager.getConnection("jdbc:derby:classpath:database/hanzi", getConnectionProperties());
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        connection.close();
    }

    @Test
    public void systemTablesCreated() throws Exception {
        List<String> systemTables = tableNamesOfType("S");
        assertThat(systemTables, hasSize(greaterThan(0)));
    }

    @Test
    public void userTablesCreated() throws Exception {
        List<String> userTables = tableNamesOfType("T");
        assertThat(userTables, containsInAnyOrder(asList(
                equalToIgnoringCase("hanzi"), equalToIgnoringCase("pinyin"),
                equalToIgnoringCase("reading"), equalToIgnoringCase("simplified"),
                equalToIgnoringCase("traditional"))));
    }

    private List<String> tableNamesOfType(String type) throws Exception {
        List<String> tableNames = new ArrayList<>();
        try (ResultSet userTables = connection.prepareCall(String.format(SQL_TABLE_NAMES, type)).executeQuery()) {
            while (userTables.next()) {
                tableNames.add(userTables.getString("tablename"));
            }
        }
        return tableNames;
    }

    private static Properties getConnectionProperties() {
        Properties properties = new Properties();
        String tempDirectory = System.getProperty("java.io.tmpdir");
        properties.setProperty("derby.storage.tempDirectory", tempDirectory);
        properties.setProperty("derby.stream.error.file", Paths.get(tempDirectory, "derby.log").toString());
        return properties;
    }
}
