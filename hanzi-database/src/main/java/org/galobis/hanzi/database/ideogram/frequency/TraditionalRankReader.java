package org.galobis.hanzi.database.ideogram.frequency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

import org.galobis.hanzi.database.HanziVisitor;
import org.galobis.hanzi.database.ideogram.UnihanReader;
import org.galobis.hanzi.domain.model.Hanzi;

public class TraditionalRankReader {
    private static final Charset CHARSET = Charset.forName("BIG5");

    private static final int LINES_TO_SKIP = 8;

    private final HanziVisitor visitor;

    public TraditionalRankReader(HanziVisitor visitor) {
        this.visitor = visitor;
    }

    public void read() throws IOException {
        try (BufferedReader reader = new BufferedReader(openZipFile())) {
            for (int i = 0; i < LINES_TO_SKIP; i++) {
                reader.readLine();
            }
            int rank = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), rank++) {
                visitor.visit(new Hanzi.Builder(line.trim().codePointAt(0)).traditionalRank(rank).build());
            }
        } finally {
            visitor.close();
        }
    }

    private static Reader openZipFile() throws IOException {
        ZipInputStream input = new ZipInputStream(
                UnihanReader.class.getResourceAsStream("/raw/sorted.zip"));
        input.getNextEntry();
        return new InputStreamReader(input, CHARSET);
    }
}
