package org.galobis.hanzi.database.ideogram;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.galobis.hanzi.database.HanziVisitor;
import org.galobis.hanzi.domain.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;

/**
 * Test coverage of the Unihan repertoire of The Unicode Standard:
 * 
 * <ul>
 * <li>CJK Unified Ideographs (4E00–9FD5)</li>
 * <li>CJK Unified Ideographs Extension A (3400–4DB5)</li>
 * <li>CJK Unified Ideographs Extension B (20000–2A6D6)</li>
 * <li>CJK Unified Ideographs Extension C (2A700–2B734)</li>
 * <li>CJK Unified Ideographs Extension D (2B740–2B81D)</li>
 * <li>CJK Unified Ideographs Extension E (2B820–2CEA1)</li>
 * <li>CJK Compatibility Ideographs Supplement (2F800–2FA1D)</li>
 * <li>CJK Compatibility Ideographs (F900–FAD9)</li>
 * </ul>
 */
public class UnihanReaderTest {

    private UnihanReader reader;

    @Injectable
    private HanziVisitor mockedVisitor;

    private List<Hanzi> actualHanzi = new ArrayList<>();

    private static final List<Hanzi> EXPECTED_HANZI = Arrays.asList(
            new Hanzi.Builder(0x3400)
                    .definition("(same as U+4E18 丘) hillock or mound")
                    .readings("qiu1")
                    .build(),
            new Hanzi.Builder(0x346F)
                    .definition("last name, all; together; mutually, "
                            + "a low rank officer to take charge of the bandits in ancient time")
                    .readings("xu3")
                    .simplified(0x3454)
                    .build(),
            new Hanzi.Builder(0x4DB5)
                    .definition("(same as U+7B8E 箎) a bamboo flute with seven holes")
                    .readings("chi2")
                    .build(),
            new Hanzi.Builder(0x4E00)
                    .definition("one; a, an; alone")
                    .readings("yi1", "yi2", "yi4")
                    .build(),
            new Hanzi.Builder(0x540E)
                    .definition("queen, empress, sovereign; (simp. for 後) behind, rear, after")
                    .readings("hou4")
                    .simplified(0x540E)
                    .traditional(0x540E, 0x5F8C)
                    .build(),
            new Hanzi.Builder(0x6F22)
                    .definition("the Chinese people, Chinese language")
                    .readings("han4", "tan1")
                    .simplified(0x6C49)
                    .build(),
            new Hanzi.Builder(0x9F4A)
                    .definition("even, uniform, of equal length")
                    .readings("qi2", "ji4", "zhai1", "ji1", "zi1", "jian3")
                    .simplified(0x9F50)
                    .build(),
            new Hanzi.Builder(0x9FD5)
                    .definition("Danio chrysotaeniata")
                    .readings("dan1")
                    .build(),
            new Hanzi.Builder(0xF900).definition("how? what?").build(),
            new Hanzi.Builder(0xFAD9).build(),
            new Hanzi.Builder(0x20000)
                    .definition("the sound made by breathing in; oh! "
                            + "(cf. U+311B BOPOMOFO LETTER O, which is derived from this character)")
                    .readings("he1")
                    .build(),
            new Hanzi.Builder(0x2A6D6).build(),
            new Hanzi.Builder(0x2A700).build(),
            new Hanzi.Builder(0x2B734).build(),
            new Hanzi.Builder(0x2B740).build(),
            new Hanzi.Builder(0x2B81D).build(),
            new Hanzi.Builder(0x2B820).build(),
            new Hanzi.Builder(0x2CEA1).build(),
            new Hanzi.Builder(0x2F800).build(),
            new Hanzi.Builder(0x2FA1D).build());

    @Before
    public void initializeVisitor() throws Exception {
        new Expectations() {
            {
                mockedVisitor.visit(withCapture(actualHanzi));
            }
        };
        reader = new UnihanReader(mockedVisitor);
    }

    @Test
    public void should_read_all_Unihan_blocks() throws Exception {
        reader.read();

        new Verifications() {
            {
                mockedVisitor.close();
            }
        };

        Map<Hanzi, Hanzi> actual = new HashMap<>();
        actualHanzi.forEach(h -> actual.put(h, h));
        for (Hanzi expected : EXPECTED_HANZI) {
            assertThat(actual.get(expected), is(sameBeanAs(expected)));
        }
    }
}