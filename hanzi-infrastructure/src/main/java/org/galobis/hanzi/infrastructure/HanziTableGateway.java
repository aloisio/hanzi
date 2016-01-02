package org.galobis.hanzi.infrastructure;

import static org.galobis.hanzi.database.DatabaseUtilities.DATABASE_URL;
import static org.galobis.hanzi.database.DatabaseUtilities.ERROR_STREAM_FIELD_PROPERTY_VALUE;
import static org.galobis.hanzi.database.DatabaseUtilities.ERROR_STREAM_FIELD_PROPERTY_KEY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.galobis.hanzi.database.DatabaseUtilities.Columns;
import org.galobis.hanzi.database.DatabaseUtilities.Tables;

public class HanziTableGateway {

    private static final String INTERSECT_PATTERN = "%s INTERSECT %s";

    private static final String SELECT_PATTERN = String.format(
            "SELECT h.%2$s FROM %1$s h LEFT JOIN %%s x ON h.%2$s = x.%2$s WHERE x.%2$s IS %%s NULL",
            Tables.HANZI, Columns.CODEPOINT);

    private static final String SQL_SIMPLIFIED = String.format(INTERSECT_PATTERN,
            String.format(SELECT_PATTERN, Tables.TRADITIONAL, "NOT"),
            String.format(SELECT_PATTERN, Tables.SIMPLIFIED, ""));

    public Set<Integer> getSimpifiedOnlyCodepoints() {
        try (Connection connection = getConnection()) {
            return asSet(connection, SQL_SIMPLIFIED, Integer.class);
        } catch (SQLException exc) {
            throw new InfrastructureException(exc);
        }
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
}
