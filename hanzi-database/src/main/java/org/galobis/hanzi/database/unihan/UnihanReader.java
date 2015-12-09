package org.galobis.hanzi.database.unihan;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.galobis.hanzi.model.Hanzi;

public class UnihanReader {

    private final UnihanVisitor visitor;

    private final InputStream input;

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
                    Hanzi hanzi = new Hanzi.Builder(codePoint).definition(definition).build();
                    visitor.visit(hanzi);
                }
                streamReader.next();
            }
            visitor.close();
        }
    }

    private static InputStream openZipFile() throws IOException {
        ZipInputStream input = new ZipInputStream(
                UnihanReader.class.getResourceAsStream("/raw/ucd.unihan.flat.zip"));
        input.getNextEntry();
        return input;
    }
}
