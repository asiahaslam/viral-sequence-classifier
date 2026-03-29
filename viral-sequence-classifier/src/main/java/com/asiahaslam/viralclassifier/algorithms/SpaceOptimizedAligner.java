package com.asiahaslam.viralclassifier.algorithms;

/**
 * space-optimized Smith-Waterman algorithm
 * uses just two rows instead of the full matrix
 * O(mn) time complexity
 * O(min(m,n)) space complexity
 * con compute the optimal score but cannot reconstruct the full alignment
 */
public class SpaceOptimizedAligner extends SequenceAligner {

    /**
     * constructor with default scoring matrix
     */
    public SpaceOptimizedAligner() {
        super();
    }

    /**
     * constructor with custom scoring matrix
     */
    public SpaceOptimizedAligner(ScoringMatrix scoringMatrix) {
        super(scoringMatrix);
    }

    @Override
    public String getAlgorithmName() {
        return "Space-Optimized Smith-Waterman";
    }

    @Override
    public String getTimeComplexity() {
        return "O(m x n)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(min(m, n))";
    }

    @Override
    public AlignmentResult align(String sequence1, String sequence2) {
        if (sequence1 == null || sequence1.isEmpty() || sequence2.isEmpty()) {
            return new AlignmentResult(0.0, 0.0);
        }

        int rows = sequence1.length() + 1;
        int cols = sequence2.length() + 1;

        sequence1 = sequence1.toUpperCase();
        sequence2 = sequence2.toUpperCase();

        // just store two rows at a time: previous and current
        double[] prevRow = new double[cols];
        double[] currRow = new double[cols];

        // initialize first row
        for (int j = 0; j < cols; j++) {
            prevRow[j] = 0;
        }

        double maxScore = 0.0;
        int maxI = 0, maxJ = 0;

        // fill matrix row by row
        for (int i = 1; i < rows; i++) {
            currRow[0] = 0; // initialize the first column

            for (int j = 1; j < cols; j++) {
                char char1 = sequence1.charAt(i - 1);
                char char2 = sequence2.charAt(j - 1);

                // calculate scores for the three operations
                double match = prevRow[j - 1] + scoringMatrix.getScore(char1, char2);
                double delete = prevRow[j] + scoringMatrix.getGapPenalty();
                double insert = currRow[j - 1] + scoringMatrix.getGapPenalty();

                // Smith-Waterman algorithm: take maximum or 0
                currRow[j] = Math.max(0, Math.max(match, Math.max(delete, insert)));

                // track the maximum score and its position
                if (currRow[j] > maxScore) {
                    maxScore = currRow[j];
                    maxI = i;
                    maxJ = j;
                }
            }

            // swap the rows for the next iteration
            double[] temp = prevRow;
            prevRow = currRow;
            currRow = temp;
        }

        // calculate the normalized score
        double maxPossible = scoringMatrix.getMaxPossibleScore(sequence1, sequence2);
        double normalizedScore = (maxPossible > 0) ? maxScore / maxPossible : 0.0;

        // create a simplified result with no full alignment reconstruction
        return new AlignmentResult(
                maxScore,
                normalizedScore,
                "", // no alignedSeq1 (would need full matrix)
                "", // no alignedSeq2 (would need full matrix)
                0, // no startPos1 (would need traceback)
                0, // no startPos2 (would need traceback)
                maxI - 1, // estimate end position sequence1
                maxJ -1 // estimate end position sequence2
        );
    }
}
