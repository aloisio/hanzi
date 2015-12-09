package org.galobis.hanzi.database.unihan;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.galobis.hanzi.model.Hanzi;
import org.junit.Before;
import org.junit.Test;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class UnihanReaderTest {

    private UnihanReader reader;

    @Injectable
    private UnihanVisitor mockedVisitor;

    @Before
    public void initializeVisitor() throws Exception {
        new NonStrictExpectations() {
            {
                mockedVisitor.visit((Hanzi) any);
            }
        };
        reader = new UnihanReader(mockedVisitor);
    }

    /**
     * Ideographic characters assigned by Unicode appear in the following blocks
     * (some codepoints are unused):
     * <ul>
     * <li>CJK Unified Ideographs (4E00–9FFF)</li>
     * <li>CJK Unified Ideographs Extension A (3400–4DBF)</li>
     * <li>CJK Unified Ideographs Extension B (20000–2A6DF)</li>
     * <li>CJK Unified Ideographs Extension C (2A700–2B73F)</li>
     * <li>CJK Unified Ideographs Extension D (2B740–2B81F)</li>
     * <li>CJK Compatibility Ideographs (F900–FAFF)</li>
     * </ul>
     */
    @Test
    public void should_read_all_Unihan_blocks() throws Exception {
        reader.read();
        for (int codePoint : Arrays.asList(
                0x4E00, 0x9FD5, 0x3400, 0x4DB5,
                0x20000, 0x2A6D6, 0x2A700, 0x2B734,
                0x2B740, 0x2B81D, 0xF900, 0xFAD9)) {
            new Verifications() {
                {
                    mockedVisitor.visit(new Hanzi.Builder(codePoint).build());
                }
            };
        }
    }

    @Test
    public void should_close_visitor_when_finished_reading() throws Exception {
        reader.read();
        new Verifications() {
            {
                mockedVisitor.close();
            }
        };
    }

    @Test
    public void should_read_Unihan_definitions() throws Exception {
        reader.read();
        new Verifications() {
            {
                mockedVisitor.visit(withArgThat(is(sameBeanAs(
                        new Hanzi.Builder("漢".codePointAt(0))
                                .definition("the Chinese people, Chinese language")
                                .build()))));
            }
        };
    }
}