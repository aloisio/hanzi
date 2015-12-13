package org.galobis.hanzi.database.unihan;

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

    private static final String XHC1983 = "kXHC1983";

    private static final String HANYU_PINYIN = "kHanyuPinyin";

    private static final String HANYU_PINLU = "kHanyuPinlu";

    private static final String MANDARIN = "kMandarin";

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
                if (streamReader.isStartElement() && streamReader.getLocalName().equals("char")) {
                    Integer codePoint = Integer.valueOf(streamReader.getAttributeValue(null, "cp"), 16);
                    String definition = streamReader.getAttributeValue(null, "kDefinition");
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
