package com.asiahaslam.viralclassifier.sequences;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// initial basic logic from https://rosettacode.org/wiki/FASTA_format

public class FastaParser {
    public static List<ViralSequence> parseMultipleSequences(String filePath, String virusFamily)
        throws IOException {
        List<ViralSequence> sequences = new ArrayList<>();
    }
    public static ViralSequence parseSingleSequence(String filePath) throws IOException {
        // create empty instance of ViralSequence object
        ViralSequence sequence = new ViralSequence();
        // using BufferedReader instead of Scanner for handling larger/more complex files
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // holds contents of current line
            String line;
            // use StringBuilder for holding the sequence
            StringBuilder currentSequence = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // trim whitespace
                line = line.trim();

                // if this line is a comment, set the virus name to contents of the comment
                if (line.charAt(0) == '>') {
                    sequence.setVirusName(line.substring(1));
                }
                // if line is not a comment and not blank, add contents to the current sequence
                else if (!line.isBlank()) {
                    currentSequence.append(line);
                }
            }
            sequence.setSequence(currentSequence.toString());
        }
        return sequence;
    }
}
