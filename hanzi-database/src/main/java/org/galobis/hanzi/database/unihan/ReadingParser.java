package org.galobis.hanzi.database.unihan;

import static org.galobis.hanzi.database.unihan.UnihanConstants.HANYU_PINLU;
import static org.galobis.hanzi.database.unihan.UnihanConstants.HANYU_PINYIN;
import static org.galobis.hanzi.database.unihan.UnihanConstants.MANDARIN;
import static org.galobis.hanzi.database.unihan.UnihanConstants.XHC1983;
import static org.galobis.hanzi.domain.model.Pinyin.pinyin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.galobis.hanzi.domain.model.Pinyin;

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
        private static final Pattern READING = Pattern.compile("^(.+)$");

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

    private static class HanyuPinluParser extends MandarinParser {
        private static final Pattern READING = Pattern.compile("^(.+)\\([0-9]+\\)$");

        @Override
        protected Pattern getPattern() {
            return READING;
        }
    }

    private static class XHC1983Parser extends MandarinParser {
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
            put(HANYU_PINYIN, new HanyuPinyinParser());
            put(MANDARIN, new MandarinParser());
            put(HANYU_PINLU, new HanyuPinluParser());
            put(XHC1983, new XHC1983Parser());
        }
    };

    public List<List<Pinyin>> parse(String category, String contents) {
        return PARSERS.get(category).parse(contents);
    }
}
