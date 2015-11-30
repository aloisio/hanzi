package org.galobis.hanzi.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.galobis.hanzi.database.unihan.BatchUnihanVisitor;
import org.galobis.hanzi.database.unihan.UnihanReader;

public class DatabasePopulator {
    private static final String[] DDL_STATEMENTS = {
            "CREATE TABLE hanzi (codepoint INTEGER NOT NULL, PRIMARY KEY (codepoint))",
            "CREATE TABLE pinyin (id INTEGER NOT NULL, PRIMARY KEY (id))",
            "CREATE TABLE reading (id INTEGER NOT NULL, PRIMARY KEY (id))",
            "CREATE TABLE simplified (id INTEGER NOT NULL, PRIMARY KEY (id))",
            "CREATE TABLE traditional (id INTEGER NOT NULL, PRIMARY KEY (id))"
    };

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String connectionURL = args[0];
            DriverManager.registerDriver(new EmbeddedDriver());
            try (Connection connection = DriverManager.getConnection(connectionURL + ";create=true")) {
                createTables(connection);
                populateTables(connection);
            }
            shutdownDatabase(connectionURL);
            shutdownEngine();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        for (String ddl : DDL_STATEMENTS) {
            connection.prepareCall(ddl).executeUpdate();
        }
    }

    private static void populateTables(Connection connection) throws Exception, IOException {
        new UnihanReader(new BatchUnihanVisitor(connection)).read();
    }

    private static void shutdownDatabase(String connectionURL) {
        try (Connection connection = DriverManager.getConnection(connectionURL + ";shutdown=true")) {
            return;
        } catch (SQLException exc) {
            return;
        }
    }

    private static void shutdownEngine() {
        try (Connection connection = DriverManager.getConnection("jdbc:derby:" + ";shutdown=true")) {
            return;
        } catch (SQLException exc) {
            return;
        }
    }
}
