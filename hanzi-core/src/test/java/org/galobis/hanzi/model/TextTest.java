package org.galobis.hanzi.model;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class TextTest {

    @Test
    public void should_return_UNKNOWN_for_non_Chinese_text() {
        asList(null, "", " ", "\t", "\n", "ABC", "。", ".", "робот", "robô",
                "1234", "ロボット", "로봇", "ρομπότ", "रोबोट").stream().forEach(
                        input -> assertThat(new Text(input).script(),
                                is(equalTo(Script.UNKNOWN))));
    }
}
