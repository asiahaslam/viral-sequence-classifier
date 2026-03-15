package com.asiahaslam.viralclassifier.sequences;

public class ViralSequence {
    private String sequence;
    private String virusFamily;
    private String name;

    // constructors
    public ViralSequence(String name, String sequence, String virusFamily) {
        this.name = name;
        this.sequence = sequence.toUpperCase();
        this.virusFamily = virusFamily;
    }

    public ViralSequence() {
        this.name = null;
        this.sequence = null;
        this.virusFamily = null;
    }

    // getters
    public String getSequence() {
        return sequence;
    }
    public String getVirusFamily() {
        return virusFamily;
    }
    public String getName() {
        return name;
    }

    // setters
    public void setSequence(String seq) {
        this.sequence = seq;
    }
    public void setVirusFamily(String family) {
        this.virusFamily = family;
    }
    public void setVirusName(String nm) {
        this.name = nm;
    }

    // get sequence length
    public int getLength() {
        return sequence.length();
    }

    // TODO: maybe override toString method for this class
}
