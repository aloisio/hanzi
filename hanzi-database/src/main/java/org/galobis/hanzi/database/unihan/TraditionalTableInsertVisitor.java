package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.galobis.hanzi.database.BatchHanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;

public class TraditionalTableInsertVisitor extends BatchHanziVisitor {

    public TraditionalTableInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO traditional(codepoint, traditional) VALUES (?,?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws SQLException {
        for (Hanzi traditional : hanzi.traditional()) {
            statement.setInt(1, hanzi.codePoint());
            statement.setInt(2, traditional.codePoint());
            statement.addBatch();
        }
    }

}
