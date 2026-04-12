package com.asiahaslam.viralclassifier.algorithms;

// this class provides a way to store Smith-Waterman alignment results

public class AlignmentResult {
    private final double alignmentScore;
    private final double normalizedScore;
    private final String alignedSequence1;
    private final String alignedSequence2;
    private final int startPos1;
    private final int startPos2;
    private final int endPos1;
    private final int endPos2;
    private final int alignmentLength;
    private final long memoryUsedBytes;

    // constructor
    public AlignmentResult(double alignmentScr, double normalizedScr, String alignedSeq1, String alignedSeq2, int startPs1, int startPs2, int endPs1, int endPs2, long memoryUsedBytes) {
        this.alignmentScore = alignmentScr;
        this.normalizedScore = normalizedScr;
        this.alignedSequence1 = alignedSeq1;
        this.alignedSequence2 = alignedSeq2;
        this.startPos1 = startPs1;
        this.startPos2 = startPs2;
        this.endPos1 = endPs1;
        this.endPos2 = endPs2;
        this.alignmentLength = alignedSequence1.length();
        this.memoryUsedBytes = memoryUsedBytes;
    }

    // basic constructor for development purposes (uses only scores)
    public AlignmentResult(double alignmentScr, double normalizedScr) {
        this(alignmentScr, normalizedScr, "", "", 0, 0, 0, 0, 0);
    }

    //
    public String getFormattedAlignment() {
        return String.format("Score: %.2f (normalized: %.3f)", alignmentScore, normalizedScore);
    }

    // getters
    public double getAlignmentScore() { return alignmentScore; }
    public double getNormalizedScore() { return normalizedScore; }
    public String getAlignedSequence1() { return alignedSequence1; }
    public String getAlignedSequence2() { return alignedSequence2; }
    public int getStartPos1() { return startPos1; }
    public int getStartPos2() { return startPos2; }
    public int getEndPos1() { return endPos1; }
    public int getEndPos2() { return endPos2; }
    public int getAlignmentLength() { return alignmentLength; }
    public long getMemoryUsedBytes() { return memoryUsedBytes; }
}