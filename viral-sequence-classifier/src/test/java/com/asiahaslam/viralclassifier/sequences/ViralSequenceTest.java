package com.asiahaslam.viralclassifier.sequences;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ViralSequenceTest {

    @Test
    void testBasicCreation() {
        ViralSequence seq = new ViralSequence("test", "ATCG", "TestVirus");
        assertEquals("test", seq.getName());
        assertEquals("ATCG", seq.getSequence());
        assertEquals("TestVirus", seq.getVirusFamily());
        assertEquals(4, seq.getLength());
    }

    @Test
    void testCaseNormalization() {
        ViralSequence seq = new ViralSequence("test", "atcg", "TestVirus");
        assertEquals("ATCG", seq.getSequence());
    }

    @Test
    void testEmptySequence() {
        ViralSequence seq = new ViralSequence("empty", "", "TestVirus");
        assertEquals(0, seq.getLength());
    }
}
