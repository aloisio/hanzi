package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.galobis.hanzi.database.BatchHanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;
import org.galobis.hanzi.domain.model.Pinyin;

public class PinyinTableInsertVisitor extends BatchHanziVisitor {

    private final Set<Pinyin> inserted = new HashSet<>();

    public PinyinTableInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO pinyin(syllable, tone) VALUES (?, ?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws SQLException {
        for (Pinyin pinyin : hanzi.readings()) {
            if (!inserted.contains(pinyin)) {
                statement.setString(1, pinyin.syllable());
                statement.setInt(2, pinyin.tone().number());
                statement.addBatch();
                inserted.add(pinyin);
            }
        }
    }
}
