package com.asiahaslam.viralclassifier.utils;

import java.util.*;

public class CommandLineParser {
    // an enum to handle input related to algorithm type
    public enum AlgorithmType {
        STANDARD("standard", "Standard Smith-Waterman"),
        SPACE_OPTIMIZED("space", "Space-Optimized Smith-Waterman"),
        BANDED("banded", "Banded Smith-Waterman"),
        ALL("all", "All three algorithms");

        private final String flag;
        private final String description;

        AlgorithmType(String flag, String description) {
            this.flag = flag;
            this.description = description;
        }

        public String getFlag() { return flag; }
        public String getDescription() { return description; }

        public static AlgorithmType fromFlag(String flag) {
            for (AlgorithmType type : values()) {
                if (type.flag.equalsIgnoreCase((flag))) {
                    return type;
                }
            }
            return null;
        }
    }

    public static class ParsedArguments {
        private final boolean showHelp;
        private final String sequenceInput;
        private final boolean isFile;
        private final boolean showSecondFamily;
        private final boolean showAlignedSequences;
        private final AlgorithmType algType;
        private final boolean showPerformanceData;
        private final int bandWidth;
        private final double confidenceThreshold;
        private final boolean showMaxScore;

        public ParsedArguments() {
            this.showHelp = true;
            this.sequenceInput = null;
            this.isFile = false;
            this.showSecondFamily = false;
            this.showAlignedSequences = false;
            this.algType = AlgorithmType.STANDARD;
            this.showPerformanceData = false;
            this.bandWidth = 20;
            this.confidenceThreshold = 0.70;
            this.showMaxScore = false;
        }

        public ParsedArguments(
                boolean showHelp, String sequenceInput, boolean isFile,
                boolean showSecondFamily, boolean showAlignedSequences, AlgorithmType algType, boolean showPerformanceData, int bandWidth, double confidenceThreshold, boolean showMaxScore) {
            this.showHelp = showHelp;
            this.sequenceInput = sequenceInput;
            this.isFile = isFile;
            this.showSecondFamily = showSecondFamily;
            this.showAlignedSequences = showAlignedSequences;
            this.algType = algType;
            this.showPerformanceData = showPerformanceData;
            this.bandWidth = bandWidth;
            this.confidenceThreshold = confidenceThreshold;
            this.showMaxScore = showMaxScore;
        }

        public boolean shouldShowHelp() { return showHelp; }
        public String getSequenceInput() { return sequenceInput; }
        public boolean isFile() { return isFile; }
        public boolean shouldShowSecondFamily() { return showSecondFamily; }
        public boolean shouldShowAlignedSequences() { return showAlignedSequences; }
        public AlgorithmType getAlgorithm() { return algType; }
        public boolean shouldShowPerformanceData () { return showPerformanceData; }
        public double getConfidenceThreshold() { return confidenceThreshold; }
        public int getBandWidth() { return bandWidth; }
        public boolean shouldShowMaxScore() { return showMaxScore; }
    }

    public static void printHelp() {
        System.out.println("====Viral Sequence Classifier===");
        System.out.println("Classifies viral DNA sequences into likely viral family using Smith-Waterman alignment algorithms");
        System.out.println();

        System.out.println("HOW TO USE:");
        System.out.println("  java ViralClassifierApp [OPTIONS]");
        System.out.println();

        System.out.println("INPUT OPTIONS (choose one):");
        System.out.println("  -f --file <filename>     Read unknown sequence from FASTA file");
        System.out.println("  -s, --sequence <seq>     Provide sequence directly as string");
        System.out.println();

        System.out.println("ALGORITHM OPTIONS:");
        System.out.println(" -v --variant <type>     Algorithm to use (default: Standard Smith-Waterman");
        System.out.println("types:");
        System.out.println("   standard     Standard Smith-Waterman (full matrix)");
        System.out.println("   space     Space-Optimized Smith-Waterman");
        System.out.println("   banded     Banded Smith-Waterman");
        System.out.println("   all     Run all three algorithms");
        System.out.println();

        System.out.println("CONFIGURATION OPTIONS:");
        System.out.println("  -h, --help     Show this help message");
        System.out.println("  -b, --band-width <k>     Band width for banded algorithm (default: 20)");
        System.out.println("  -t, --two-families     Show top two viral family predictions");
        System.out.println("  -m, --max-score     Show max score info");
        System.out.println("  -a, --alignment     Show sequence alignment for top sequence(es)");
        System.out.println("  -p, --performance     Show algorithm performance (speed and memory)");
        System.out.println("  -c, --confidence     Provide custom confidence threshold (0.5 to 1.0)");
        System.out.println();

        // examples for user
        System.out.println("EXAMPLES:");
        System.out.println("Read from a FASTA file, show two predictions, algorithm performance data, and max score data");
        System.out.println("-f data/unknown.fasta -a -t -p -m");
        System.out.println("Read from a string, use the banded algorithm with band-width = 25");
        System.out.println("-s AATTGGCCAAG -v banded -b 25");
        System.out.println("Read from a FASTA file, use 0.85 confidence threshold, use default algorithm");
        System.out.println("-f data/unknown.fasta -c 0.85");
        System.out.println("Read from a string, use all algorithm variants, show algorithm performance data");
        System.out.println("-s AATTGGCCAAG -v all -p");
    }

    public static ParsedArguments parsedArguments(String[] args) {
        if (args.length == 0) {
            return new ParsedArguments();
        }

        boolean showHelp = false;
        String sequenceInput = null;
        boolean isFile = false;
        boolean showSecondFamily = false;
        boolean showAlignedSequences = false;
        // List<AlgorithmType> algorithms = new ArrayList<>();
        AlgorithmType algType = AlgorithmType.STANDARD;
        boolean showPerformanceData = false;
        int bandWidth = 20;
        double confidenceThreshold = 0.70;
        boolean showMaxScore = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg.toLowerCase()) {
                case "-h":
                case"--help":
                    showHelp = true;
                    break;
                case "-f":
                case "--file":
                    if (i + 1 >= args.length) {
                        System.err.println("Error: -f/--file requires a filename");
                        showHelp = true;
                        break;
                    }
                    sequenceInput = args[++i];
                    isFile = true;
                    break;
                case "-s":
                case "--sequence":
                    if (i + 1 >= args.length) {
                        System.err.println("Error: -s/--sequence requires a sequence string");
                        showHelp = true;
                        break;
                    }
                    sequenceInput = args[++i];
                    isFile = false;
                    break;
                case "-v":
                case "--variant":
                    if (i + 1 >= args.length) {
                        System.err.println("Error: -v/--variant requires an algorithm type");
                        showHelp = true;
                        break;
                    }
                    String algFlag = args[++i];
                    algType = AlgorithmType.fromFlag(algFlag);
                    if (algType == null) {
                        System.err.println("Error: Unknown algorithm " + algFlag);
                        System.err.println("Valid options: standard, space, banded, all");
                        showHelp = true;
                        break;
                    }
                    break;
                case "-b":
                case "--band-width":
                    if (i + 1 >= args.length) {
                        System.err.println("Error: -b/--band-width requires a number");
                        showHelp = true;
                        break;
                    }
                    try {
                        bandWidth = Integer.parseInt(args[++i]);
                        if (bandWidth < 1) {
                            System.err.println("Error: Band width must be positive");
                            showHelp = true;
                        }
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Error: Invalid band width " + args[++i]);
                        showHelp = true;
                    }
                    break;
                case "-t":
                case "--two-families":
                    showSecondFamily = true;
                    break;
                case "-a":
                case "--alignment":
                    showAlignedSequences = true;
                    break;
                case "-p":
                case "--performance":
                    showPerformanceData = true;
                    break;
                case "-m":
                case "--max-score":
                    showMaxScore = true;
                    break;
                case "-c":
                case "--confidence":
                    if (i + 1 >= args.length) {
                        System.err.println("Error: -c/--confidence requires a number");
                        showHelp = true;
                        break;

                    }
                    try {
                        confidenceThreshold = Double.parseDouble(args[++i]);
                        if (confidenceThreshold < 0.5 || confidenceThreshold > 1.0) {
                            System.err.println("Error: Confidence threshold must be between 0.5 and 1.0 inclusive");
                            showHelp = true;
                        }
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Error: Invalid confidence threshold " + args[++i]);
                        showHelp = true;
                    }
                    break;
                default:
                    System.err.println("Error: Unknown option " + arg);
                    showHelp = true;
                    break;

            }
        }

        // make sure there is a sequence to classify
        if (!showHelp && sequenceInput == null) {
            System.err.println("Error: no input sequence specified. Use -f or -s");
            showHelp = true;
        }

        return new ParsedArguments(
                showHelp, sequenceInput, isFile,
                showSecondFamily, showAlignedSequences, algType, showPerformanceData, bandWidth, confidenceThreshold, showMaxScore);
    }
}
