package com.asiahaslam.viralclassifier.algorithms;

public class ScoringMatrix {
    private int matchScore;
    private int mismatchScore;
    private int gapPenalty;

    // this default constructor sets scoring parameters that work well for DNA sequences
    public ScoringMatrix() {
        this.matchScore = 2;
        this.mismatchScore = -1;
        this.gapPenalty = -1;
    }

    // this constructor allows for custom scoring parameters
    public ScoringMatrix(int match, int mismatch, int gap) {
        this.matchScore = match;
        this.mismatchScore = mismatch;
        this.gapPenalty = gap;
    }
}
