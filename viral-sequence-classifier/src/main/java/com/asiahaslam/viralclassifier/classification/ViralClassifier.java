package com.asiahaslam.viralclassifier.classification;

import com.asiahaslam.viralclassifier.algorithms.SmithWatermanAligner;
import com.asiahaslam.viralclassifier.algorithms.AlignmentResult;
import com.asiahaslam.viralclassifier.sequences.ViralSequence;

import java.util.*;

// this class classifies unknown sequences using Smith-Waterman alignment
// it compares the new sequence against a reference database to predict likely virus family
public class ViralClassifier {
    private final SmithWatermanAligner aligner;
    private final Map<String, List<ViralSequence>> referenceDatabase;
    private final double confidenceThreshold;
    private final int maxReferencesPerFamily;

    // constructor with default parameters
    public ViralClassifier(Map<String, List<ViralSequence>> referenceDatabase) {
        this.aligner = new SmithWatermanAligner();
        this.referenceDatabase = new HashMap<>(referenceDatabase);
        this.confidenceThreshold = 0.70; // default is 70% similarity
        this.maxReferencesPerFamily = 5; // limit to 5 references to improve performance
    }

    /**
     * constructor with custom parameters
     * @param aligner custom smith-waterman aligner
     * @param referenceDatabase map of virus family name to reference sequences
     * @param confidenceThreshold minimum confidence for classification from 0.0 to 1.0
     * @param maxReferencesPerFamily max reference sequences to check per family
     */
    public ViralClassifier(SmithWatermanAligner aligner, Map<String, List<ViralSequence>> referenceDatabase,
                           double confidenceThreshold, int maxReferencesPerFamily) {
        this.aligner = aligner;
        this.referenceDatabase = referenceDatabase;
        this.confidenceThreshold = confidenceThreshold;
        this.maxReferencesPerFamily = maxReferencesPerFamily;
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
                    "Smith-Waterman",
                    System.currentTimeMillis() - startTime
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

        long processingTime = System.currentTimeMillis() - startTime;

        return new ClassificationResult(
                unknownSequence.getName(),
                finalPrediction,
                bestScore,
                isConfident,
                familyScores,
                "Smith-Waterman",
                processingTime
        );
    }

    // calculate alignment scores for each virus family

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
        // find the number of sequences to check: either all sequences or the set limit, whichever is smaller
        int sequencesToCheck = Math.min(referenceSequences.size(), maxReferencesPerFamily);

        // sort references by length because a longer sequence is likely to have better alignment
        List<ViralSequence> sortedReferences = new ArrayList<>(referenceSequences);
        sortedReferences.sort((a, b) -> Integer.compare(b.getLength(), a.getLength()));

        // now go through the sequences to find the best match
        for (int i = 0; i < sequencesToCheck; i++) {
            ViralSequence reference = sortedReferences.get(i);

            try {
                // try to align the unknown sequence with the current sequence to compare in this family
                AlignmentResult result = aligner.align(
                        unknownSequence.getSequence(),
                        reference.getSequence()
                );

                double normalizedScore = result.getNormalizedScore();
                bestScore = Math.max(bestScore, normalizedScore); // see if the current alignment is the best so far
            }

            catch (Exception e) {
                // log error but continue aligning other references
                System.err.println("Error aligning with reference" + reference.getName() + " : " + e.getMessage());
            }
        }
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
    public int getMaxReferencesPerFamily() { return maxReferencesPerFamily; }
    public SmithWatermanAligner getAligner() { return aligner; }

}







