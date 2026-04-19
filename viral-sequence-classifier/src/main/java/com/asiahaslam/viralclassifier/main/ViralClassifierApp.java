package com.asiahaslam.viralclassifier.main;

import com.asiahaslam.viralclassifier.classification.*;
import com.asiahaslam.viralclassifier.sequences.*;
import com.asiahaslam.viralclassifier.algorithms.*;
import com.asiahaslam.viralclassifier.utils.CommandLineParser;

import java.io.IOException;
import java.util.*;

public class ViralClassifierApp {
    static void main(String[] args) {
        CommandLineParser.ParsedArguments parsedArgs = CommandLineParser.parsedArguments(args);
        System.out.println("\nArguments received: " + Arrays.toString(args) + "\n");

        if (parsedArgs.shouldShowHelp()) {
            CommandLineParser.printHelp();
        }
        else {
            runClassificationWithArgs(parsedArgs);
        }

        // Run test classification (good for performance analysis)
        // runTestClassification();

        // Run application by automatically pulling from file called unknown.fasta and using all 3 algorithms
        // runClassification();
    }

    // run classification based on command line arguments
    private static void runClassificationWithArgs(CommandLineParser.ParsedArguments args) {
        try {
            // step 1: load reference sequences
            System.out.println("Loading reference sequences. . .");
            Map<String, List<ViralSequence>> database = loadDatabase();

            // step 2: load unknown sequence to classify
            ViralSequence unknownSequence = loadUnknownSequenceFromArgs(args);

            // step 3: run classification with specified algorithms
            if (args.getAlgorithm().getFlag().equals("all")) {
                runAllAlgorithmsClassification(database, unknownSequence, args);
            }
            else {
                runSingleAlgorithmClassification(database, unknownSequence, args);
            }
        }
        catch (Exception e) {
            System.err.println("Error in classification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ViralSequence loadUnknownSequenceFromArgs(CommandLineParser.ParsedArguments args) throws IOException {
        if (args.isFile()) {
            // load from file
            try {
                return FastaParser.parseSingleSequence(args.getSequenceInput());
            } catch (IOException e) {
                System.out.println("Could not load FASTA file");
                throw e;
            }
        }
        else {
            return new ViralSequence("command_input", args.getSequenceInput(), "Unknown");
        }
    }

    private static void runSingleAlgorithmClassification(
            Map<String, List<ViralSequence>> database, ViralSequence unknownSequence, CommandLineParser.ParsedArguments args) {
        CommandLineParser.AlgorithmType algType = args.getAlgorithm();

        // create appropriate aligner
        SequenceAligner aligner = switch (algType) {
            case STANDARD -> new SmithWatermanAligner();
            case SPACE_OPTIMIZED -> new SpaceOptimizedAligner();
            case BANDED -> new BandedAligner(args.getBandWidth());
            default -> throw new IllegalArgumentException("Unknown algorithm type: " + algType);
        };

        // create classifier and run the classification
        ViralClassifier classifier = new ViralClassifier(aligner, database, args.getConfidenceThreshold());
        ClassificationResult result = classifier.classify(unknownSequence);

        // display results
        System.out.println("\n\n=== Classification Results ===");
        System.out.println("Algorithm used: " + result.getAlgorithmUsed());
        System.out.println("Predicted family: " + result.getPredictedFamily());
        System.out.println("Confidence: " + String.format("%.3f", result.getConfidence()));
        System.out.println("This prediction is " + (result.isPredictionConfident() ? "" : " not") + "confident");
        if (args.shouldShowMaxScore()) {
            System.out.println(classifier.getTopAlignment().getFormattedAlignment());
        }
        if (args.shouldShowSecondFamily()) {
            System.out.println("\n--Second-best viral family--");
            System.out.println("Predicted family: " + result.getSecondBestFamily().getSecondBest());
            System.out.println("Confidence: " + String.format("%.3f", result.getSecondBestFamily().getSecondBestScore()));
        }
        if (args.shouldShowAlignedSequences()) {
            if (aligner.getAlgorithmName().equals("Standard Smith-Waterman")) {
                System.out.println("\n--Sequence alignment information--");
                System.out.println("Optimal alignment for the best match:");
                System.out.println(classifier.getTopAlignment().getAlignedSequence1());
                System.out.println(classifier.getTopAlignment().getAlignedSequence2());
                System.out.println("Sequence 1: start: " + classifier.getTopAlignment().getStartPos1() + " end: " + classifier.getTopAlignment().getEndPos1());
                System.out.println("Sequence 2: start: " + classifier.getTopAlignment().getStartPos2() + " end: " + classifier.getTopAlignment().getEndPos1());
            }
            else {
                System.out.println("Cannot recreate optimal alignment for " + aligner.getAlgorithmName());
            }
        }
        if (args.shouldShowPerformanceData()) {
            System.out.println("\n--Algorithm performance information--");
            System.out.println("Algorithm complexity: " + aligner.getTimeComplexity() + " time, " + aligner.getSpaceComplexity() + " space");
            System.out.println("Processing time: " + result.getProcessingTimeMs() + "ms");
            System.out.println("Memory used: " + String.format("%.2f KB", result.getMemoryUsedKB()));
        }
    }

    private static void runAllAlgorithmsClassification(
            Map<String, List<ViralSequence>> database, ViralSequence unknownSequence, CommandLineParser.ParsedArguments args
    ) {
        // create classifiers
        ViralClassifier smithWatermanClassifier = new ViralClassifier(
                new SmithWatermanAligner(), database, args.getConfidenceThreshold());
        ViralClassifier bandedClassifier = new ViralClassifier(
                new BandedAligner(), database, args.getConfidenceThreshold());
        ViralClassifier spaceOptimizedClassifier = new ViralClassifier(
                new SpaceOptimizedAligner(), database, args.getConfidenceThreshold());

        // create list of classifiers
        List<ViralClassifier> classifiers = new ArrayList<>();
        classifiers.add(smithWatermanClassifier);
        classifiers.add(bandedClassifier);
        classifiers.add(spaceOptimizedClassifier);

        // run classification
        ClassificationResult smithWatermanResults = smithWatermanClassifier.classify(unknownSequence);
        ClassificationResult bandedResults = bandedClassifier.classify(unknownSequence);
        ClassificationResult spaceOptimizedResults = spaceOptimizedClassifier.classify(unknownSequence);

        // create list of classification results
        List<ClassificationResult> results = new ArrayList<>();
        results.add(smithWatermanResults);
        results.add(bandedResults);
        results.add(spaceOptimizedResults);

        // display results
        System.out.println("\n\n=== Classification Results ===");
        for (ClassificationResult result : results) {
            System.out.printf("%s: predicted family='%s', confidence = %.3f, confident=%s\n",
                    result.getAlgorithmUsed(), result.getPredictedFamily(), result.getConfidence(), result.isPredictionConfident());
        }

        if (args.shouldShowMaxScore()) {
            System.out.println("\n--Max scores--");
            for (ViralClassifier classifier : classifiers) {
                System.out.println(classifier.getAligner().getAlgorithmName() + ": " + classifier.getTopAlignment().getFormattedAlignment());
            }
        }
        if (args.shouldShowSecondFamily()) {
            System.out.println("\n--Second-best viral family--");
            for (ClassificationResult result : results) {
                System.out.printf("%s: predicted family='%s', confidence = %.3f\n",
                        result.getAlgorithmUsed(), result.getSecondBestFamily().getSecondBest(), result.getSecondBestFamily().getSecondBestScore());
            }
        }
        if (args.shouldShowPerformanceData()) {
            System.out.println("\n--Algorithm performance information--");
            for (ClassificationResult result : results) {
                System.out.printf("%s: time=%dms, memory=%.2fMB}\n",
                        result.getAlgorithmUsed(), result.getProcessingTimeMs(), result.getMemoryUsedKB());
            }
        }
        if (args.shouldShowAlignedSequences()) {
            System.out.println("\n--Standard Smith-Waterman sequence alignment information--");
            System.out.println("Optimal alignment for the best match:");
            System.out.println(smithWatermanClassifier.getTopAlignment().getAlignedSequence1());
            System.out.println(smithWatermanClassifier.getTopAlignment().getAlignedSequence2());
            System.out.println("Sequence 1: start: " + smithWatermanClassifier.getTopAlignment().getStartPos1() + " end: " + smithWatermanClassifier.getTopAlignment().getEndPos1());
            System.out.println("Sequence 2: start: " + smithWatermanClassifier.getTopAlignment().getStartPos2() + " end: " + smithWatermanClassifier.getTopAlignment().getEndPos1());
        }
    }

    // main workflow for application
    static void runClassification() {
        System.out.println("\n\nRUNNING VIRAL CLASSIFICATION. . .");
        try {
            // step 1: load reference sequences
            Map<String, List<ViralSequence>> database = loadDatabase();

            // step 2: create classifier
            ViralClassifier smithWatermanClassifier = new ViralClassifier(
                    new SmithWatermanAligner(), database, 0.70);
            ViralClassifier bandedClassifier = new ViralClassifier(
                    new BandedAligner(), database, 0.70);
            ViralClassifier spaceOptimizedClassifier = new ViralClassifier(
                    new SpaceOptimizedAligner(), database, 0.70);

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
                new BandedAligner(), testDatabase, 0.70);
        ViralClassifier testSpaceOptimizedClassifier = new ViralClassifier(
                new SpaceOptimizedAligner(), testDatabase, 0.70);
        ViralClassifier testSmithWatermanClassifier = new ViralClassifier(
                new SmithWatermanAligner(), testDatabase, 0.70);

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