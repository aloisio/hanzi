package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.galobis.hanzi.database.BatchHanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;

public class SimplifiedTableInsertVisitor extends BatchHanziVisitor {

    public SimplifiedTableInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO simplified(codepoint, simplified) VALUES (?,?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws SQLException {
        for (Hanzi simplified : hanzi.simplified()) {
            statement.setInt(1, hanzi.codePoint());
            statement.setInt(2, simplified.codePoint());
            statement.addBatch();
        }
    }

}
