package com.asiahaslam.viralclassifier.algorithms;

import com.asiahaslam.viralclassifier.utils.MemoryMeasurement;

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

        String finalSequence1 = sequence1.toUpperCase();
        String finalSequence2 = sequence2.toUpperCase();

        final AlignmentResultData resultData = new AlignmentResultData();

        long memoryUsed = MemoryMeasurement.measureAllocatedMemory(() -> {
            performBandedAlignment(finalSequence1, finalSequence2, resultData);
        });

        // calculate normalized score
        double maxPossible = scoringMatrix.getMaxPossibleScore(sequence1, sequence2);
        double normalizedScore = (maxPossible > 0) ? resultData.maxScore / maxPossible : 0.0;

        // simplified result (cannot do traceback with this implementation)
        return new AlignmentResult(
                resultData.maxScore,
                normalizedScore,
                "", // no full alignment so no alignedSeq1
                "", // no full alignment so no alignedSeq2
                0,
                0,
                resultData.maxI - 1,
                resultData.maxJ - 1,
                memoryUsed
        );
    }

    // helper class to hold results from lambda
    private static class AlignmentResultData {
        double maxScore = 0.0;
        int maxI = 0;
        int maxJ = 0;
    }

    // core algorithm logic separated for memory measurement
    private void performBandedAlignment(String sequence1, String sequence2, AlignmentResultData result) {
        int m = sequence1.length();
        int n = sequence2.length();

        // just store two rows of the band at a time
        int bandSize = 2 * bandWidth + 1;
        double[] prevDiagonal = new double[bandSize];
        double[] currDiagonal = new double[bandSize];

        // initialize the matrix
        Arrays.fill(prevDiagonal, -1);
        Arrays.fill(currDiagonal, -1);
        prevDiagonal[bandWidth] = 0.0; // center of band is [0,0]

        // fill in the banded matrix
        for (int i = 1; i <= m; i++) {
            Arrays.fill(currDiagonal, -1);

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

                double match = -1;
                double delete = -1;
                double insert = -1;

                // match/mismatch (diagonal)
                int prevBandJ = (j - 1) - (i - 1) + bandWidth;
                if (prevBandJ >= 0 && prevBandJ < bandSize && prevDiagonal[prevBandJ] != -1) {
                    match = prevDiagonal[prevBandJ] + scoringMatrix.getScore(char1, char2);
                }

                // deletion (up)
                int upBandJ = j - (i - 1) + bandWidth;
                if (upBandJ >= 0 && upBandJ < bandSize && prevDiagonal[upBandJ] != -1) {
                    delete = prevDiagonal[upBandJ] + scoringMatrix.getGapPenalty();
                }

                // insertion (left)
                if (j > jStart) {
                    int leftBandJ = (j - 1) - i + bandWidth;
                    if (leftBandJ >= 0 && leftBandJ < bandSize && currDiagonal[leftBandJ] != -1) {
                        insert = currDiagonal[leftBandJ] + scoringMatrix.getGapPenalty();
                    }
                }

                // take the maximum score or 0 (Smith-Waterman algorithm)
                double score = Math.max(0, Math.max(match, Math.max(delete, insert)));

                // store computed score
                currDiagonal[bandJ] = score;

                // track maximum
                if (score > result.maxScore) {
                    result.maxScore = score;
                    result.maxI = i;
                    result.maxJ = j;
                }
            }
            // swap diagonals
            double[] temp = prevDiagonal;
            prevDiagonal = currDiagonal;
            currDiagonal = temp;
        }
    }

    public int getBandWidth() {
        return bandWidth;
    }
}
