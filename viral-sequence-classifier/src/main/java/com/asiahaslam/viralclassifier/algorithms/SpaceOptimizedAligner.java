package com.asiahaslam.viralclassifier.algorithms;

/**
 * space-optimized Smith-Waterman algorithm
 * uses just two rows instead of the full matrix
 * O(mn) time complexity
 * O(min(m,n)) space complexity
 * con compute the optimal score but cannot reconstruct the full alignment
 */
public class SpaceOptimizedAligner extends SequenceAligner {

    public SpaceOptimizedAligner() {
        super();
    }

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


        return new AlignmentResult();
    }
}
