package com.asiahaslam.viralclassifier.classification;

import com.asiahaslam.viralclassifier.algorithms.*;
import com.asiahaslam.viralclassifier.sequences.ViralSequence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class ViralClassifierTest {

    private Map<String, List<ViralSequence>> database;
    private ViralClassifier classifier;

    @BeforeEach
    void setUp() {
        database = new HashMap<>();

        // create mock reference sequences
        List<ViralSequence> influenzaRefs = Arrays.asList(
                new ViralSequence("flu1", "ATCGATCGATCGATCGATCG", "Influenza"),
                new ViralSequence("flu2", "ATCGATCGATCGATCC", "Influenza")
        );

        List<ViralSequence> coronaRefs = Arrays.asList(
                new ViralSequence("corona1", "GGCCGGCCGGCCGGCCGGCCGGCC", "Coronavirus"),
                new ViralSequence("corona2", "GGCCGGCCGGCCGGCCGGCCGGCCA", "Coronavirus")
        );

        database.put("Influenza", influenzaRefs);
        database.put("Coronavirus", coronaRefs);

        classifier = new ViralClassifier(database);
    }

    @Test
    void testInfluenzaClassification() {
        ViralSequence testSeq = new ViralSequence("test1", "ATCGATCGATCGATCGATCG", "Unknown");
        ClassificationResult result = classifier.classify(testSeq);

        assertEquals("Influenza", result.getPredictedFamily());
        assertTrue(result.getConfidence() > 0.7);
        assertTrue(result.isPredictionConfident());
    }

    @Test
    void testCoronavirusClassification() {
        ViralSequence testSeq = new ViralSequence("test2", "GGCCGGCCGGCCGGCCGGCCGGCC", "Unknown");
        ClassificationResult result = classifier.classify(testSeq);

        assertEquals("Coronavirus", result.getPredictedFamily());
        assertTrue(result.getConfidence() > 0.7);
    }
}
