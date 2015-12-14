package org.galobis.hanzi.database;

import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
    private static final String CODEPOINT = "codepoint";

    private static final String DEFINITION = "definition";

    private static final String SQL_COLUMN_HANZI = "SELECT %s FROM hanzi WHERE codepoint IN (%d, %d, %d)";

    private static final String SQL_COUNT_HANZI = "SELECT COUNT(%s) FROM hanzi";

    private static final String SQL_COLUMN_PINYIN = "SELECT trim(syllable) || trim(char(tone)) FROM pinyin WHERE syllable='ma'";

    private static Connection connection;

    private static int totalUnihanCodePoints = 0;

    private static int totalUnihanDefinitions = 0;

    @BeforeClass
    public static void connectToDatabase() throws Exception {
        collectUnihanStats();
        connection = getConnection();
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        connection.close();
    }

    @Test
    public void database_should_contain_unihan_codepoints() throws Exception {
        List<Integer> codepoints = asList(connection,
                String.format(SQL_COLUMN_HANZI, CODEPOINT, asInt("才"), asInt("的"), asInt("𠜱")),
                Integer.class);
        assertThat(codepoints, contains(asInt("才"), asInt("的"), asInt("𠜱")));
    }

    @Test
    public void database_should_contain_all_unihan_codepoints() throws Exception {
        try (ResultSet codepointCount = connection.prepareCall(
                String.format(SQL_COUNT_HANZI, CODEPOINT)).executeQuery()) {
            codepointCount.next();
            assertThat(codepointCount.getInt(1), is(equalTo(totalUnihanCodePoints)));
        }
    }

    @Test
    public void database_should_contain_Unihan_definitions() throws Exception {
        List<String> definitions = asList(connection,
                String.format(SQL_COLUMN_HANZI, DEFINITION, asInt("梦"), asInt("覉"), asInt("䃟")),
                String.class);
        assertThat(definitions,
                containsInAnyOrder("dream; visionary; wishful",
                        "variant of 羇 U+7F87, inn; to lodge; to travel",
                        "䃟頭窰, a place in Hong Kong"));
    }

    @Test
    public void database_should_contain_pinyin_pronunciations() throws Exception {
        List<String> pinyin = asList(connection, SQL_COLUMN_PINYIN, String.class);
        assertThat(pinyin, containsInAnyOrder("ma1", "ma2", "ma3", "ma4", "ma5"));
    }

    @Test
    public void database_should_contain_all_Unihan_definitions() throws Exception {
        try (ResultSet definitionCount = connection.prepareCall(
                String.format(SQL_COUNT_HANZI, DEFINITION)).executeQuery()) {
            definitionCount.next();
            assertThat(definitionCount.getInt(1), is(equalTo(totalUnihanDefinitions)));
        }
    }

    public static Integer asInt(String hanzi) {
        return hanzi.codePointAt(0);
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
                    if ("char".equals(qName)) {
                        totalUnihanCodePoints++;
                    }
                    if (attributes.getValue("kDefinition") != null) {
                        totalUnihanDefinitions++;
                    }
                }
            });
            xmlReader.parse(new InputSource(zipInputStream));
        }
    }
}
