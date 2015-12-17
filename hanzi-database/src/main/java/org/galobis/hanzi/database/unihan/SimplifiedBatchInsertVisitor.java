package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;

public class SimplifiedBatchInsertVisitor extends BatchUnihanVisitor {

    public SimplifiedBatchInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO simplified(codepoint, simplified, ordinal) VALUES (?,?,?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
        int ordinal = 0;
        for (Hanzi simplified : hanzi.simplified()) {
            statement.setInt(1, hanzi.codePoint());
            statement.setInt(2, simplified.codePoint());
            statement.setInt(3, ordinal++);
            statement.addBatch();
        }
    }

}
