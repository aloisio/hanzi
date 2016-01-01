package org.galobis.hanzi.database.unihan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

import org.galobis.hanzi.domain.model.Hanzi;
import org.galobis.hanzi.domain.model.Pinyin;

public class PinyinBatchInsertVisitor extends BatchUnihanVisitor {

    private final Set<Pinyin> inserted = new HashSet<>();

    public PinyinBatchInsertVisitor(Connection connection) throws Exception {
        super(connection);
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO pinyin(syllable, tone) VALUES (?, ?)";
    }

    @Override
    protected void addBatches(PreparedStatement statement, Hanzi hanzi) throws Exception {
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
