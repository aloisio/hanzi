package org.galobis.hanzi.util;

import static org.galobis.hanzi.util.UnicodeUtilities.composition;
import static org.galobis.hanzi.util.UnicodeUtilities.decomposition;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnicodeUtilitiesTest {

    @Test
    public void decomposition_should_break_down_all_combining_diacritics() throws Exception {
        assertEquals("Lu\u0308\u030C", decomposition("Lǚ"));
        assertEquals("e\u0302\u0304", decomposition("ê̄"));
        assertEquals("bia\u0300n", decomposition("biàn"));
    }

    @Test
    public void composition_should_use_composite_codepoints_as_much_as_possible() throws Exception {
        assertEquals("Lǚ", composition("Lǚ"));
        assertEquals("Lǚ", composition("Lu\u0308\u030C"));
        assertEquals("ê\u0304", composition("ê̄"));
        assertEquals("ê\u0304", composition("e\u0302\u0304"));
        assertEquals("biàn", composition("bia\u0300n"));
    }
}
