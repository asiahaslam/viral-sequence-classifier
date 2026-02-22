package com.asiahaslam.viralclassifier.algorithms;

/**
 * this class will use dynamic programming to implement the
 * Smith-Waterman local sequence alignment algorithm
 * dynamic programming is a good technique here because
 * we are trying to maximize alignment
 */

public class SmithWatermanAligner {
    private final ScoringMatrix scoringMatrix;

    // this constructor uses the default scoring matrix
    public SmithWatermanAligner() {
        this.scoringMatrix = new ScoringMatrix();
    }

    // this constructor uses a custom scoring matrix
    public SmithWatermanAligner(ScoringMatrix scoringMatrix1) {
        this.scoringMatrix = scoringMatrix1;
    }

    // helper class to store position and max value in matrix
    private static class MaxScorePosition {
        final double score;
        final int row;
        final int col;

        MaxScorePosition(double score1, int row1, int col1) {
            this.score = score1;
            this.row = row1;
            this.col = col1;
        }
    }
}
