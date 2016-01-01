package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.domain.model.Hanzi;

public class HanziBatchInsertVisitor extends BatchUnihanVisitor {

    public HanziBatchInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    protected String getSQL() {
        return "INSERT INTO hanzi(codepoint, definition) VALUES (?, ?)";
    }

    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
        statement.setInt(1, hanzi.codePoint());
        statement.setString(2, hanzi.definition());
        statement.addBatch();
    }
}
