package com.asiahaslam.viralclassifier.algorithms;

import java.util.Arrays;

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

        return "Banded Smith-Waterman (band = " + bandWidth + ")";
    }

    @Override
    public String getTimeComplexity() {

        return "O(k x n) where k = " + bandWidth;
    }

    @Override
    public String getSpaceComplexity() {

        return "O(k) where k = " + bandWidth;
    }

    @Override
    public AlignmentResult align(String sequence1, String sequence2) {
        if (sequence1 == null || sequence2 == null || sequence1.isEmpty() || sequence2.isEmpty()) {
            return new AlignmentResult(0.0, 0.0);
        }

        sequence1 = sequence1.toUpperCase();
        sequence2 = sequence2.toUpperCase();

        int m = sequence1.length();
        int n = sequence2.length();

        // just store the band as a 1-dimensional array
        int bandSize = 2 * bandWidth + 1;
        double[] prevDiagonal = new double[bandSize];
        double[] currDiagonal = new double[bandSize];


        // initialize the matrix
        Arrays.fill(prevDiagonal, Double.MIN_VALUE);
        Arrays.fill(currDiagonal, Double.MIN_VALUE);
        prevDiagonal[bandWidth] = 0.0; // center of band is [0,0]

        double maxScore = 0.0;
        int maxI = 0;
        int maxJ = 0;

        // fill in the banded matrix
        for (int i = 1; i <= m; i++) {
            Arrays.fill(currDiagonal, Double.MIN_VALUE);

            int jStart = Math.max(1, i - bandWidth);
            int jEnd = Math.min(n, i + bandWidth);

            for (int j = jStart; j <= jEnd; j++) {
                // convert to band coordinates so we know where we are in the matrix
                int bandJ = j - i + bandWidth;

                // skip if outside band boundaries
                if (bandJ < 0 || bandJ >= bandSize) {
                    continue;
                }

                char char1 = sequence1.charAt(i - 1);
                char char2 = sequence2.charAt(j - 1);

                double match = Double.MIN_VALUE;
                double delete = Double.MIN_VALUE;
                double insert = Double.MIN_VALUE;

                // match/mismatch (diagonal)
                int prevBandJ = (j - 1) - (i - 1) + bandWidth;
                if (prevBandJ >= 0 && prevBandJ < bandSize && prevDiagonal[prevBandJ] != Double.MIN_VALUE) {
                    match = prevDiagonal[prevBandJ] + scoringMatrix.getScore(char1, char2);
                }

                // deletion (up)
                int upBandJ = j - (i - 1) + bandWidth;
                if (upBandJ >= 0 && upBandJ < bandSize && prevDiagonal[upBandJ] != Double.MIN_VALUE) {
                    delete = prevDiagonal[upBandJ] + scoringMatrix.getGapPenalty();
                }

                // insertion (left)
                if (j > jStart) {
                    int leftBandJ = (j - 1) - i + bandWidth;
                    if (leftBandJ >= 0 && leftBandJ < bandSize && currDiagonal[leftBandJ] != Double.MIN_VALUE) {
                        insert = currDiagonal[leftBandJ] + scoringMatrix.getGapPenalty();
                    }
                }

                // take the maximum score or 0 (Smith-Waterman algorithm)
                double score = Math.max(0, Math.max(match, Math.max(delete, insert)));

                // store computed score
                currDiagonal[bandJ] = score;

                // track maximum
                if (score > maxScore) {
                    maxScore = score;
                    maxI = i;
                    maxJ = j;
                }
            }
            // swap diagonals
            double[] temp = prevDiagonal;
            prevDiagonal = currDiagonal;
            currDiagonal = temp;
        }

        // calculate normalized score
        double maxPossible = scoringMatrix.getMaxPossibleScore(sequence1, sequence2);
        double normalizedScore = (maxPossible > 0) ? maxScore / maxPossible : 0.0;

        // simplified result (it would be very complex to do traceback in banded matrix)
        return new AlignmentResult(
                maxScore,
                normalizedScore,
                "", // no full alignment so no alignedSeq1
                "", // no full alignment so no alignedSeq2
                0,
                0,
                maxI - 1,
                maxJ - 1
        );
    }

    public int getBandWidth() {
        return bandWidth;
    }
}
