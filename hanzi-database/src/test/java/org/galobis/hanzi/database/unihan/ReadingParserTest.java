package org.galobis.hanzi.database.unihan;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static java.util.Arrays.asList;
import static org.galobis.hanzi.domain.model.Pinyin.pinyin;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.galobis.hanzi.domain.model.Pinyin;
import org.junit.Test;

public class ReadingParserTest {

    private final ReadingParser parser = new ReadingParser();

    private final Map<String, List<List<Pinyin>>> expected = new HashMap<>();

    @Test
    public void should_parse_kHanyuPinyin_readings() throws Exception {
        expected.put("", asList());
        expected.put("10019.020:tiàn", asList(asList(pinyin("tian4"))));
        expected.put("10668.030:ńg,ń,ňg,ň,ǹg,ǹ",
                asList(asList(pinyin("ng2"), pinyin("n2"), pinyin("ng3"),
                        pinyin("n3"), pinyin("ng4"), pinyin("n4"))));
        expected.put("53017.140,53027.030:qiǎn", asList(asList(pinyin("qian3"))));
        expected.put("10513.110,10514.010,10514.020:gǒng", asList(asList(pinyin("gong3"))));
        expected.put("10093.130:xī,lǔ 74609.020:lǔ,xī",
                asList(asList(pinyin("xi1"), pinyin("lu3")),
                        asList(pinyin("lu3"), pinyin("xi1"))));
        for (String content : expected.keySet()) {
            assertThat("For " + content, parser.parse("kHanyuPinyin", content),
                    is(equalTo(expected.get(content))));
        }
    }

    @Test
    public void should_parse_kMandarin() throws Exception {
        expected.put("", asList());
        expected.put("wòng", asList(asList(pinyin("wong4"))));
        expected.put("wēi wéi", asList(asList(pinyin("wei1"), pinyin("wei2"))));
        for (String content : expected.keySet()) {
            assertThat("For " + content, parser.parse("kMandarin", content),
                    is(equalTo(expected.get(content))));
        }
    }

    @Test
    public void should_parse_kHanyuPinlu() throws Exception {
        expected.put("", asList());
        expected.put("guǐ(72)", asList(asList(pinyin("gui3"))));
        expected.put("zhe(10643) zháo(545) zhù(275) zhuó(125) zhāo(15)",
                asList(asList(pinyin("zhe5"), pinyin("zhao2"), pinyin("zhu4"),
                        pinyin("zhuo2"), pinyin("zhao1"))));
        for (String content : expected.keySet()) {
            assertThat("For " + content, parser.parse("kHanyuPinlu", content),
                    is(equalTo(expected.get(content))));
        }
    }

    @Test
    public void should_parse_kXHC1983() throws Exception {
        expected.put("", asList());
        expected.put("0265.081:dū",
                asList(asList(pinyin("du1"))));
        expected.put("0639.020*:kē",
                asList(asList(pinyin("ke1"))));
        expected.put("0286.030*,0286.031:ér",
                asList(asList(pinyin("er2"))));
        expected.put("0742.040:lǜ 1072.010,1072.032:shuài",
                asList(asList(pinyin("lü4"), pinyin("shuai4"))));
        expected.put("0418.061:guī 0624.021:jūn 0939.101:qiū",
                asList(asList(pinyin("gui1"), pinyin("jun1"), pinyin("qiu1"))));
        for (String content : expected.keySet()) {
            assertThat("For " + content, parser.parse("kXHC1983", content),
                    is(equalTo(expected.get(content))));
        }
    }
}
