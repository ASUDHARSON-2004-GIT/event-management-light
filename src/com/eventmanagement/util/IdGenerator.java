package com.eventmanagement.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private final AtomicInteger counter;

    public IdGenerator(int startValue) {
        this.counter = new AtomicInteger(startValue);
    }

    public int nextId() {
        return counter.getAndIncrement();
    }
}
