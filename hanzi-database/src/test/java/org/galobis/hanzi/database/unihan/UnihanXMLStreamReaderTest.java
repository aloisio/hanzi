package org.galobis.hanzi.database.unihan;

import java.io.InputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;

public class UnihanXMLStreamReaderTest {

    @Injectable
    private InputStream mockedInput;

    @Capturing
    private XMLStreamReader mockedXMLReader;

    @Test
    public void should_close_InputStream_and_XMLStreamReader_in_try_block() throws Exception {
        try (UnihanXMLStreamReader reader = new UnihanXMLStreamReader(mockedInput)) {
            reader.next();
        }
        new Verifications() {
            {
                mockedInput.close();
                mockedXMLReader.close();
            }
        };
    }

    @Test
    public void should_close_InputStream_in_try_block_when_error_closing_XMLStreamReader() throws Exception {
        new Expectations() {
            {
                mockedXMLReader.close();
                result = new XMLStreamException();
            }
        };

        try (UnihanXMLStreamReader reader = new UnihanXMLStreamReader(mockedInput)) {
            reader.next();
        } catch (XMLStreamException expected) {
            // Expected
        }

        new Verifications() {
            {
                mockedInput.close();
            }
        };
    }
}
