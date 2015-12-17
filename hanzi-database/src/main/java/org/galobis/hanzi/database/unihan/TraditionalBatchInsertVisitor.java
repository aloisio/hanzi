package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;

public class TraditionalBatchInsertVisitor extends BatchUnihanVisitor {

    public TraditionalBatchInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO traditional(codepoint, traditional) VALUES (?,?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
        for (Hanzi traditional : hanzi.traditional()) {
            statement.setInt(1, hanzi.codePoint());
            statement.setInt(2, traditional.codePoint());
            statement.addBatch();
        }
    }

}
