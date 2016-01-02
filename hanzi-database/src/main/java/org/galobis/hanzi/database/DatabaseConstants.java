package org.galobis.hanzi.database;

import java.io.OutputStream;

public final class DatabaseConstants {
    public static final class Columns {
        public static final String CODEPOINT = "codepoint";

        private Columns() {
            throw new UnsupportedOperationException(UTILITY_ERROR);
        }
    }

    public static final class Tables {
        public static final String HANZI = "hanzi";

        public static final String SIMPLIFIED = "simplified";

        public static final String TRADITIONAL = "traditional";

        private Tables() {
            throw new UnsupportedOperationException(UTILITY_ERROR);
        }
    }

    public static final String DATABASE_URL = "jdbc:derby:classpath:database/hanzi";

    public static final OutputStream DEV_NULL = new OutputStream() {
        public void write(int b) {
            return;
        }
    };

    public static final String ERROR_STREAM_FIELD_PROPERTY_KEY = "derby.stream.error.field";

    public static final String ERROR_STREAM_FIELD_PROPERTY_VALUE = String.format("%s.DEV_NULL",
            DatabaseConstants.class.getName());

    private static final String UTILITY_ERROR = "Do not instantiate";

    private DatabaseConstants() {
        throw new UnsupportedOperationException(UTILITY_ERROR);
    }
}
