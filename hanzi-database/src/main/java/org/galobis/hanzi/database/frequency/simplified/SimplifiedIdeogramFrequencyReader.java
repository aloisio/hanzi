package org.galobis.hanzi.database.frequency.simplified;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.galobis.hanzi.domain.model.Hanzi;

public class SimplifiedIdeogramFrequencyReader {
    private static final Charset CHARSET = Charset.forName("GBK");

    private static final String COMMENT_PREFIX = "/*";

    private static final String FILE = "/raw/CharFreq.txt";

    private static final int IDEOGRAM_INDEX = 1;

    private static final int RANK_INDEX = 0;

    private static final String SEPARATOR = "\\t";

    private final SimplifiedIdeogramFrequencyVisitor visitor;

    public SimplifiedIdeogramFrequencyReader(SimplifiedIdeogramFrequencyVisitor visitor) {
        this.visitor = visitor;
    }

    public void read() throws IOException {
        try (BufferedReader reader = new BufferedReader(openRawDataFile())) {
            reader.lines()
                    .filter(line -> !line.startsWith(COMMENT_PREFIX))
                    .forEach(line -> visitor.visit(buildHanzi(line.split(SEPARATOR))));
        } finally {
            visitor.close();
        }
    }

    private Hanzi buildHanzi(String[] line) {
        return new Hanzi.Builder(getIdeogram(line))
                .simplifiedRank(getRank(line))
                .build();
    }

    private String getIdeogram(String[] line) {
        return line[IDEOGRAM_INDEX];
    }

    private Integer getRank(String[] line) {
        return Integer.valueOf(line[RANK_INDEX]);
    }

    private Reader openRawDataFile() {
        return new InputStreamReader(
                getClass().getResourceAsStream(FILE), CHARSET);
    }
}
