package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.domain.model.Hanzi;

public class SimplifiedBatchInsertVisitor extends BatchUnihanVisitor {

    public SimplifiedBatchInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO simplified(codepoint, simplified) VALUES (?,?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
        for (Hanzi simplified : hanzi.simplified()) {
            statement.setInt(1, hanzi.codePoint());
            statement.setInt(2, simplified.codePoint());
            statement.addBatch();
        }
    }

}
