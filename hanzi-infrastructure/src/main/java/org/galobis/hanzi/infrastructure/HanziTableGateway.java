package org.galobis.hanzi.infrastructure;

import static org.galobis.hanzi.database.DatabaseConstants.DATABASE_URL;
import static org.galobis.hanzi.database.DatabaseConstants.ERROR_STREAM_FIELD_PROPERTY_KEY;
import static org.galobis.hanzi.database.DatabaseConstants.ERROR_STREAM_FIELD_PROPERTY_VALUE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.galobis.hanzi.database.DatabaseConstants.Columns;
import org.galobis.hanzi.database.DatabaseConstants.Tables;

public class HanziTableGateway {

    private static final String INTERSECT_PATTERN = "%s INTERSECT %s";

    private static final String SELECT_PATTERN = String.format(
            "SELECT h.%2$s FROM %1$s h LEFT JOIN %%s x ON h.%2$s = x.%2$s WHERE x.%2$s IS %%s NULL",
            Tables.HANZI, Columns.CODEPOINT);

    private static final String SQL_SIMPLIFIED = String.format(INTERSECT_PATTERN,
            String.format(SELECT_PATTERN, Tables.TRADITIONAL, "NOT"),
            String.format(SELECT_PATTERN, Tables.SIMPLIFIED, ""));

    private static final String SQL_TRADITIONAL = String.format(INTERSECT_PATTERN,
            String.format(SELECT_PATTERN, Tables.TRADITIONAL, ""),
            String.format(SELECT_PATTERN, Tables.SIMPLIFIED, "NOT"));

    public Set<Integer> getSimplifiedOnlyCodePoints() {
        return retrieve(SQL_SIMPLIFIED);
    }

    public Set<Integer> getTraditionalOnlyCodePoints() {
        return retrieve(SQL_TRADITIONAL);
    }

    private <T> Set<T> asSet(Connection connection, String sql, Class<T> type) throws SQLException {
        Set<T> result = new LinkedHashSet<>();
        try (ResultSet resultSet = connection.prepareCall(sql).executeQuery()) {
            while (resultSet.next()) {
                result.add(resultSet.getObject(1, type));
            }
        }
        return result;
    }

    private Connection getConnection() throws SQLException {
        System.setProperty(ERROR_STREAM_FIELD_PROPERTY_KEY, ERROR_STREAM_FIELD_PROPERTY_VALUE);
        DriverManager.registerDriver(new EmbeddedDriver());
        return DriverManager.getConnection(DATABASE_URL);
    }

    private Set<Integer> retrieve(String sql) {
        try (Connection connection = getConnection()) {
            return asSet(connection, sql, Integer.class);
        } catch (SQLException exc) {
            throw new InfrastructureException(exc);
        }
    }
}
