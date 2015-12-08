package org.galobis.hanzi.database.unihan;

import static org.galobis.hanzi.model.Pinyin.pinyin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.galobis.hanzi.model.Pinyin;

public class ReadingParser {

    private static interface ReadingCategoryParser {
        List<List<Pinyin>> parse(String contents);
    }

    private static class HanyuPinyinParser implements ReadingCategoryParser {
        private static final Pattern READING_BLOCK = Pattern.compile("^(?:\\d{5}\\.\\d{3},)*\\d{5}\\.\\d{3}:(.+)");

        @Override
        public List<List<Pinyin>> parse(String contents) {
            List<List<Pinyin>> readings = new ArrayList<>();
            for (String readingBlock : contents.split(" ")) {
                Matcher matcher = READING_BLOCK.matcher(readingBlock);
                if (matcher.matches()) {
                    readings.add(
                            Arrays.stream(matcher.group(1).split(","))
                                    .map(Pinyin::pinyin)
                                    .collect(Collectors.toList()));
                }
            }
            return readings;
        }
    }

    private static class MandarinParser implements ReadingCategoryParser {

        @Override
        public List<List<Pinyin>> parse(String contents) {
            if (contents.isEmpty()) {
                return Collections.emptyList();
            }
            return Arrays.asList(
                    Arrays.stream(contents.split(" "))
                            .map(Pinyin::pinyin)
                            .collect(Collectors.toList()));
        }
    }

    private static class HanyuPinluParser implements ReadingCategoryParser {
        private static final Pattern READING = Pattern.compile("^(.+)\\([0-9]+\\)$");

        protected Pattern getPattern() {
            return READING;
        }

        @Override
        public List<List<Pinyin>> parse(String contents) {
            if (contents.isEmpty()) {
                return Collections.emptyList();
            }
            List<Pinyin> readings = new ArrayList<>();
            for (String reading : contents.split(" ")) {
                Matcher matcher = getPattern().matcher(reading);
                if (matcher.matches()) {
                    readings.add(pinyin(matcher.group(1)));
                }
            }
            return Arrays.asList(readings);
        }
    }

    private static class XHC1983Parser extends HanyuPinluParser {
        private static final Pattern READING = Pattern
                .compile("^(?:[0-9]{4}\\.[0-9]{3}\\*?,)*[0-9]{4}\\.[0-9]{3}\\*?:(.*)$");

        @Override
        protected Pattern getPattern() {
            return READING;
        }
    }

    private static final Map<String, ReadingCategoryParser> PARSERS = new HashMap<String, ReadingCategoryParser>() {
        private static final long serialVersionUID = 1L;

        {
            put("kHanyuPinyin", new HanyuPinyinParser());
            put("kMandarin", new MandarinParser());
            put("kHanyuPinlu", new HanyuPinluParser());
            put("kXHC1983", new XHC1983Parser());
        }
    };

    public List<List<Pinyin>> parse(String category, String contents) {
        return PARSERS.get(category).parse(contents);
    }
}
