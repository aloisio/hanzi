package org.galobis.hanzi.database.ideogram.frequency;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.hasItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.galobis.hanzi.database.HanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;

public class SimplifiedRankReaderTest {

    @Injectable
    private HanziVisitor visitor;

    @Test
    public void should_visit_all_codePoints_in_raw_data_source() throws Exception {
        List<Hanzi> expected = Arrays.asList(
                new Hanzi.Builder("的").simplifiedRank(1).build(),
                new Hanzi.Builder("狐").simplifiedRank(2321).build(),
                new Hanzi.Builder("鴒").simplifiedRank(9933).build());
        final List<Hanzi> values = new ArrayList<>();
        new Expectations() {
            {
                visitor.visit(withCapture(values));
            }
        };
        new SimplifiedRankReader(visitor).read();
        expected.forEach(e -> assertThat(values, hasItem(sameBeanAs(e))));
        new Verifications() {
            {
                visitor.close();
            }
        };
    }
}
