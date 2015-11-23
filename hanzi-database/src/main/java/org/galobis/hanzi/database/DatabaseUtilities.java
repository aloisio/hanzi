package org.galobis.hanzi.database;

import java.io.OutputStream;

public class DatabaseUtilities {
    public static final OutputStream DEV_NULL = new OutputStream() {
        public void write(int b) {
            return;
        }
    };
}
