package org.galobis.hanzi.infrastructure;

import org.galobis.hanzi.domain.model.Script;
import org.galobis.hanzi.domain.model.Text;
import org.galobis.hanzi.domain.model.TextFactory;

public class DatabaseTextFactory implements TextFactory {

    @Override
    public Text textFrom(String content) {
        return new Text(content, Script.UNKNOWN);
    }
}
