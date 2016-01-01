package org.galobis.hanzi.domain.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;

import org.junit.Test;

public class TextTest {
    @Test
    public void should_implement_equals_contract() {
        Text text = new Text("ABC", Script.UNKNOWN);
        assertEquals(text, text);
        assertEquals(text, new Text("ABC", Script.UNKNOWN));
        assertNotEquals(text, new Text("我", Script.UNKNOWN));
        assertNotEquals(text, new Text("ABC", Script.SIMPLIFIED));
        assertNotEquals(text, null);
        assertNotEquals(text, "ABC");
    }

    @Test
    public void should_implement_hashCode_contract() {
        Text text1 = new Text("ABC", Script.UNKNOWN);
        Text text2 = new Text("ABC", Script.UNKNOWN);
        Text text3 = new Text("我", Script.SIMPLIFIED);
        Text text4 = new Text("我", Script.TRADITIONAL);
        HashMap<Text, Integer> map = new HashMap<>();
        map.put(text1, 1);
        map.put(text2, 2);
        map.put(text3, 3);
        map.put(text4, 4);
        assertThat(map.entrySet(), hasSize(3));
        assertThat(map.get(text1), is(equalTo(2)));
        assertThat(map.get(text2), is(equalTo(2)));
        assertThat(map.get(new Text("我", Script.SIMPLIFIED)), is(equalTo(3)));
        assertThat(map.get(new Text("我", Script.TRADITIONAL)), is(equalTo(4)));
    }

    @Test
    public void should_implement_toString() {
        assertEquals("U:ABC", new Text("ABC", Script.UNKNOWN).toString());
        assertEquals("S:中国", new Text("中国", Script.SIMPLIFIED).toString());
        assertEquals("T:國語", new Text("國語", Script.TRADITIONAL).toString());
    }
}
