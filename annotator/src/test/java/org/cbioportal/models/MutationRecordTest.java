package org.cbioportal.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MutationRecordTest {

    @Test
    void init() {
    }

    @Test
    void get() {
        List<String> inputHeaders = List.of("a", "b", "c");
        MutationRecord.init(inputHeaders);
        MutationRecord record = new MutationRecord("1\t22\t333");
        assertEquals("1", record.get("a"));
        assertEquals("22", record.get("b"));
        assertEquals("333", record.get("c"));
    }

    @Test
    void asGenomicLocation() {
    }

    @Test
    void toLine() {
    }

    @Test
    void getHeaders() {
    }
}