package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.domain.model.Hanzi;
import org.galobis.hanzi.domain.model.Pinyin;

public class ReadingBatchInsertVisitor extends BatchUnihanVisitor {

    public ReadingBatchInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    protected String getSQL() {
        return "INSERT INTO reading(codepoint, pinyin_id, ordinal) "
                + "SELECT ?, id, ? FROM pinyin WHERE syllable=? AND tone=?";
    }

    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
        int ordinal = 0;
        for (Pinyin pinyin : hanzi.readings()) {
            statement.setInt(1, hanzi.codePoint());
            statement.setInt(2, ordinal++);
            statement.setString(3, pinyin.syllable());
            statement.setInt(4, pinyin.tone().number());
            statement.addBatch();
        }
    }
}
