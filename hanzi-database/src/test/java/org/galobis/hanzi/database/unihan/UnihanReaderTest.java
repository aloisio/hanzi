package org.galobis.hanzi.database.unihan;

import java.util.Arrays;

import org.galobis.hanzi.model.Hanzi;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class UnihanReaderTest {

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
    public void should_read_all_Unihan_blocks(@Injectable UnihanVisitor mockedVisitor) throws Exception {
        new NonStrictExpectations() {
            {
                mockedVisitor.visit((Hanzi) any);
            }
        };
        new UnihanReader(mockedVisitor).read();
        for (int codePoint : Arrays.asList(
                0x4E00, 0x9FD5, 0x3400, 0x4DB5,
                0x20000, 0x2A6D6, 0x2A700, 0x2B734,
                0x2B740, 0x2B81D, 0xF900, 0xFAD9)) {
            new Verifications() {
                {
                    mockedVisitor.visit(new Hanzi(codePoint));
                }
            };
        }
    }
}