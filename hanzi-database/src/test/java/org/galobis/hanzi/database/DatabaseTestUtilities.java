package org.galobis.hanzi.database;

import static org.galobis.hanzi.database.DatabaseConstants.DATABASE_URL;
import static org.galobis.hanzi.database.DatabaseConstants.ERROR_STREAM_FIELD_PROPERTY_KEY;
import static org.galobis.hanzi.database.DatabaseConstants.ERROR_STREAM_FIELD_PROPERTY_VALUE;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseTestUtilities {

    public static Connection getConnection() throws SQLException {
        System.setProperty(ERROR_STREAM_FIELD_PROPERTY_KEY, ERROR_STREAM_FIELD_PROPERTY_VALUE);
        return DriverManager.getConnection(DATABASE_URL, getConnectionProperties());
    }

    public static <T> List<T> asList(Connection connection, String sql, Class<T> type) throws Exception {
        List<T> result = new ArrayList<>();
        try (ResultSet resultSet = connection.prepareCall(sql).executeQuery()) {
            while (resultSet.next()) {
                result.add(resultSet.getObject(1, type));
            }
        }
        return result;
    }

    private static Properties getConnectionProperties() {
        Properties properties = new Properties();
        String tempDirectory = System.getProperty("java.io.tmpdir");
        properties.setProperty("derby.storage.tempDirectory", tempDirectory);
        properties.setProperty("derby.stream.error.file", Paths.get(tempDirectory, "derby.log").toString());
        return properties;
    }
}
