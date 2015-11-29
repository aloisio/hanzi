package org.galobis.hanzi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.galobis.hanzi.database.unihan.UnihanVisitor;
import org.galobis.hanzi.database.unihan.UnihanReader;
import org.galobis.hanzi.model.Hanzi;

public class DatabasePopulator {
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String connectionURL = args[0];
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            try (Connection connection = DriverManager.getConnection(connectionURL)) {
                new UnihanReader(new UnihanVisitor() {

                    @Override
                    public void visit(Hanzi hanzi) throws SQLException {
                        String sql = String.format("insert into hanzi(codepoint) values(%s)", hanzi.codePoint());
                        connection.prepareCall(sql).executeUpdate();
                    }
                }).read();
            }
        }
    }
}
