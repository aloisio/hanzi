package org.galobis.hanzi.database;

import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParserFactory;

import org.galobis.test.annotation.category.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

@Category(IntegrationTest.class)
public class InitialStateIntegrationTest {
    private static Connection connection;

    private static int totalUnihanCodePoints = 0;

    @BeforeClass
    public static void connectToDatabase() throws Exception {
        connection = getConnection();
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        connection.close();
    }

    @Test
    public void database_should_contain_unihan_codepoints() throws Exception {
        List<Integer> codepoints = asList(connection,
                "select codepoint from hanzi where codepoint in (25165, 30340, 132913)",
                Integer.class);
        assertThat(codepoints, contains(asInt("才"), asInt("的"), asInt("𠜱")));
    }

    @Test
    public void database_should_contain_all_unihan_codepoints() throws Exception {
        collectUnihanStats();
        try (ResultSet codepointCount = connection.prepareCall(
                "SELECT COUNT(codepoint) FROM hanzi").executeQuery()) {
            codepointCount.next();
            assertThat(codepointCount.getInt(1), is(equalTo(totalUnihanCodePoints)));
        }
    }

    public static Integer asInt(String hanzi) {
        return hanzi.codePointAt(0);
    }

    public static String asString(int codePoint) {
        return String.valueOf(Character.toChars(codePoint));
    }

    private static void collectUnihanStats() throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(
                InitialStateIntegrationTest.class.getResourceAsStream("/raw/ucd.unihan.flat.zip"))) {
            zipInputStream.getNextEntry();
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xmlReader.setContentHandler(new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes)
                        throws SAXException {
                    if (qName.equals("char")) {
                        totalUnihanCodePoints++;
                    }
                }
            });
            xmlReader.parse(new InputSource(zipInputStream));
        }
    }
}
