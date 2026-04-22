package com.asiahaslam.viralclassifier.classification;

import com.asiahaslam.viralclassifier.algorithms.SequenceAligner;
import com.asiahaslam.viralclassifier.algorithms.SmithWatermanAligner;
import com.asiahaslam.viralclassifier.algorithms.AlignmentResult;
import com.asiahaslam.viralclassifier.sequences.ViralSequence;

import javax.swing.*;
import java.util.*;

// this class classifies unknown sequences using Smith-Waterman alignment
// it compares the new sequence against a reference database to predict likely virus family
public class ViralClassifier {
    private final SequenceAligner aligner;
    private final Map<String, List<ViralSequence>> referenceDatabase;
    private final double confidenceThreshold;
    private long totalMemoryUsed;
    private AlignmentResult topAlignment;

    // constructor with default parameters
    public ViralClassifier(Map<String, List<ViralSequence>> referenceDatabase) {
        this.aligner = new SmithWatermanAligner(); // default to standard algorithm
        this.referenceDatabase = new HashMap<>(referenceDatabase);
        this.confidenceThreshold = 0.70; // default is 70% similarity
        this.totalMemoryUsed = 0;
        this.topAlignment = new AlignmentResult(0.0, 0.0);
    }

    /**
     * constructor with custom parameters
     * @param aligner custom smith-waterman aligner
     * @param referenceDatabase map of virus family name to reference sequences
     * @param confidenceThreshold minimum confidence for classification from 0.0 to 1.0
     */
    public ViralClassifier(SequenceAligner aligner, Map<String, List<ViralSequence>> referenceDatabase,
                           double confidenceThreshold) {
        this.aligner = aligner;
        this.referenceDatabase = referenceDatabase;
        this.confidenceThreshold = confidenceThreshold;
        this.totalMemoryUsed = 0;
        this.topAlignment = new AlignmentResult(0.0, 0.0);
    }

    /**
     * classify a single viral sequence
     * @param unknownSequence the sequence to be classified
     * @return ClassificationResult with prediction and confidence scores
     */
    public ClassificationResult classify(ViralSequence unknownSequence) {
        long startTime = System.currentTimeMillis(); // for calculating processing time

        // validate input
        // if no sequence, use createUnknown to create an unknown viral sequence
        if (unknownSequence == null || unknownSequence.getSequence().isEmpty()) {
            return ClassificationResult.createUnknown(
                    unknownSequence != null ? unknownSequence.getName() : "null",
                    new HashMap<>(),
                    aligner.getAlgorithmName(),
                    System.currentTimeMillis() - startTime,
                    0
                    );
        }

        // calculate alignment scores against each virus family
        Map<String, Double> familyScores = calculateFamilyScores(unknownSequence);

        // determine best classification
        String bestFamily = findBestFamily(familyScores);
        double bestScore = familyScores.getOrDefault(bestFamily, 0.0);

        // check if prediction meets confidence threshold
        boolean isConfident = bestScore >= confidenceThreshold;
        String finalPrediction = isConfident ? bestFamily : "Unknown";

        // calculate time it took to process
        long processingTime = System.currentTimeMillis() - startTime;

        return new ClassificationResult(
                unknownSequence.getName(),
                finalPrediction,
                bestScore,
                isConfident,
                familyScores,
                aligner.getAlgorithmName(),
                processingTime,
                totalMemoryUsed
        );
    }

    /**
     * calculate alignment scores for each virus family
     * @param unknownSequence the unclassified sequence
     * @return Map<String, Double> familyScores the map of alignment scores for each sequence in the family
     */
    private Map<String, Double> calculateFamilyScores(ViralSequence unknownSequence) {
        Map<String, Double> familyScores = new HashMap<>();

        for (Map.Entry<String, List<ViralSequence>> familyEntry : referenceDatabase.entrySet()) {
            String familyName = familyEntry.getKey();
            List<ViralSequence> references = familyEntry.getValue();

            double bestScore = calculateBestScoreForFamily(unknownSequence, references);
            familyScores.put(familyName, bestScore);
        }
        return familyScores;
    }

    /**
     * find the highest alignment score for the unclassified sequence and the sequences in a viral family
     * @param unknownSequence the unclassified sequence
     * @param referenceSequences the sequences in a certain viral family
     * @return double the best normalized score for a virus family
     */
    private double calculateBestScoreForFamily(ViralSequence unknownSequence,
                                               List<ViralSequence> referenceSequences) {
        // variable to hold the current best score in the family
        double bestScore = 0.0;

        // find the number of sequences to check
        int sequencesToCheck = referenceSequences.size();

        // sort references by length because a longer sequence is likely to have better alignment
        List<ViralSequence> sortedReferences = new ArrayList<>(referenceSequences);
        sortedReferences.sort((a, b) -> Integer.compare(b.getLength(), a.getLength()));
        AlignmentResult result = new AlignmentResult(0.0, 0.0);

        // now go through the sequences to find the best match
        for (int i = 0; i < sequencesToCheck; i++) {
            ViralSequence reference = sortedReferences.get(i);

            try {
                // try to align the unknown sequence with the current sequence to compare in this family
                result = aligner.align(
                        unknownSequence.getSequence(),
                        reference.getSequence()
                );

                double normalizedScore = result.getNormalizedScore();

                if (normalizedScore > bestScore) {
                    bestScore = normalizedScore;
                    topAlignment = result;
                    System.out.println(result.getReferenceSequenceName() + ": " + result.getNormalizedScore());
                    System.out.println(bestScore);
                }

                // bestScore = Math.max(bestScore, normalizedScore); // see if the current alignment is the best so far
                totalMemoryUsed += result.getMemoryUsedBytes();
                // for logging memory use
                // System.out.println("Memory (bytes) used for classifying " + reference.getVirusFamily() + " with " + aligner.getAlgorithmName() + " number " + (i + 1) + ": " + result.getMemoryUsedBytes());
            }

            catch (Exception e) {
                // log error but continue aligning other references
                System.err.println(
                        "Error aligning with reference" + reference.getName() + " using " + aligner.getAlgorithmName() + " : " + e.getMessage()
                );
            }
        }
        // topAlignment = result;
        return bestScore;
    }

    // find virus family with the highest score
    private String findBestFamily(Map<String, Double> familyScores) {
        return familyScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    // getters
    public double getConfidenceThreshold() { return confidenceThreshold; }
    public SequenceAligner getAligner() { return aligner; }
    public AlignmentResult getTopAlignment() { return topAlignment; }
}







