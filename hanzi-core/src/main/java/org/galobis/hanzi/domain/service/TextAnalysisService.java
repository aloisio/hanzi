package org.galobis.hanzi.domain.service;

import org.galobis.hanzi.domain.model.Text;
import org.galobis.hanzi.domain.model.TextFactory;

public class TextAnalysisService {

    private final TextFactory textFactory;

    public TextAnalysisService(TextFactory textFactory) {
        this.textFactory = textFactory;
    }

    public Text textFrom(String content) {
        return textFactory.textFrom(content);
    }
}
