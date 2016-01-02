package org.galobis.hanzi.infrastructure;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
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
                                is(equalTo(new Text(input, Script.UNKNOWN)))));
    }

    @Test
    public void should_return_SIMPLIFIED_for_Simplified_Chinese_text() {
        String input = "人人生而自由，在尊严和权利上一律平等。他们赋有理性和良心，并应以兄弟关系的精神互相对待。";
        assertThat(new DatabaseTextFactory().textFrom(input),
                is(equalTo(new Text(input, Script.SIMPLIFIED))));
    }

    @Test
    public void should_return_TRADITIONAL_for_Traditional_Chinese_text() {
        String input = "人人生而自由﹐在尊嚴和權利上一律平等。他們賦有理性和良心﹐並應以兄弟關係的精神互相對待。";
        assertThat(new DatabaseTextFactory().textFrom(input),
                is(equalTo(new Text(input, Script.TRADITIONAL))));
    }
}
