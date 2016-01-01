package org.galobis.hanzi.database.unihan;

import static org.galobis.hanzi.database.util.BatchCountingStatementHandler.countBatches;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.database.util.BatchCountingStatementHandler.BatchCountingStatement;
import org.galobis.hanzi.domain.model.Hanzi;

public abstract class BatchUnihanVisitor implements UnihanVisitor {

    private static final int DEFAULT_BATCH_SIZE = 8192;

    private final int batchSize;

    private final Connection connection;

    private final BatchCountingStatement statement;

    public BatchUnihanVisitor(Connection connection) throws Exception {
        this(connection, DEFAULT_BATCH_SIZE);
    }

    BatchUnihanVisitor(Connection connection, int batchSize) throws Exception {
        this.batchSize = batchSize;
        this.connection = connection;
        statement = countBatches(connection.prepareStatement(getSQL()));
    }

    @Override
    public void close() throws Exception {
        if ((statement.getBatchCount() % batchSize) != 0) {
            statement.executeBatch();
            connection.commit();
        }
        statement.close();
    }

    @Override
    public void visit(Hanzi hanzi) throws Exception {
        addBatches(statement, hanzi);
        if ((statement.getBatchCount() % batchSize) == 0) {
            statement.executeBatch();
            connection.commit();
        }
    }

    protected abstract String getSQL();

    protected abstract void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception;
}
