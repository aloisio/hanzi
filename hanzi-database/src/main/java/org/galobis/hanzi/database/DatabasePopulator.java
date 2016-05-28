package org.galobis.hanzi.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.galobis.hanzi.database.ideogram.HanziTableInsertVisitor;
import org.galobis.hanzi.database.ideogram.PinyinTableInsertVisitor;
import org.galobis.hanzi.database.ideogram.ReadingTableInsertVisitor;
import org.galobis.hanzi.database.ideogram.SimplifiedTableInsertVisitor;
import org.galobis.hanzi.database.ideogram.TraditionalTableInsertVisitor;
import org.galobis.hanzi.database.ideogram.UnihanReader;
import org.galobis.hanzi.database.ideogram.frequency.HanziTableSimplifiedRankUpdateVisitor;
import org.galobis.hanzi.database.ideogram.frequency.SimplifiedRankReader;

public class DatabasePopulator {
    private static final String[] DDL_STATEMENTS = {
            "CREATE TABLE hanzi ("
                    + "codepoint INTEGER NOT NULL, definition LONG VARCHAR, "
                    + "simplified_rank INTEGER, "
                    + "PRIMARY KEY (codepoint))",
            "CREATE TABLE pinyin ("
                    + "id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "syllable VARCHAR(10) NOT NULL, tone INTEGER NOT NULL, "
                    + "PRIMARY KEY (id), UNIQUE(syllable, tone))",
            "CREATE TABLE reading ("
                    + "id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "codepoint INTEGER NOT NULL, pinyin_id INTEGER NOT NULL, ordinal INTEGER NOT NULL, "
                    + "PRIMARY KEY (id), UNIQUE(codepoint, pinyin_id, ordinal), "
                    + "FOREIGN KEY (codepoint) REFERENCES hanzi (codepoint), "
                    + "FOREIGN KEY (pinyin_id) REFERENCES pinyin (id))",
            "CREATE TABLE simplified ("
                    + "id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "codepoint INTEGER NOT NULL, simplified INTEGER NOT NULL, "
                    + "PRIMARY KEY (id), UNIQUE(codepoint, simplified), "
                    + "FOREIGN KEY (codepoint) REFERENCES hanzi (codepoint), "
                    + "FOREIGN KEY (simplified) REFERENCES hanzi (codepoint))",
            "CREATE TABLE traditional ("
                    + "id INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                    + "codepoint INTEGER NOT NULL, traditional INTEGER NOT NULL, "
                    + "PRIMARY KEY (id), UNIQUE(codepoint, traditional), "
                    + "FOREIGN KEY (codepoint) REFERENCES hanzi (codepoint), "
                    + "FOREIGN KEY (traditional) REFERENCES hanzi (codepoint))"
    };

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String connectionURL = args[0];
            DriverManager.registerDriver(new EmbeddedDriver());
            try (Connection connection = DriverManager.getConnection(connectionURL + ";create=true")) {
                createTables(connection);
                connection.setAutoCommit(false);
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
        new UnihanReader(new CompositeHanziVisitor(
                new HanziTableInsertVisitor(connection),
                new PinyinTableInsertVisitor(connection))).read();
        new UnihanReader(new CompositeHanziVisitor(
                new ReadingTableInsertVisitor(connection),
                new SimplifiedTableInsertVisitor(connection),
                new TraditionalTableInsertVisitor(connection))).read();
        new SimplifiedRankReader(new HanziTableSimplifiedRankUpdateVisitor(connection)).read();
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
