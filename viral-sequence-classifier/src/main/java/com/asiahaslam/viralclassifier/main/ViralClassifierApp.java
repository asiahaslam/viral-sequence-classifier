package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.algorithms.AlignmentResult;
import com.asiahaslam.viralclassifier.algorithms.SmithWatermanAligner;
import com.asiahaslam.viralclassifier.classification.ClassificationResult;
import com.asiahaslam.viralclassifier.sequences.ViralSequence;
import com.asiahaslam.viralclassifier.sequences.FastaParser;
import com.asiahaslam.viralclassifier.algorithms.ScoringMatrix;

import java.util.HashMap;
import java.util.Map;

public class ViralClassifierApp {
    static void testFastaParser() {
        System.out.println("Testing FASTA parser:\n");
        try {
            // parse a FASTA file
            ViralSequence sequence = FastaParser.parseFile("data/influenza_b.fasta");

            System.out.println("Sequence: " + sequence.getSequence());
        }
        catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    static void testScoring() {
        // scoring matrix
        ScoringMatrix matrix = new ScoringMatrix();

        System.out.println("Testing scoring matrix:\n");
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
    }

    static void testSWAligner() {
        System.out.println("Testing Smith-Waterman Aligner:\n");
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

    static void testClassificationResult() {
        System.out.println("Testing classification result class:\n");
        // create sample family scores
        Map<String, Double> familyScores = new HashMap<>();
        familyScores.put("Influenza", 0.85);
        familyScores.put("Coronavirus", 0.42);
        familyScores.put("Herpesvirus", 0.28);

        // create classification result
        ClassificationResult result = new ClassificationResult(
                "test_sequence_001",
                "Influenza",
                0.85,
                true,
                familyScores,
                "Smith-Waterman",
                1250
        );

        // create unknown classification
        ClassificationResult unknown = ClassificationResult.createUnknown(
                "unknown_seq1", familyScores, "Smith-Waterman", 800
        );

        System.out.println("Basic result: \n" + result);
        System.out.println("Unknown result: \n" + unknown);
    }

    static void main(String[] args) {
        // TEST 1: test FASTA parsing
        testFastaParser();

        // TEST 2: test scoring
        testScoring();

        // TEST 3: test smith-waterman aligner
        testSWAligner();

        // TEST 4: test alignment result class
        testClassificationResult();
    }
}
