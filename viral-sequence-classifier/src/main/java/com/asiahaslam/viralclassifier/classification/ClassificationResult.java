package com.asiahaslam.viralclassifier.classification;

import com.asiahaslam.viralclassifier.algorithms.AlignmentResult;

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
    private final long memoryUsedBytes;
    private final AlignmentResult topAlignment;
    private final String topReferenceName;
    private final double topRawScore;


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
                                Map<String, Double> allFamilyScores, String algorithmUsed, long processingTimeMs,
                                long memoryUsedBytes, AlignmentResult topAlignment, String topReferenceName, double topRawScore) {
        this.sequenceId = sequenceId;
        this.predictedFamily = predictedFamily;
        this.confidence = confidence;
        this.isPredictionConfident = isPredictionConfident;
        this.allFamilyScores = allFamilyScores;
        this.algorithmUsed = algorithmUsed;
        this.processingTimeMs = processingTimeMs;
        this.memoryUsedBytes = memoryUsedBytes;
        this.topAlignment = topAlignment;
        this.topReferenceName = topReferenceName;
        this.topRawScore = topRawScore;
    }

    // simple constructor for classifications that are uncertain
    public static ClassificationResult createUnknown(String sequenceId, Map<String, Double> allFamilyScores,
                                              String algorithmUsed, long processingTimeMs, long memoryUsedBytes) {
        return new ClassificationResult(sequenceId, "Unknown", 0.0, false,
                allFamilyScores, algorithmUsed, processingTimeMs, memoryUsedBytes, new AlignmentResult(0.0, 0.0), "", 0.0);
    }

    public static class SecondBestFamily {
        public final String secondBest;
        public double secondBestScore;

        public SecondBestFamily(String secondBest, double secondBestScore) {
            this.secondBest = secondBest;
            this.secondBestScore = secondBestScore;
        }

        public String getSecondBest() { return secondBest; }
        public double getSecondBestScore() { return secondBestScore; }
    }

    // find virus family with the second-highest score
    public SecondBestFamily getSecondBestFamily() {
        String best = null;
        String secondBest = null;
        double bestScore = -1;
        double secondBestScore = -1;

        for (Map.Entry<String, Double> entry : allFamilyScores.entrySet()) {
            if (entry.getValue() > bestScore) {
                secondBest = best;
                secondBestScore = bestScore;
                best = entry.getKey();
                bestScore = entry.getValue();
            }
            else if (entry.getValue() > secondBestScore) {
                secondBest = entry.getKey();
                secondBestScore = entry.getValue();
            }
        }

        return new SecondBestFamily(secondBest, secondBestScore);
    }

    // getters
    public String getAlgorithmUsed() {
        return algorithmUsed;
    }
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }
    public double getMemoryUsedKB() {
        return memoryUsedBytes / 1024.0;
    }
    public double getMemoryUsedMB() {
        return memoryUsedBytes / (1024.0 * 1024.0);
    }
    public String getSequenceId() {
        return sequenceId;
    }
    public String getPredictedFamily() {
        return predictedFamily;
    }
    public double getConfidence() {
        return confidence;
    }
    public boolean isPredictionConfident() {
        return isPredictionConfident;
    }
    public AlignmentResult getTopAlignment() { return topAlignment; }
    public String getTopReferenceName() { return topReferenceName; }
    public double getTopRawScore() { return topRawScore; }

    // print max score details
    public void printMaxScore() {
        if (topAlignment != null && !topAlignment.getAlignedSequence1().isEmpty() && !topAlignment.getAlignedSequence2().isEmpty()) {
            System.out.printf("Score: %.2f (normalized: %.3f)", topRawScore, confidence);
            System.out.println(" Reference sequence name: " + topReferenceName);
        }
    }


    // print alignment details
    public void printAlignmentInfo() {
        if (topAlignment != null && !topAlignment.getAlignedSequence1().isEmpty() && !topAlignment.getAlignedSequence2().isEmpty()) {
            System.out.println("Reference sequence name: " + topReferenceName);
            System.out.println("Optimal alignment for the best match:");
            System.out.println(topAlignment.getAlignedSequence1());
            System.out.println(topAlignment.getAlignedSequence2());
            System.out.println("Sequence 1: start: " + topAlignment.getStartPos1() + " end: " + topAlignment.getEndPos1());
            System.out.println("Sequence 2: start: " + topAlignment.getStartPos2() + " end: " + topAlignment.getEndPos2());
        }
    }

    @Override
    public String toString() {
        return String.format(
                "ClassificationResult{id='%s', predicted='%s', confidence = %.3f, confident=%s, time=%dms, memory=%.2fMB}",
                sequenceId, predictedFamily, confidence, isPredictionConfident, processingTimeMs, getMemoryUsedMB()
        );
    }
}


