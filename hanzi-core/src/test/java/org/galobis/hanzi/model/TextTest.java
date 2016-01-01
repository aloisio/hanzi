package org.galobis.hanzi.model;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class TextTest {

    @Test
    public void should_return_UNKNOWN_for_null_String() {
        assertThat(new Text(null).script(), is(equalTo(Script.UNKNOWN)));
    }
}
