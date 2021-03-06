package org.galobis.hanzi.database.ideogram;

import static org.galobis.hanzi.database.ideogram.UnihanConstants.CHARACTER;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.CODE_POINT;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.DEFINITION;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.HANYU_PINLU;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.HANYU_PINYIN;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.MANDARIN;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.SIMPLIFIED;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.TRADITIONAL;
import static org.galobis.hanzi.database.ideogram.UnihanConstants.XHC1983;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipInputStream;

import org.galobis.hanzi.database.HanziVisitor;
import org.galobis.hanzi.database.util.BordaCounter;
import org.galobis.hanzi.domain.model.Hanzi;
import org.galobis.hanzi.domain.model.Pinyin;

public class UnihanReader {
    private final HanziVisitor visitor;

    private final InputStream input;

    private final ReadingParser readingParser = new ReadingParser();

    private final BordaCounter<Pinyin> readingCounter = new BordaCounter<>();

    public UnihanReader(HanziVisitor visitor) throws IOException {
        this(openZipFile(), visitor);
    }

    UnihanReader(InputStream input, HanziVisitor visitor) {
        this.input = input;
        this.visitor = visitor;
    }

    public void read() throws Exception {
        try (UnihanXMLStreamReader streamReader = new UnihanXMLStreamReader(input)) {
            while (streamReader.hasNext()) {
                if (streamReader.isStartElement() && streamReader.getLocalName().equals(CHARACTER)) {
                    Integer codePoint = Integer.valueOf(streamReader.getAttributeValue(null, CODE_POINT), 16);
                    String definition = streamReader.getAttributeValue(null, DEFINITION);
                    Hanzi hanzi = new Hanzi.Builder(codePoint)
                            .definition(definition)
                            .readings(getReadings(streamReader))
                            .simplified(getVariant(streamReader, SIMPLIFIED))
                            .traditional(getVariant(streamReader, TRADITIONAL))
                            .build();
                    visitor.visit(hanzi);
                }
                streamReader.next();
            }
            visitor.close();
        }

    }

    private Hanzi[] getVariant(UnihanXMLStreamReader streamReader, String attribute) {
        return Arrays.stream(Optional.ofNullable(streamReader.getAttributeValue(null, attribute))
                .orElse("").replaceAll("U\\+", "").split(" "))
                .filter(s -> !s.isEmpty())
                .map(s -> Integer.valueOf(s, 16))
                .map(c -> new Hanzi.Builder(c).build()).toArray(Hanzi[]::new);
    }

    private List<Pinyin> getReadings(UnihanXMLStreamReader streamReader) {
        readingCounter.reset();
        List<List<Pinyin>> rawReadings = new ArrayList<>();
        rawReadings.addAll(readingParser.parse(MANDARIN, getReading(streamReader, MANDARIN)));
        rawReadings.addAll(readingParser.parse(HANYU_PINLU, getReading(streamReader, HANYU_PINLU)));
        rawReadings.addAll(readingParser.parse(HANYU_PINYIN, getReading(streamReader, HANYU_PINYIN)));
        rawReadings.addAll(readingParser.parse(XHC1983, getReading(streamReader, XHC1983)));
        rawReadings.forEach(readingCounter::vote);
        return readingCounter.winners();
    }

    private String getReading(UnihanXMLStreamReader streamReader, String attribute) {
        return Optional.ofNullable(streamReader.getAttributeValue(null, attribute)).orElse("");
    }

    private static InputStream openZipFile() throws IOException {
        ZipInputStream input = new ZipInputStream(
                UnihanReader.class.getResourceAsStream("/raw/ucd.unihan.flat.zip"));
        input.getNextEntry();
        return input;
    }
}
