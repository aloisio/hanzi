package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.galobis.hanzi.database.BatchHanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;

public class HanziTableInsertVisitor extends BatchHanziVisitor {

    public HanziTableInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    protected String getSQL() {
        return "INSERT INTO hanzi(codepoint, definition) VALUES (?, ?)";
    }

    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws SQLException {
        statement.setInt(1, hanzi.codePoint());
        statement.setString(2, hanzi.definition());
        statement.addBatch();
    }
}
