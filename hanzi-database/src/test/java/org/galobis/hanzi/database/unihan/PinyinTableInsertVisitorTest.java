package org.galobis.hanzi.database.unihan;

import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.galobis.hanzi.database.HanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;

public class PinyinTableInsertVisitorTest {

    @Injectable
    private Connection connection;

    @Injectable
    private PreparedStatement statement;

    private HanziVisitor visitor;

    @Before
    public void createVisitor() throws Exception {
        new Expectations() {
            {
                connection.prepareStatement(withArgThat(containsString("pinyin")));
                result = statement;
            }
        };

        visitor = new PinyinTableInsertVisitor(connection);
    }

    @Test
    public void should_insert_each_pinyin_only_once() throws Exception {
        visitor.visit(new Hanzi.Builder(0x4FBF).readings("bian4", "pian2").build());
        visitor.visit(new Hanzi.Builder(0x53D8).readings("bian4").build());
        new Verifications() {
            {
                statement.setString(1, "bian");
                statement.setString(1, "pian");
                statement.addBatch();
                times = 2;
            }
        };
    }
}
