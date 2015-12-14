package org.galobis.hanzi.database.unihan;

import static org.galobis.hanzi.database.unihan.UnihanConstants.CHARACTER;
import static org.galobis.hanzi.database.unihan.UnihanConstants.CODE_POINT;
import static org.galobis.hanzi.database.unihan.UnihanConstants.DEFINITION;
import static org.galobis.hanzi.database.unihan.UnihanConstants.HANYU_PINLU;
import static org.galobis.hanzi.database.unihan.UnihanConstants.HANYU_PINYIN;
import static org.galobis.hanzi.database.unihan.UnihanConstants.MANDARIN;
import static org.galobis.hanzi.database.unihan.UnihanConstants.XHC1983;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipInputStream;

import org.galobis.hanzi.model.Hanzi;
import org.galobis.hanzi.model.Pinyin;
import org.galobis.hanzi.util.BordaCounter;

public class UnihanReader {
    private final UnihanVisitor visitor;

    private final InputStream input;

    private final ReadingParser readingParser = new ReadingParser();

    private final BordaCounter<Pinyin> readingCounter = new BordaCounter<>();

    public UnihanReader(UnihanVisitor visitor) throws IOException {
        this(openZipFile(), visitor);
    }

    UnihanReader(InputStream input, UnihanVisitor visitor) {
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
                            .build();
                    visitor.visit(hanzi);
                }
                streamReader.next();
            }
            visitor.close();
        }
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
