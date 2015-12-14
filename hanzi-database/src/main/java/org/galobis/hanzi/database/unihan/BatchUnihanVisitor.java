package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.model.Hanzi;

public abstract class BatchUnihanVisitor implements UnihanVisitor {

    private static final int DEFAULT_BATCH_SIZE = 8192;

    private int batchCount = 0;

    private final int batchSize;

    private final Connection connection;

    private final PreparedStatement statement;

    public BatchUnihanVisitor(Connection connection) throws Exception {
        this(connection, DEFAULT_BATCH_SIZE);
    }

    BatchUnihanVisitor(Connection connection, int batchSize) throws Exception {
        this.batchSize = batchSize;
        this.connection = connection;
        statement = connection.prepareStatement(getSQL());
    }

    @Override
    public void close() throws Exception {
        if ((batchCount % batchSize) != 0) {
            statement.executeBatch();
            connection.commit();
        }
        statement.close();
    }

    @Override
    public void visit(Hanzi hanzi) throws Exception {
        setParameters(statement, hanzi);
        statement.addBatch();
        batchCount++;
        if ((batchCount % batchSize) == 0) {
            statement.executeBatch();
            connection.commit();
        }
    }

    protected abstract String getSQL();

    protected abstract void setParameters(PreparedStatement statement, Hanzi hanzi) throws Exception;
}
