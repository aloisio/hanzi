package org.galobis.hanzi.database.util;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.galobis.hanzi.database.util.BatchCountingStatementHandler.countBatches;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.sql.PreparedStatement;

import org.galobis.hanzi.database.util.BatchCountingStatementHandler.BatchCountingStatement;
import org.junit.Before;
import org.junit.Test;

import mockit.Injectable;
import mockit.Verifications;

public class BatchCountingStatementHandlerTest {

    @Injectable
    private PreparedStatement delegate;

    private BatchCountingStatement statement;

    @Before
    public void createProxy() {
        statement = countBatches(delegate);
    }

    @Test
    public void should_send_through_addBatch_calls() throws Exception {
        statement.addBatch();
        new Verifications() {
            {
                delegate.addBatch();
            }
        };
    }

    @Test
    public void should_count_addBatch_calls() throws Exception {
        assertThat(statement.getBatchCount(), is(equalTo(0)));
        statement.addBatch();
        assertThat(statement.getBatchCount(), is(equalTo(1)));
        statement.addBatch();
        assertThat(statement.getBatchCount(), is(equalTo(2)));
    }
}
