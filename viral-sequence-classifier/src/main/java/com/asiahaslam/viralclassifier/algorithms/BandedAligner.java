package com.asiahaslam.viralclassifier.algorithms;

/**
 * Banded Smith-Waterman aligner
 * Only computes the alignment within a diagonal band
 * O(kn) time and space complexity
 * It assumes the sequences are similar and the optimal alignment lies near the main diagonal
 */
public class BandedAligner extends SequenceAligner {
    private final int bandWidth;

    /**
     * constructor with default scoring matrix and default band width
     */
    public BandedAligner() {
        super();
        this.bandWidth = 20; // set default band width
    }

    /**
     * constructor with custom band width and custom scoring matrix
     */
    public BandedAligner(int bandWidth, ScoringMatrix scoringMatrix) {
        super(scoringMatrix);
        this.bandWidth = bandWidth;
    }

    /**
     * constructor with default scoring matrix and custom band width
     */
    public BandedAligner(int bandWidth) {
        super();
        this.bandWidth = bandWidth;
    }

    @Override
    public String getAlgorithmName() {
        return "Banded Smith=Waterman (band=" + bandWidth + ")";
    }

    @Override
    public String getTimeComplexity() {
        return "O(k x n) where k =" + bandWidth;
    }

    @Override
    public String getSpaceComplexity() {
        return "O(k x n) where k =" + bandWidth;
    }

    @Override
    public AlignmentResult align(String sequence1, String sequence2) {

        return new AlignmentResult(0.0, 0.0);
    }
}
