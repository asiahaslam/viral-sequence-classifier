package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.classification.*;
import com.asiahaslam.viralclassifier.sequences.*;
import com.asiahaslam.viralclassifier.algorithms.*;

import java.io.IOException;
import java.util.*;

public class ViralClassifierApp {
    static void main() {
        /*// TEST 1: test FASTA file parsing
        testFastaParser();

        // TEST 2: test scoring
        testScoring();

        // TEST 3: test smith-waterman aligner
        testSWAligner();

        // TEST 3.5: test banded aligner with large sequence
        testBandedAligner();

        // TEST 4: test the class that holds the result of the viral classification
        testClassificationResult();

        // TEST 5: test the class that classifies the unknown viral sequence
        testViralClassifier();*/

        // Run test classification
        runTestClassification();

        // Run application
        // runClassification();
    }

    // main workflow for application
    static void runClassification() {
        System.out.println("\n\nRUNNING VIRAL CLASSIFICATION. . .");
        try {
            // step 1: load reference sequences
            Map<String, List<ViralSequence>> database = loadDatabase();

            // step 2: create classifier
            ViralClassifier smithWatermanClassifier = new ViralClassifier(
                    new SmithWatermanAligner(), database, 0.70, 10
            );
            ViralClassifier bandedClassifier = new ViralClassifier(
                    new BandedAligner(), database, 0.70, 10
            );
            ViralClassifier spaceOptimizedClassifier = new ViralClassifier(
                    new SpaceOptimizedAligner(), database, 0.70, 10
            );

            // step 3: load unknown sequence to classify
            ViralSequence unknownSequence = loadUnknownSequence();

            // step 4: classify sequence
            ClassificationResult smithWatermanResults = smithWatermanClassifier.classify(unknownSequence);
            ClassificationResult bandedResults = bandedClassifier.classify(unknownSequence);
            ClassificationResult spaceOptimizedResults = spaceOptimizedClassifier.classify(unknownSequence);

            // step 5: display results
            displayResults(smithWatermanResults, "Smith-Waterman");
            displayResults(bandedResults, "Banded Smith-Waterman");
            displayResults(spaceOptimizedResults, "Space-Optimized Smith-Waterman");

        } catch (IOException e) {
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
        } catch (IOException e) {
            System.out.println("Could not load influenza_nucleocapsid.fasta");
        }

        // load human papillomavirus sequences
        try {
            List<ViralSequence> papillomavirusSeqs = FastaParser.parseMultipleSequences(
                    "data/papillomavirus_L1.fasta", "Human Papillomavirus"
            );
            database.put("Human Papillomavirus", papillomavirusSeqs);
            System.out.println("Loaded " + papillomavirusSeqs.size() + " Human papillomavirus sequences");
        } catch (IOException e) {
            System.out.println("Could not load papillomavirus_L1.fasta");
        }

        // load adenovirus sequences
        try {
            List<ViralSequence> adenovirusSeqs = FastaParser.parseMultipleSequences(
                    "data/adenovirus_hexon.fasta", "Adenovirus"
            );
            database.put("Adenovirus", adenovirusSeqs);
            System.out.println("Loaded " + adenovirusSeqs.size() + " Adenovirus sequences");
        } catch (IOException e) {
            System.out.println("Could not load adenovirus_hexon.fasta");
        }

        // load polyomavirus sequences
        try {
            List<ViralSequence> polyomavirusSeqs = FastaParser.parseMultipleSequences(
                    "data/polyomavirus_VP1.fasta", "Polyomavirus"
            );
            database.put("Polyomavirus", polyomavirusSeqs);
            System.out.println("Loaded " + polyomavirusSeqs.size() + " Polyomavirus sequences");
        } catch (IOException e) {
            System.out.println("Could not load polyomavirus_VP1.fasta");
        }

        return database;
    }


    // load unknown sequence to classify
    private static ViralSequence loadUnknownSequence() throws IOException {
        try {
            return FastaParser.parseSingleSequence("data/unknown.fasta");
        } catch (IOException e) {
            System.out.println("Could not load unknown.fasta");
            throw e;
        }
    }

    // display classification results to console
    private static void displayResults(ClassificationResult result, String algorithmName) {
        System.out.println("\n=== " + algorithmName + " Classification Results ===");
        System.out.println(result.toString());
    }


    // test classification with 10000 length sequences
    static void runTestClassification() {
        System.out.println("\n\nRUNNING TEST VIRAL CLASSIFICATION. . .");
        // step 1: load reference sequences
        Map<String, List<ViralSequence>> testDatabase = loadTestDatabase();

        // step 2: create classifiers
        ViralClassifier testBandedClassifier = new ViralClassifier(
                new BandedAligner(), testDatabase, 0.70, 10
        );
        ViralClassifier testSpaceOptimizedClassifier = new ViralClassifier(
                new SpaceOptimizedAligner(), testDatabase, 0.70, 10
        );
        ViralClassifier testSmithWatermanClassifier = new ViralClassifier(
                new SmithWatermanAligner(), testDatabase, 0.70, 10
        );

        // step 3: load unknown sequence to classify
        String testString = "A".repeat(7500);
        ViralSequence testSequence = new ViralSequence("test", testString, "testing");

        // step 4: classify sequence
        ClassificationResult smithWatermanResults = testSmithWatermanClassifier.classify(testSequence);
        ClassificationResult bandedResults = testBandedClassifier.classify(testSequence);
        ClassificationResult spaceOptimizedResults = testSpaceOptimizedClassifier.classify(testSequence);

        // step 5: display results
        displayResults(smithWatermanResults, "Smith-Waterman");
        displayResults(bandedResults, "Banded Smith-Waterman");
        displayResults(spaceOptimizedResults, "Space-Optimized Smith-Waterman");
    }

    // loads test database
    private static Map<String, List<ViralSequence>> loadTestDatabase() {
        Map<String, List<ViralSequence>> database = new HashMap<>();
        int count = 7500;

        String longSeq1 = "A".repeat(count);
        String longSeq2 = "T".repeat(count);
        String longSeq3 = "C".repeat(count);
        String longSeq4 = "G".repeat(count);

        ViralSequence test1 = new ViralSequence("test", longSeq1, "testingA");
        ViralSequence test2 = new ViralSequence("test", longSeq2, "testingT");
        ViralSequence test3 = new ViralSequence("test", longSeq3, "testingC");
        ViralSequence test4 = new ViralSequence("test", longSeq4, "testingG");

        List<ViralSequence> testSeqs = new ArrayList<>();
        testSeqs.add(test1);
        testSeqs.add(test2);
        testSeqs.add(test3);
        testSeqs.add(test4);
        testSeqs.add(test1);
        testSeqs.add(test2);
        testSeqs.add(test3);
        testSeqs.add(test4);

        database.put("Test seqs 1", testSeqs);
        database.put("Test seqs 2", testSeqs);
        database.put("Test seqs 3", testSeqs);
        database.put("Test seqs 4", testSeqs);
        System.out.println("Loaded " + testSeqs.size() + " Test sequences");

        return database;
    }
}