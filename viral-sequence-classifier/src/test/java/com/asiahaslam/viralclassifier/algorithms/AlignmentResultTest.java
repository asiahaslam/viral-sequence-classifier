package com.asiahaslam.viralclassifier.algorithms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlignmentResultTest {
    @Test
    void testDevCreation() {
        AlignmentResult result = new AlignmentResult(0, 0.0);
        assertEquals(0, result.getAlignmentScore());
        assertEquals(0.0, result.getNormalizedScore());
        assertEquals("", result.getAlignedSequence1());
        assertEquals("", result.getAlignedSequence2());
        assertEquals(0, result.getStartPos1());
        assertEquals(0, result.getStartPos2());
        assertEquals(0, result.getEndPos1());
        assertEquals(0, result.getEndPos2());
        assertEquals(0L, result.getMemoryUsedBytes());
    }

    @Test
    void testBasicCreation() {
        AlignmentResult result = new AlignmentResult(1000, 0.95, "AAAA", "GGGG", 5, 10, 7, 12, 0L);
        assertEquals(1000, result.getAlignmentScore());
        assertEquals(0.95, result.getNormalizedScore());
        assertEquals("AAAA", result.getAlignedSequence1());
        assertEquals("GGGG", result.getAlignedSequence2());
        assertEquals(5, result.getStartPos1());
        assertEquals(10, result.getStartPos2());
        assertEquals(7, result.getEndPos1());
        assertEquals(12, result.getEndPos2());
        assertEquals(0L, result.getMemoryUsedBytes());
    }
}
