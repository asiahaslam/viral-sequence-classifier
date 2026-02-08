package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.sequences.ViralSequence;
import com.asiahaslam.viralclassifier.sequences.FastaParser;

public class ViralClassifierApp {
    public static void main(String[] args) {
        try {
            // parse a FASTA file
            ViralSequence sequence = FastaParser.parseFile("data/influenza_b.fasta");

            System.out.println("Sequence: " + sequence.getSequence());
        }
        catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
