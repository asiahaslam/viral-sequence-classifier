package com.asiahaslam.viralclassifier.classification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class ClassificationResultTest {
    private Map<String, Double> familyScores;

    @BeforeEach
    void setUp() {
        familyScores = new HashMap<>();
        familyScores.put("Influenza", 0.85);
        familyScores.put("Coronavirus", 0.42);
        familyScores.put("Herpesvirus", 0.28);
    }

    @Test
    void testBasicCreation() {
        ClassificationResult result = new ClassificationResult(
                "test1", "Influenza", 0.85, true, familyScores, "TestAlg", 100L, 1024L
        );

        assertEquals("test1", result.getSequenceId());
        assertEquals("Influenza", result.getPredictedFamily());
        assertEquals(0.85, result.getConfidence(), 0.001);
        assertTrue(result.isPredictionConfident());
        assertEquals("TestAlg", result.getAlgorithmUsed());
        assertEquals(100L, result.getProcessingTimeMs());
        assertEquals(1024, result.getMemoryUsedBytes());
    }

    @Test
    void testSecondBestFamily() {
        ClassificationResult result = new ClassificationResult(
                "test2", "Influenza", 0.85, true, familyScores, "TestAlg", 100L, 1024L
        );

        assertEquals("Coronavirus", result.getSecondBestFamily());
    }

    @Test
    void testMemoryConversion() {
        ClassificationResult result = new ClassificationResult(
                "test2", "Influenza", 0.85, true, familyScores, "TestAlg", 100L, 2048L
        );

        assertEquals(2.0, result.getMemoryUsedKB());
    }

    @Test
    void testCreateUnknown() {
        ClassificationResult unknown = ClassificationResult.createUnknown("unknown1", familyScores, "TestAlg", 150L, 512L);

        assertEquals("Unknown", unknown.getPredictedFamily());
        assertEquals(0.0, unknown.getConfidence());
        assertFalse(unknown.isPredictionConfident());
    }
}
