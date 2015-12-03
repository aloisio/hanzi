package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;

public class BatchUnihanVisitor implements UnihanVisitor {

    private static final int DEFAULT_BATCH_SIZE = 8192;

    private final PreparedStatement statement;

    private final int batchSize;

    private final Connection connection;

    private int batchCount = 0;

    public BatchUnihanVisitor(Connection connection) throws Exception {
        this(connection, DEFAULT_BATCH_SIZE);
    }

    BatchUnihanVisitor(Connection connection, int batchSize) throws Exception {
        this.batchSize = batchSize;
        this.connection = connection;
        statement = connection
                .prepareStatement("INSERT INTO hanzi(codepoint, definition) VALUES (?, ?)");
    }

    @Override
    public void visit(Hanzi hanzi) throws Exception {
        statement.setInt(1, hanzi.codePoint());
        statement.setString(2, hanzi.defintion());
        statement.addBatch();
        batchCount++;
        if ((batchCount % batchSize) == 0) {
            statement.executeBatch();
            connection.commit();
        }
    }

    @Override
    public void close() throws Exception {
        if ((batchCount % batchSize) != 0) {
            statement.executeBatch();
            connection.commit();
        }
        statement.close();
    }
}
