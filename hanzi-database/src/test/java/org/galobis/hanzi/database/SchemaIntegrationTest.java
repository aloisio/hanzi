package org.galobis.hanzi.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import org.galobis.test.annotation.category.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SchemaIntegrationTest {
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
        try (ResultSet systemTables = connection.prepareCall("SELECT * FROM sys.systables WHERE tabletype = 'S'").executeQuery()) {
            assertThat(systemTables.next(), is(true));
        }
    }

    private static Properties getConnectionProperties() {
        Properties properties = new Properties();
        String tempDirectory = System.getProperty("java.io.tmpdir");
        properties.setProperty("derby.storage.tempDirectory", tempDirectory);
        properties.setProperty("derby.stream.error.file", Paths.get(tempDirectory, "derby.log").toString());
        return properties;
    }
}
