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

    public ClassificationResult classify(ViralSequence unknownSequence) {
        long startTime = System.currentTimeMillis(); // for calculating processing time

        // validate input

        // calculate alignment scores against each virus family

        // determine best classification

        // check if prediction meets confidence threshold
    }

    // getters
    public double getConfidenceThreshold() { return confidenceThreshold; }
    public int getMaxReferencesPerFamily() { return maxReferencesPerFamily; }
    public SmithWatermanAligner getAligner() { return aligner; }

}







