package org.galobis.hanzi.database;

import static org.galobis.hanzi.database.util.BatchCountingStatementHandler.countBatches;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.galobis.hanzi.database.util.BatchCountingStatementHandler.BatchCountingStatement;
import org.galobis.hanzi.domain.model.Hanzi;

public abstract class BatchHanziVisitor implements HanziVisitor {

    private static final int DEFAULT_BATCH_SIZE = 8192;

    private final int batchSize;

    private final Connection connection;

    private final BatchCountingStatement statement;

    public BatchHanziVisitor(Connection connection) throws Exception {
        this(connection, DEFAULT_BATCH_SIZE);
    }

    BatchHanziVisitor(Connection connection, int batchSize) throws Exception {
        this.batchSize = batchSize;
        this.connection = connection;
        statement = countBatches(connection.prepareStatement(getSQL()));
    }

    @Override
    public void close() {
        try {
            if ((statement.getBatchCount() % batchSize) != 0) {
                statement.executeBatch();
                connection.commit();
            }
            statement.close();
        } catch (SQLException exc) {
            throw new DatabaseException(exc);
        }
    }

    @Override
    public final void visit(Hanzi hanzi) {
        try {
            addBatches(statement, hanzi);
            if ((statement.getBatchCount() % batchSize) == 0) {
                statement.executeBatch();
                connection.commit();
            }
        } catch (SQLException exc) {
            throw new DatabaseException(exc);
        }
    }

    protected abstract String getSQL();

    protected abstract void addBatches(PreparedStatement statement, Hanzi hanzi) throws SQLException;
}
