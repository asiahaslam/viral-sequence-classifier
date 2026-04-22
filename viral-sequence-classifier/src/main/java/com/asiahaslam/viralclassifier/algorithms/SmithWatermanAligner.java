package com.asiahaslam.viralclassifier.algorithms;

/*
 * This class will use dynamic programming (typical technique for this algorithm)
 * to implement the Smith-Waterman local sequence alignment algorithm.
 * This algorithm helps us find the best possible alignment between the sequences
 * where there are the highest number of matches between the nucleotides.
 * First, we create a Smith-Waterman scoring matrix from the 2 DNA sequences.
 * This uses the scoring matrix class to find the match score between each possible pair of nucleotides.
 * Next, we find the max score in the matrix and its position.
 * Then we do a traceback to find the optimal alignment for the 2 sequences
 * starting with the position of the max score.
 */

import com.asiahaslam.viralclassifier.utils.MemoryMeasurement;

/**
 * standard Smith-Waterman local sequence alignment algorithm
 * uses full dynamic programming matrix
 * O(mn) time and space
 */
public class SmithWatermanAligner extends SequenceAligner {

    // this constructor uses the default scoring matrix
    public SmithWatermanAligner() {
        super();
    }

    // this constructor uses a custom scoring matrix
    public SmithWatermanAligner(ScoringMatrix scoringMatrix) {
        super(scoringMatrix);
    }

    @Override
    public String getAlgorithmName() {
        return "Standard Smith-Waterman";
    }

    @Override
    public String getTimeComplexity() {
        return "O(m x n)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(m x n)";
    }

    @Override
    public AlignmentResult align(String sequence1, String sequence2) {
        if (sequence1 == null || sequence2 == null || sequence1.isEmpty() || sequence2.isEmpty()) {
            return new AlignmentResult(0.0, 0.0);
        }

        // convert sequences to uppercase
        String finalSequence1 = sequence1.toUpperCase();
        String finalSequence2 = sequence2.toUpperCase();

        // store results to capture from lambda
        final AlignmentResultData resultData = new AlignmentResultData();

        /*// for debugging: test the memory measurement
        long testMemory = AccurateMemoryMeasurement.measureAllocatedMemory(() -> {
            // something we know the size of
            double[] testArray = new double[1000]; // Should be 8000 bytes
            for (int i = 0; i < testArray.length; i++) {
                testArray[i] = i; // Force actual allocation
            }
        });
        System.out.println("Test allocation: " + testMemory + " bytes (should be ~8000)");*/

        // measure memory of just the core algorithm
        long memoryUsed = MemoryMeasurement.measureAllocatedMemory(() -> {
            performSmithWatermanAlignment(finalSequence1, finalSequence2, resultData);
        });

        // calculate normalized score
        double maxPossible = scoringMatrix.getMaxPossibleScore(sequence1, sequence2);
        double normalizedScore = (maxPossible > 0) ? resultData.alignmentScore / maxPossible : 0.0;

        return new AlignmentResult(
                resultData.alignmentScore,
                normalizedScore,
                resultData.alignedSequence1,
                resultData.alignedSequence2,
                resultData.startPos1,
                resultData.startPos2,
                resultData.endPos1,
                resultData.endPos2,
                memoryUsed
        );
    }

    private void performSmithWatermanAlignment(String sequence1, String sequence2, AlignmentResultData result) {
        // create the scoring matrix
        double[][] scoreMatrix = createScoringMatrix(sequence1, sequence2);

        // find the highest score in the matrix
        MaxScorePosition maxPos = findMaxScore(scoreMatrix);
        result.alignmentScore = maxPos.score;

        // traceback to find the best alignment of the 2 sequences
        // this will give us all the other information we need in addition to the max score and normalized score
        AlignmentData tracebackResult = traceback(scoreMatrix, sequence1, sequence2, maxPos);
        result.alignedSequence1 = tracebackResult.alignedSequence1;
        result.alignedSequence2 = tracebackResult.alignedSequence2;
        result.startPos1 = tracebackResult.startPos1;
        result.startPos2 = tracebackResult.startPos2;
        result.endPos1 = tracebackResult.endPos1;
        result.endPos2 = tracebackResult.endPos2;
    }

    /**
     * Helper class to capture results from the lambda
     */
    private static class AlignmentResultData {
        double alignmentScore = 0.0;
        String alignedSequence1 = "";
        String alignedSequence2 = "";
        int startPos1 = 0;
        int startPos2 = 0;
        int endPos1 = 0;
        int endPos2 = 0;
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
                // find the nucleotides associated with this cell in the matrix
                char char1 = seq1.charAt(i - 1);
                char char2 = seq2.charAt(j - 1);

                // calculate scores for the 3 possible operations
                double match = matrix[i - 1][j - 1] + scoringMatrix.getScore(char1, char2);
                double delete = matrix[i - 1][j] + scoringMatrix.getGapPenalty();
                double insert = matrix[i][j - 1] + scoringMatrix.getGapPenalty();

                // take maximum of the 3 operations above or 0 if they are all negative
                // this is the value for the current index in the matrix
                matrix[i][j] = Math.max(0, Math.max(match, Math.max(delete, insert)));
            }
        }
        return matrix;
    }

    // find the position of the highest score in the scoring matrix
    private MaxScorePosition findMaxScore(double[][] matrix) {
        double maxScore = 0;
        int maxI = 0;
        int maxJ = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] > maxScore) {
                    maxScore = matrix[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }
        return new MaxScorePosition(maxScore, maxI, maxJ);
    }

    // helper class to store the position of the max value in the matrix
    private static class MaxScorePosition {
        double score;
        int row;
        int col;

        MaxScorePosition(double score1, int row1, int col1) {
            this.score = score1;
            this.row = row1;
            this.col = col1;
        }
    }

    // traceback to construct the optimal alignment of the 2 sequences
    private AlignmentData traceback(double[][] matrix, String seq1, String seq2, MaxScorePosition maxPos) {
        StringBuilder alignedSeq1 = new StringBuilder();
        StringBuilder alignedSeq2 = new StringBuilder();

        // variables to iterate through matrix
        int i = maxPos.row;
        int j = maxPos.col;

        // set end position variables to final position in 0-based sequence
        int endPos1 = i - 1;
        int endPos2 = j - 1;

        // trace back through matrix until we reach either the edge of the matrix or a cell with score 0
        while (i > 0 && j > 0 && matrix[i][j] > 0) {
            // find nucleotides associated with the current matrix cell
            char char1 = seq1.charAt(i - 1);
            char char2 = seq2.charAt(j - 1);

            // score in current position
            double currentScore = matrix[i][j];
            // calculate score in the cell one up and one to the left
            double diagonalScore = matrix[i - 1][j - 1] + scoringMatrix.getScore(char1, char2);
            // calculate score in the cell directly above
            double upScore = matrix[i - 1][j] + scoringMatrix.getGapPenalty();
            // calculate score in the cell directly to the left
            double leftScore = matrix[i][j - 1] + scoringMatrix.getGapPenalty();

            // find which operation led to the current cell
            if (Math.abs(currentScore - diagonalScore) < 1e-9) {
                // it was a match or mismatch
                // no need to change the position of either nucleotide in the sequence
                alignedSeq1.insert(0, char1);
                alignedSeq2.insert(0, char2);
                i--;
                j--;
            }
            else if (Math.abs(currentScore - upScore) < 1e-9) {
                // it was a deletion (a gap in sequence 2)

                alignedSeq1.insert(0, char1);
                // we need to add a gap in sequence 2
                alignedSeq2.insert(0, '-');
                // only need to decrement i so we don't skip the next nucleotide in sequence 2
                i--;
            }
            else if (Math.abs(currentScore - leftScore) < 1e-9) {
                // it was an insertion (gap in sequence 1)
                // we need to add a gap in sequence 1
                alignedSeq1.insert(0, '-');
                alignedSeq2.insert(0, char2);
                // only need to decrement j so we don't skip the next nucleotide in sequence 1
                j--;
            }
            else {
                // to avoid an infinite loop, just in case
                break;
            }
        }
        // once we break out of the loop, i and j are the starting positions for the aligned sequences
        int startPos1 = i;
        int startPos2 = j;

        return new AlignmentData(
                alignedSeq1.toString(),
                alignedSeq2.toString(),
                startPos1,
                startPos2,
                endPos1,
                endPos2
        );
    }

    // helper class for traceback results
        private record AlignmentData(String alignedSequence1, String alignedSequence2, int startPos1, int startPos2,
                                     int endPos1, int endPos2) {
    }
}
