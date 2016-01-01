package org.galobis.hanzi.domain.service;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.galobis.hanzi.domain.model.Script;
import org.galobis.hanzi.domain.model.Text;
import org.galobis.hanzi.domain.model.TextFactory;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;

public class TextAnalysisServiceTest {
    @Injectable
    private TextFactory factory;

    @Test
    public void textFrom_should_delegate_when_script_not_passed() {
        new Expectations() {
            {
                factory.textFrom(anyString);
                returns(new Text("abc", Script.UNKNOWN));
            }
        };
        TextAnalysisService service = new TextAnalysisService(factory);
        assertThat(service.textFrom("input").script(),
                is(equalTo(Script.UNKNOWN)));
    }
}
