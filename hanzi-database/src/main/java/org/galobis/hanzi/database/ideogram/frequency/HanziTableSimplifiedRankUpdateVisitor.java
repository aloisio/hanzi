package org.galobis.hanzi.database.ideogram.frequency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.galobis.hanzi.database.BatchHanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;

public class HanziTableSimplifiedRankUpdateVisitor extends BatchHanziVisitor {

    public HanziTableSimplifiedRankUpdateVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "UPDATE hanzi SET simplified_rank = ? WHERE codepoint = ?";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws SQLException {
        statement.setInt(1, hanzi.simplifiedRank());
        statement.setInt(2, hanzi.codePoint());
        statement.addBatch();
    }
}
