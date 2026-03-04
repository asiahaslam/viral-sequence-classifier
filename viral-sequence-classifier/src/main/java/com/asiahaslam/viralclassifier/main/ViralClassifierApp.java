package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.algorithms.AlignmentResult;
import com.asiahaslam.viralclassifier.algorithms.SmithWatermanAligner;
import com.asiahaslam.viralclassifier.sequences.ViralSequence;
import com.asiahaslam.viralclassifier.sequences.FastaParser;
import com.asiahaslam.viralclassifier.algorithms.ScoringMatrix;

public class ViralClassifierApp {
    static void main(String[] args) {
        // TEST 1: test FASTA parsing
        try {
            // parse a FASTA file
            ViralSequence sequence = FastaParser.parseFile("data/influenza_b.fasta");

            System.out.println("Sequence: " + sequence.getSequence());
        }
        catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        // TEST 2: test scoring
        // scoring matrix
        ScoringMatrix matrix = new ScoringMatrix();

        System.out.println("Testing scoring matrix:");
        System.out.println(matrix);

        // nucleotide scoring
        System.out.println("A-A: " + matrix.getScore('A', 'A'));
        System.out.println("A-T: " + matrix.getScore('A', 'T'));
        System.out.println("G-g: " + matrix.getScore('G', 'g'));
        System.out.println("T-N: " + matrix.getScore('T', 'N'));
        System.out.println("Gap penalty: " + matrix.getGapPenalty());

        // max possible score
        System.out.println("Max possible score for ATCG and ATCGAA: " +
                matrix.getMaxPossibleScore("ATCG", "ATCGAA"));

        // TEST 3: test smith-waterman aligner
        SmithWatermanAligner aligner = new SmithWatermanAligner();

        // perfect match
        AlignmentResult result1 = aligner.align("ATCG", "ATCG");
        System.out.println("Perfect match test: ");
        System.out.println(result1.getFormattedAlignment());

        // mostly similar sequences
        AlignmentResult result2 = aligner.align("ATCGATCG", "ATCGTTCG");
        System.out.println("Very similar test: ");
        System.out.println(result2.getFormattedAlignment());

        // sequences of different length (requiring gap)
        AlignmentResult result3 = aligner.align("ATCGATCG", "ATCGTCG");
        System.out.println("One gap test: ");
        System.out.println(result3.getFormattedAlignment());

        // sequences with no similarity
        AlignmentResult result4 = aligner.align("AAAA", "TTTT");
        System.out.println("No similarity test: ");
        System.out.println(result4.getFormattedAlignment());
    }
}
