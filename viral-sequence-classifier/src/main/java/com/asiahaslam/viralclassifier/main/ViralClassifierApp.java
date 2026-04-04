package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.classification.*;
import com.asiahaslam.viralclassifier.sequences.*;
import com.asiahaslam.viralclassifier.algorithms.*;

import java.io.IOException;
import java.util.*;

public class ViralClassifierApp {
    static void main() {
        // TEST 1: test FASTA file parsing
        testFastaParser();

        // TEST 2: test scoring
        testScoring();

        // TEST 3: test smith-waterman aligner
        testSWAligner();

        // TEST 4: test the class that holds the result of the viral classification
        testClassificationResult();

        // TEST 5: test the class that classifies the unknown viral sequence
        testViralClassifier();

        // Run application
        runClassification();
    }

    // main workflow for application
    static void runClassification() {
        System.out.println("\n\nRUNNING VIRAL CLASSIFICATION. . .");
        try {
            // step 1: load reference sequences
            Map<String, List<ViralSequence>> database = loadDatabase();

            // step 2: create classifier
            ViralClassifier bandedClassifier = new ViralClassifier(
                    new BandedAligner(), database, 0.70, 10
            );
            ViralClassifier smithWatermanClassifier = new ViralClassifier(
                    new SpaceOptimizedAligner(), database, 0.70, 10
            );
            ViralClassifier spaceOptimizedClassifier = new ViralClassifier(
                    new SmithWatermanAligner(), database, 0.70, 10
            );

            // step 3: load unknown sequence to classify
            ViralSequence unknownSequence = loadUnknownSequence();

            // step 4: classify sequence
            ClassificationResult bandedResults = bandedClassifier.classify(unknownSequence);
            ClassificationResult smithWatermanResults = smithWatermanClassifier.classify(unknownSequence);
            ClassificationResult spaceOptimizedResults = spaceOptimizedClassifier.classify(unknownSequence);

            // step 5: display results
            displayResults(bandedResults, "Banded Smith-Waterman");
            displayResults(smithWatermanResults, "Smith-Waterman");
            displayResults(spaceOptimizedResults, "Space-Optimized Smith-Waterman");

        }
        catch (IOException e) {
            System.err.println("Error in classification pipeline: " + e.getMessage());
            System.out.println("Make sure you have FASTA files in the 'data/' directory");
        }
    }

    // loads database from files specified directly
    private static Map<String, List<ViralSequence>> loadDatabase() throws IOException {
        Map<String, List<ViralSequence>> database = new HashMap<>();

        // load influenza A sequences
        try {
            List<ViralSequence> influenzaSeqs = FastaParser.parseMultipleSequences(
                    "data/influenza_nucleocapsid.fasta", "Human Influenza A"
            );
            database.put("Influenza A", influenzaSeqs);
            System.out.println("Loaded " + influenzaSeqs.size() + " Human Influenza A sequences");
        }
        catch (IOException e) {
            System.out.println("Could not load influenza_nucleocapsid.fasta");
        }

        // load herpesvirus 1 sequences
        /*try {
            List<ViralSequence> herpesSeqs = FastaParser.parseMultipleSequences(
                    "data/herpes1.fasta", "Herpesvirus 1"
            );
            database.put("Herpesvirus 1", herpesSeqs);
            System.out.println("Loaded " + herpesSeqs.size() + " Herpesvirus 1 sequences");
        }
        catch (IOException e) {
            System.out.println("Could not load herpes1.fasta");
        }*/

        // load human papillomavirus sequences
        try {
            List<ViralSequence> papillomavirusSeqs = FastaParser.parseMultipleSequences(
                    "data/papillomavirus_L1.fasta", "Human Papillomavirus"
            );
            database.put("Human Papillomavirus", papillomavirusSeqs);
            System.out.println("Loaded " + papillomavirusSeqs.size() + " Human papillomavirus sequences");
        }
        catch (IOException e) {
            System.out.println("Could not load papillomavirus_L1.fasta");
        }

        // load adenovirus sequences
        try {
            List<ViralSequence> adenovirusSeqs = FastaParser.parseMultipleSequences(
                    "data/adenovirus_hexon.fasta", "Adenovirus"
            );
            database.put("Adenovirus", adenovirusSeqs);
            System.out.println("Loaded " + adenovirusSeqs.size() + " Adenovirus sequences");
        }
        catch (IOException e) {
            System.out.println("Could not load adenovirus_hexon.fasta");
        }

        // load polyomavirus sequences
        try {
            List<ViralSequence> polyomavirusSeqs = FastaParser.parseMultipleSequences(
                    "data/polyomavirus_VP1.fasta", "Polyomavirus"
            );
            database.put("Polyomavirus", polyomavirusSeqs);
            System.out.println("Loaded " + polyomavirusSeqs.size() + " Polyomavirus sequences");
        }
        catch (IOException e) {
            System.out.println("Could not load polyomavirus_VP1.fasta");
        }

        // TODO: get sequences for more viral families and then add code here to load them

        return database;
    }

    // load unknown sequence to classify
    private static ViralSequence loadUnknownSequence() throws IOException {
        try {
            return FastaParser.parseSingleSequence("data/unknown.fasta");
        }
        catch (IOException e) {
            System.out.println("Could not load unknown.fasta");
            throw e;
        }
    }

    // display classification results to console
    private static void displayResults(ClassificationResult result, String algorithmName) {
        System.out.println("\n=== " + algorithmName + " Classification Results ===");
        System.out.println(result.toString());
    }


    // TEST 1: FASTA parser
    static void testFastaParser() {
        System.out.println("Testing FASTA parser:\n");
        try {
            // parse a FASTA file
            ViralSequence sequence = FastaParser.parseSingleSequence("data/test.fasta");

            System.out.println("Sequence: " + sequence.getSequence());
        }
        catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    // TEST 2: Scoring
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

    // TEST 3: Smith-Waterman aligner
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

    // TEST 4: Classification result
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

    // TEST 5: Viral Classifier
    static void testViralClassifier() {
        System.out.println("Testing viral sequence classifier:\n");

        // create a mock database of reference sequences
        ViralClassifier classifier = getViralClassifier();

        // test classification
        ViralSequence testSeq1 = new ViralSequence("test1", "ATCGATCGATCGATTT", "Unknown");
        ViralSequence testSeq2 = new ViralSequence("test2", "GGCCGGCCGGCCGGCCGGCC", "Unknown");
        ViralSequence testSeq3 = new ViralSequence("test3", "TTTTTTTTTTTTTTTTTTTTT", "Unknown");

        System.out.println("Test 1 (influenza-like): " + classifier.classify(testSeq1));
        System.out.println("Test 2 (corona-like): " + classifier.classify(testSeq2));
        System.out.println("Test 3 (unknown): " + classifier.classify(testSeq3));
    }

    // create the mock reference sequence database
    private static ViralClassifier getViralClassifier() {
        Map<String, List<ViralSequence>> database = new HashMap<>();

        // add some sample reference sequences to the database
        List<ViralSequence> influenzaRefs = Arrays.asList(
                new ViralSequence("flu_1", "ATCGATCGATCGATCGATCG", "Influenza"),
                new ViralSequence("flu_2", "ATCGATCGATCGATCGATCC", "Influenza")
        );

        List<ViralSequence> coronaRefs = Arrays.asList(
                new ViralSequence("corona_1", "GGCCGGCCGGCCGGCCGGCC", "Coronavirus"),
                new ViralSequence("corona_2", "GGCCGGCCGGCCGGCCGGCA", "Coronavirus")
        );

        database.put("Influenza", influenzaRefs);
        database.put("Coronavirus", coronaRefs);

        // create classifier instance
        return new ViralClassifier(database);
    }
}
