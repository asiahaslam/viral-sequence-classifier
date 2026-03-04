package com.asiahaslam.viralclassifier.algorithms;

/**
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
        MaxScorePosition maxPos = findMaxScore(scoreMatrix);

        // calculate normalized score
        double maxPossible = scoringMatrix.getMaxPossibleScore(sequence1, sequence2);
        double normalizedScore = (maxPossible > 0) ? maxPos.score / maxPossible : 0.0;

        // traceback to find the best alignment of the 2 sequences
        // this will give us all the other information we need in addition to the max score and normalized score
        AlignmentResult result = traceback(scoreMatrix, sequence1, sequence2, maxPos);

        return new AlignmentResult(
                maxPos.score,
                normalizedScore,
                result.getAlignedSequence1(),
                result.getAlignedSequence2(),
                result.getStartPos1(),
                result.getStartPos2(),
                result.getEndPos1(),
                result.getEndPos2()
        );
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
        final double score;
        final int row;
        final int col;

        MaxScorePosition(double score1, int row1, int col1) {
            this.score = score1;
            this.row = row1;
            this.col = col1;
        }
    }

    // traceback to construct the optimal alignment of the 2 sequences
    private AlignmentResult traceback(double[][] matrix, String seq1, String seq2, MaxScorePosition maxPos) {
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

        return new AlignmentResult(
                0, 0, // we set the scores in AlignmentResult after we call this method
                alignedSeq1.toString(),
                alignedSeq2.toString(),
                startPos1,
                startPos2,
                endPos1,
                endPos2
        );
    }




}
