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
        private final boolean showMaxScoreInfo;
        private final boolean showAlignedSequences;
        private final List<AlgorithmType> algorithms;
        private final boolean runPerformanceTest;
        private final int bandWidth;

        public ParsedArguments(
                boolean showHelp, String sequenceInput, boolean isFile,
                boolean showSecondFamily, boolean showMaxScoreInfo, boolean showAlignedSequences,
                List<AlgorithmType> algorithms, boolean runPerformanceTest, int bandWidth) {
            this.showHelp = showHelp;
            this.sequenceInput = sequenceInput;
            this.isFile = isFile;
            this.showSecondFamily = showSecondFamily;
            this.showMaxScoreInfo = showMaxScoreInfo;
            this.showAlignedSequences = showAlignedSequences;
            this.algorithms = algorithms;
            this.runPerformanceTest = runPerformanceTest;
            this.bandWidth = bandWidth;
        }

        public boolean shouldShowHelp() { return showHelp; }
        public String getSequenceInput() { return sequenceInput; }
        public boolean isFile() { return isFile; }
        public boolean shouldShowSecondFamily() { return showSecondFamily; }
        public boolean shouldShowScoreInfo() { return showMaxScoreInfo; }
        public boolean shouldShowAlignedSequences() { return showAlignedSequences; }
        public List<AlgorithmType> getAlgorithms() { return algorithms; }
        public boolean shouldRunPerformanceTest() { return runPerformanceTest; }
        public int getBandWidth() { return bandWidth; }
    }
}
