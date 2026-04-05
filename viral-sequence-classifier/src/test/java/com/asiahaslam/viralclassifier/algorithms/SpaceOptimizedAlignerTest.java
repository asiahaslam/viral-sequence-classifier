package com.asiahaslam.viralclassifier.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class SpaceOptimizedAlignerTest {
    private SpaceOptimizedAligner aligner;

    @BeforeEach
    void setUp() {
        aligner = new SpaceOptimizedAligner();
    }

    @Test
    void testPerfectMatch() {
        AlignmentResult result = aligner.align("ATCG", "ATCG");
        assertEquals(8.0, result.getAlignmentScore(), 0.001);
        assertEquals(1.0, result.getNormalizedScore(), 0.001);
    }

    @Test
    void testCompletelyDifferent() {
        AlignmentResult result = aligner.align("AAAA", "TTTT");
        assertEquals(0.0, result.getAlignmentScore(), 0.001);
        assertEquals(0.0, result.getNormalizedScore(), 0.001);
    }

    @Test
    void testPartialMatch() {
        AlignmentResult result = aligner.align("ATCGATCG", "ATCGATTCG");
        assertTrue(result.getAlignmentScore() > 0);
        assertTrue(result.getNormalizedScore() > 0.5);
    }

    @Test
    void testWithGaps() {
        AlignmentResult result = aligner.align("ATCGATCG", "ATCGCG");
        assertTrue(result.getAlignmentScore() > 0);
        assertTrue(result.getNormalizedScore() < 1.0);
    }

    @Test
    void testEmptySequences() {
        AlignmentResult result = aligner.align("", "ATCG");
        assertEquals(0.0, result.getAlignmentScore());

        result = aligner.align("ATCG", "");
        assertEquals(0.0, result.getAlignmentScore());
    }

    @Test
    void testNullSequences() {
        AlignmentResult result = aligner.align(null, "ATCG");
        assertEquals(0.0, result.getAlignmentScore());

        result = aligner.align("ATCG", null);
        assertEquals(0.0, result.getAlignmentScore());
    }

    @Test
    void testAlgorithmInfo() {
        assertEquals("Space-Optimized Smith-Waterman", aligner.getAlgorithmName());
        assertEquals("O(m x n)", aligner.getTimeComplexity());
        assertEquals("O(min(m, n))", aligner.getSpaceComplexity());
    }

    @Test
    void TestDifferentLengths() {
        AlignmentResult result = aligner.align("AT", "ATCGATCGATCG");
        assertTrue(result.getAlignmentScore() > 0);
        assertTrue(result.getNormalizedScore() > 0);
    }
}
