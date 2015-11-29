package org.galobis.hanzi.database.unihan;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

class UnihanXMLStreamReader implements AutoCloseable {
    private final InputStream input;

    private final XMLStreamReader delegate;

    UnihanXMLStreamReader(InputStream input) throws FactoryConfigurationError, XMLStreamException {
        this.input = input;
        this.delegate = XMLInputFactory.newInstance().createXMLStreamReader(input);
    }

    public boolean hasNext() throws XMLStreamException {
        return delegate.hasNext();
    }

    public boolean isStartElement() {
        return delegate.isStartElement();
    }

    public String getLocalName() {
        return delegate.getLocalName();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        return delegate.getAttributeValue(namespaceURI, localName);
    }

    public void next() throws XMLStreamException {
        delegate.next();
    }

    @Override
    public void close() throws IOException, XMLStreamException {
        try {
            delegate.close();
        } finally {
            input.close();
        }
    }
}