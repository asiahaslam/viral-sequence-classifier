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

    // checks if a DNA nucleotide is valid
    private boolean isValidNucleotide(char nucleotide) {
        return switch (nucleotide) {
            case 'A', 'G', 'T', 'C', 'N' -> true;
            default -> false;
        };
    }

    // get the alignment score for a nucleotide pair
    public int getScore(char nuc1, char nuc2) {
        // normalize to uppercase
        nuc1 = Character.toUpperCase(nuc1);
        nuc2 = Character.toUpperCase(nuc2);

        // if either nucleotide is unknown, treat as a mismatch
        if (nuc1 == 'N' || nuc2 == 'N') return mismatchScore;

        // make sure nucleotides are valid
        if (isValidNucleotide(nuc1) || isValidNucleotide((nuc2))) {
            throw new IllegalArgumentException(
                    String.format("Invalid nucleotide pair: '%c' and '%c'", nuc1, nuc2)
            );
        }

        // return match score if nucleotides match
        if (nuc1 == nuc2) return matchScore;

        // return mismatch score if nucleotides do not match
        else return mismatchScore;
    }

    // get the maximum possible score that would occur if 2 sequences are perfectly aligned
    public int getMaxPossibleScore(String sequence1, String sequence2) {
        int minLength = Math.min(sequence1.length(), sequence2.length());
        return minLength * matchScore;
    }

    // getter for gap penalty (used for insertions and deletions)
    public int getGapPenalty() {
        return gapPenalty;
    }

    // getter for match score
    public int getMatchScore() {
        return matchScore;
    }

    // getter for mismatch score
    public int getMismatchScore() {
        return mismatchScore;
    }
}
