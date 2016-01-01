package org.galobis.hanzi.infrastructure;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;

import org.galobis.hanzi.domain.model.Script;
import org.galobis.hanzi.domain.model.Text;
import org.junit.Test;

public class DatabaseTextFactoryTest {
    @Test
    public void should_return_UNKNOWN_for_non_Chinese_text() {
        asList(null, "", " ", "\t", "\n", "ABC", "。", ".", "робот", "robô",
                "1234", "ロボット", "로봇", "ρομπότ", "रोबोट").stream().forEach(
                        input -> assertThat(new DatabaseTextFactory().textFrom(input),
                                is(sameBeanAs(new Text(input, Script.UNKNOWN)))));
    }
}
