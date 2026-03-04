package com.asiahaslam.viralclassifier.classification;

import java.util.Map;
import java.util.HashMap;

public class ClassificationResult {
    private final String sequenceId;
    private final String predictedFamily;
    private final double confidence;
    private final boolean isPredictionConfident;
    private final Map<String, Double> allFamilyScores;
    private final String algorithmUsed;
    private final long processingTimeMs;

    /**
     * Primary constructor for new viral classification results
     * @param sequenceId identifier for sequence being classified
     * @param predictedFamily predicted virus family or Unknown
     * @param confidence confidence score from 0.0 to 1.0
     * @param isPredictionConfident whether the prediction meets the confidence threshold
     * @param allFamilyScores map of all family names to their scores
     * @param algorithmUsed name of the algorithm used for classification
     * @param processingTimeMs time taken for classification in milliseconds
     */
    public ClassificationResult(String sequenceId, String predictedFamily, double confidence, boolean isPredictionConfident,
                                Map<String, Double> allFamilyScores, String algorithmUsed, long processingTimeMs) {
        this.sequenceId = sequenceId;
        this.predictedFamily = predictedFamily;
        this.confidence = confidence;
        this.isPredictionConfident = isPredictionConfident;
        this.allFamilyScores = allFamilyScores;
        this.algorithmUsed = algorithmUsed;
        this.processingTimeMs = processingTimeMs;
    }

    // simple constructor for classifications that are uncertain
    public static ClassificationResult createUnknown(String sequenceId, Map<String, Double> allFamilyScores,
                                              String algorithmUsed, long processingTimeMs) {
        return new ClassificationResult(sequenceId, "Unknown", 0.0, false,
                allFamilyScores, algorithmUsed, processingTimeMs);
    }

    @Override
    public String toString() {
        return String.format(
                "ClassificationResult{id='%s', predicted='%s', confidence = %.3f, confident=%s, time=%dms",
                sequenceId, predictedFamily, confidence, isPredictionConfident, processingTimeMs
        );
    }
}


