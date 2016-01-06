package org.galobis.hanzi.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import org.junit.Test;

public class HanziTableGatewayTest {

    @Test
    public void should_return_set_of_codepoints_which_only_appear_in_Simplified_Chinese() {
        assertThat(new HanziTableGateway().getSimplifiedOnlyCodePoints(),
                hasItems(0x5B66, 0x5BF9));
    }

    @Test
    public void should_return_set_of_codepoints_which_only_appear_in_Traditional_Chinese() {
        assertThat(new HanziTableGateway().getTraditionalOnlyCodePoints(),
                hasItems(0x5B78, 0x5C0D));
    }
}
