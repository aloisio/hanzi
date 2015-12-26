package org.galobis.hanzi.database.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;

public class BatchCountingStatementHandler implements InvocationHandler {

    public static BatchCountingStatement countBatches(PreparedStatement delegate) {
        return (BatchCountingStatement) Proxy.newProxyInstance(
                BatchCountingStatementHandler.class.getClassLoader(),
                new Class[] { BatchCountingStatement.class },
                new BatchCountingStatementHandler(delegate));
    }

    private final PreparedStatement delegate;

    private int count = 0;

    private BatchCountingStatementHandler(PreparedStatement delegate) {
        this.delegate = delegate;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getBatchCount".equals(method.getName())) {
            return count;
        }
        try {
            return method.invoke(delegate, args);
        } finally {
            if ("addBatch".equals(method.getName())) {
                ++count;
            }
        }
    }

    public static interface BatchCountingStatement extends PreparedStatement {
        public int getBatchCount();
    }
}