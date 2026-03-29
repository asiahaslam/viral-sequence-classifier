package com.asiahaslam.viralclassifier.algorithms;

import javax.sound.midi.Sequence;

// abstract base class for sequence alignment algorithms (Smith-Waterman variants)
public abstract class SequenceAligner {
    protected final ScoringMatrix scoringMatrix;

    // constructor with default scoring matrix
    public SequenceAligner() {
        this.scoringMatrix = new ScoringMatrix();
    }

    // constructor with custom scoring matrix
    public SequenceAligner(ScoringMatrix scoringMatrix) {
        this.scoringMatrix = scoringMatrix;
    }

    /**
     * Perform sequence alignment (must be implemented by all subclasses)
     * @param sequence1 first DNA sequence
     * @param sequence2 second DNA sequence
     * @return AlignmentResult containing score and alignment details
     */
    public abstract AlignmentResult align(String sequence1, String sequence2);

    /**
     * get the name of the alignment algorithm
     * @return algorithm name for identification
     */
    public abstract String getAlgorithmName();

    /**
     * get time complexity description
     * @return String describing time complexity
     */
    public abstract String getTimeComplexity();

    /**
     * get space complexity description
     * @return String describing space complexity
     */
    public abstract String getSpaceComplexity();

    // getters
    public ScoringMatrix getScoringMatrix() {
        return scoringMatrix;
    }
}
