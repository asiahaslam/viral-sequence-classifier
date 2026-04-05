package com.asiahaslam.viralclassifier.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


public class ScoringMatrixTest {
    private ScoringMatrix matrix;

    @BeforeEach
    void setUp() {
        matrix = new ScoringMatrix();
    }

    @Test
    void testMatchScore() {
        assertEquals(2, matrix.getScore('A', 'A'));
        assertEquals(2, matrix.getScore('T', 'T'));
        assertEquals(2, matrix.getScore('G', 'G'));
        assertEquals(2, matrix.getScore('C', 'C'));
    }

    @Test
    void testMismatchScore() {
        assertEquals(-1, matrix.getScore('A', 'T'));
        assertEquals(-1, matrix.getScore('A', 'G'));
        assertEquals(-1, matrix.getScore('A', 'C'));
        assertEquals(-1, matrix.getScore('C', 'T'));
        assertEquals(-1, matrix.getScore('C', 'G'));
        assertEquals(-1, matrix.getScore('G', 'T'));
    }

    @Test
    void testCaseInsensitive() {
        assertEquals(2, matrix.getScore('a', 'A'));
        assertEquals(-1, matrix.getScore('a', 'T'));
        assertEquals(2, matrix.getScore('g', 'G'));
        assertEquals(2, matrix.getScore('C', 'c'));
        assertEquals(-1, matrix.getScore('t', 'G'));
        assertEquals(2, matrix.getScore('T', 't'));
    }

    @Test
    void testUnknownNucleotide() {
        assertEquals(-1, matrix.getScore('A', 'N'));
        assertEquals(-1, matrix.getScore('N', 'T'));
        assertEquals(-1, matrix.getScore('G', 'N'));
        assertEquals(-1, matrix.getScore('N', 'C'));
    }

    @Test
    void testInvalidNucleotide() {
        assertThrows(IllegalArgumentException.class, () -> {
            matrix.getScore('X', 'A');
        });
    }

    @Test
    void testGapPenalty() {
        assertEquals(-1, matrix.getGapPenalty());
    }

    @Test
    void testMaxPossibleScore() {
        assertEquals(8, matrix.getMaxPossibleScore("ATCG", "ATCG"));
        assertEquals(6, matrix.getMaxPossibleScore("ATG", "ATCGAA"));
        assertEquals(20, matrix.getMaxPossibleScore("ATCGATCGAT", "ATCTAGCTTA"));
        assertEquals(4, matrix.getMaxPossibleScore("ATGTAGC", "AT"));
    }

    @Test
    void testCustomScoringMatrix() {
        ScoringMatrix customMatrix = new ScoringMatrix(3, -2, -2);

        assertEquals(3, customMatrix.getMatchScore());
        assertEquals(-2, customMatrix.getMismatchScore());
        assertEquals(-2, customMatrix.getGapPenalty());
    }
}
