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

    public AlignmentResult align(String sequence1, String sequence2) {
        if (sequence1 == null || sequence2 == null || sequence1.isEmpty() || sequence2.isEmpty()) {
            return new AlignmentResult(0.0, 0.0);
        }

        // convert sequences to uppercase
        sequence1 = sequence1.toUpperCase();
        sequence2 = sequence2.toUpperCase();

        // create scoring matrix
        double[][] scoreMatrix = createScoringMatrix(sequence1, sequence2);

        // find position of max score

        // calculate normalized score

        // traceback to find actual alignment
    }

    private double[][] createScoringMatrix(String seq1, String seq2) {
        int rows = seq1.length() + 1;
        int cols = seq2.length() + 1;

        // create matrix
        double[][] matrix = new double[rows][cols];

        // initialize first row and column to 0 (always first step for Smith-Waterman scoring matrix)
        for (int i = 0; i < rows; i++) {
            matrix[i][0] = 0;
        }
        for (int j = 0; j < cols; j++) {
            matrix[0][j] = 0;
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                /* find the corresponding characters for this row and column of the matrix
                from the two sequences */
                char char1 = seq1.charAt(i - 1);
                char char2 = seq2.charAt(j - 1);

                // calculate scores for the 3 possible operations
                double match = matrix[i - 1][j - 1] + scoringMatrix.getScore(char1, char2);
                double delete = matrix[i - 1][j] + scoringMatrix.getGapPenalty();
                double insert = matrix[i][j - 1] + scoringMatrix.getGapPenalty();

                // take maximum of the 3 operations above or 0 if they are all negative
                matrix[i][j] = Math.max(0, Math.max(match, Math.max(delete, insert)));
            }
        }
        return matrix;
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
