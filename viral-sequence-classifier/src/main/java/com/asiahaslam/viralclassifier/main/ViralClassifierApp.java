package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.sequences.ViralSequence;
import com.asiahaslam.viralclassifier.sequences.FastaParser;
import com.asiahaslam.viralclassifier.algorithms.ScoringMatrix;

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

        // test scoring matrix
        ScoringMatrix matrix = new ScoringMatrix();

        System.out.println("Testing scoring matrix:");
        System.out.println(matrix);

        // test nucleotide scoring
        System.out.println("A-A: " + matrix.getScore('A', 'A'));
        System.out.println("A-T: " + matrix.getScore('A', 'T'));
        System.out.println("G-g: " + matrix.getScore('G', 'g'));
        System.out.println("T-N: " + matrix.getScore('T', 'N'));
        System.out.println("Gap penalty: " + matrix.getGapPenalty());

        // test max possible score
        System.out.println("Max possible score for ATCG and ATCGAA: " +
                matrix.getMaxPossibleScore("ATCG", "ATCGAA"));

    }
}
