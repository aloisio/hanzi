package org.galobis.hanzi.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;

import org.hamcrest.Matchers;
import org.junit.Test;

public class HanziTest {

    @Test
    public void equalsContract() {
        Hanzi hanzi = new Hanzi(0x6797);
        assertEquals(hanzi, hanzi);
        assertEquals(hanzi, new Hanzi(0x6797));
        assertNotEquals(hanzi, new Hanzi(0x2072F));
        assertNotEquals(hanzi, null);
        assertNotEquals(hanzi, Integer.valueOf(0x6797));
    }

    @Test
    public void hashCodeContract() {
        Hanzi hanzi1 = new Hanzi(0x68A6);
        Hanzi hanzi2 = new Hanzi(0x68A6);
        Hanzi hanzi3 = new Hanzi(0x68A7);
        HashMap<Hanzi, Integer> map = new HashMap<>();
        map.put(hanzi1, 1);
        map.put(hanzi2, 2);
        map.put(hanzi3, 3);
        assertThat(map.entrySet(), hasSize(2));
        assertThat(map.get(hanzi1), Matchers.equalTo(2));
        assertThat(map.get(hanzi2), Matchers.equalTo(2));
        assertThat(map.get(hanzi3), Matchers.equalTo(3));
    }

    @Test
    public void asString() {
        assertEquals(new Hanzi(0x2072e).toString(), "𠜮");
        assertEquals(new Hanzi(0x798F).toString(), "福");
    }
}
