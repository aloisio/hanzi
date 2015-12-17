package org.galobis.hanzi.database;

import static java.util.Arrays.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.asList;
import static org.galobis.hanzi.database.DatabaseTestUtilities.getConnection;
import static org.galobis.hanzi.database.unihan.UnihanConstants.CHARACTER;
import static org.galobis.hanzi.database.unihan.UnihanConstants.DEFINITION;
import static org.galobis.hanzi.database.unihan.UnihanConstants.SIMPLIFIED;
import static org.galobis.hanzi.database.unihan.UnihanConstants.TRADITIONAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String COLUMN_CODEPOINT = "codepoint";

    private static final String COLUMN_DEFINITION = "definition";

    private static final String SQL_COLUMN_HANZI = "SELECT %s FROM hanzi WHERE codepoint IN (%d, %d, %d)";

    private static final String SQL_COUNT_HANZI = "SELECT COUNT(%s) FROM hanzi";

    private static final String SQL_COLUMN_PINYIN = "SELECT TRIM(syllable) || TRIM(CHAR(tone)) "
            + "FROM pinyin WHERE syllable='ma'";

    private static final String SQL_COLUMN_READING = "SELECT TRIM(syllable) || TRIM(CHAR(tone)) "
            + "FROM reading, pinyin WHERE pinyin.id = pinyin_id "
            + "AND codepoint = %d ORDER BY ordinal";

    private static final String SQL_COUNT_SIMPLIFIED = "SELECT COUNT(*) FROM simplified";

    private static final String SQL_COUNT_TRADITIONAL = "SELECT COUNT(*) FROM traditional";

    private static final String SQL_COLUMN_SIMPLIFIED = "SELECT simplified "
            + "FROM simplified WHERE codepoint = %d ORDER BY simplified";

    private static final String SQL_COLUMN_TRADITIONAL = "SELECT traditional "
            + "FROM traditional WHERE codepoint = %d ORDER BY traditional";

    private static Connection connection;

    private static int totalUnihanCodePoints = 0;

    private static int totalUnihanDefinitions = 0;

    private static int totalSimplifiedVariants = 0;

    private static int totalTraditionalVariants = 0;

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
                String.format(SQL_COLUMN_HANZI, COLUMN_CODEPOINT, asInt("才"), asInt("的"), asInt("𠜱")),
                Integer.class);
        assertThat(codepoints, contains(asInt("才"), asInt("的"), asInt("𠜱")));
    }

    @Test
    public void database_should_contain_all_unihan_codepoints() throws Exception {
        try (ResultSet codepointCount = connection.prepareCall(
                String.format(SQL_COUNT_HANZI, COLUMN_CODEPOINT)).executeQuery()) {
            codepointCount.next();
            assertThat(codepointCount.getInt(1), is(equalTo(totalUnihanCodePoints)));
        }
    }

    @Test
    public void database_should_contain_Unihan_definitions() throws Exception {
        List<String> definitions = asList(connection,
                String.format(SQL_COLUMN_HANZI, COLUMN_DEFINITION, asInt("梦"), asInt("覉"), asInt("䃟")),
                String.class);
        assertThat(definitions,
                containsInAnyOrder("dream; visionary; wishful",
                        "variant of 羇 U+7F87, inn; to lodge; to travel",
                        "䃟頭窰, a place in Hong Kong"));
    }

    @Test
    public void database_should_contain_all_Unihan_definitions() throws Exception {
        try (ResultSet definitionCount = connection.prepareCall(
                String.format(SQL_COUNT_HANZI, COLUMN_DEFINITION)).executeQuery()) {
            definitionCount.next();
            assertThat(definitionCount.getInt(1), is(equalTo(totalUnihanDefinitions)));
        }
    }

    @Test
    public void database_should_contain_pinyin_pronunciations() throws Exception {
        List<String> pinyin = asList(connection, SQL_COLUMN_PINYIN, String.class);
        assertThat(pinyin, containsInAnyOrder("ma1", "ma2", "ma3", "ma4", "ma5"));
    }

    @Test
    public void database_should_contain_readings_of_a_hanzi() throws Exception {
        List<String> readings = asList(connection,
                String.format(SQL_COLUMN_READING, "卤".codePointAt(0)),
                String.class);
        assertThat(readings, contains("lu3", "xi1"));
    }

    @Test
    public void database_should_contain_simplified_variants() throws Exception {
        Map<Integer, List<Integer>> expected = new HashMap<>();
        expected.put(0x4E95, asList());
        expected.put(0x611B, asList(0x7231));
        expected.put(0x5FB5, asList(0x5F81, 0x5FB5));
        expected.put(0x937E, asList(0x949F, 0x953A));
        for (Integer codePoint : expected.keySet()) {
            List<Integer> variants = asList(connection,
                    String.format(String.format(SQL_COLUMN_SIMPLIFIED, codePoint)),
                    Integer.class);
            assertThat(variants, equalTo(expected.get(codePoint)));
        }
    }

    @Test
    public void database_should_contain_traditional_variants() throws Exception {
        Map<Integer, List<Integer>> expected = new HashMap<>();
        expected.put(0x4E95, asList());
        expected.put(0x96BE, asList(0x96E3));
        expected.put(0x4F59, asList(0x4F59, 0x9918));
        expected.put(0x4F2A, asList(0x507D, 0x50DE));
        expected.put(0x4E48, asList(0x5E7A, 0x9EBC, 0x9EBD));
        expected.put(0x53F0, asList(0x53F0, 0x6AAF, 0x81FA, 0x98B1));
        for (Integer codePoint : expected.keySet()) {
            List<Integer> variants = asList(connection,
                    String.format(String.format(SQL_COLUMN_TRADITIONAL, codePoint)),
                    Integer.class);
            assertThat(variants, equalTo(expected.get(codePoint)));
        }
    }

    @Test
    public void database_should_contain_all_simplified_variants() throws Exception {
        try (ResultSet simplifiedCount = connection.prepareCall(
                String.format(SQL_COUNT_SIMPLIFIED)).executeQuery()) {
            simplifiedCount.next();
            assertThat(simplifiedCount.getInt(1), is(equalTo(totalSimplifiedVariants)));
        }
    }

    @Test
    public void database_should_contain_all_traditional_variants() throws Exception {
        try (ResultSet traditionalCount = connection.prepareCall(
                String.format(SQL_COUNT_TRADITIONAL)).executeQuery()) {
            traditionalCount.next();
            assertThat(traditionalCount.getInt(1), is(equalTo(totalTraditionalVariants)));
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
                    if (CHARACTER.equals(qName)) {
                        totalUnihanCodePoints++;
                    }
                    if (attributes.getValue(DEFINITION) != null) {
                        totalUnihanDefinitions++;
                    }
                    if (attributes.getValue(SIMPLIFIED) != null) {
                        totalSimplifiedVariants += attributes.getValue(SIMPLIFIED).split(" ").length;
                    }
                    if (attributes.getValue(TRADITIONAL) != null) {
                        totalTraditionalVariants += attributes.getValue(TRADITIONAL).split(" ").length;
                    }
                }
            });
            xmlReader.parse(new InputSource(zipInputStream));
        }
    }
}
