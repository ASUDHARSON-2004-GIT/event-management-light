package com.eventmanagement.util;

import java.util.concurrent.atomic.AtomicInteger;

// A small helper class that hands out unique numbers.
// Each entity type keeps its own counter so ids do not
// depend on how many other objects exist in the system.
public class IdGenerator {

    private final AtomicInteger counter;

    public IdGenerator(int startValue) {
        this.counter = new AtomicInteger(startValue);
    }

    public int nextId() {
        return counter.getAndIncrement();
    }
}
