package com.asiahaslam.viralclassifier.sequences;

import java.io.*;
import java.util.Scanner;

// initial basic logic from https://rosettacode.org/wiki/FASTA_format
// TODO: add logic for dealing with different kinds of formatting in files
// TODO: instead of printing to console, create string objects from parsed FASTA files

public class FastaParser {
    public static void main(String[] args) throws FileNotFoundException {
        boolean first = true;

        try (Scanner sc = new Scanner(new File("src/main/java/com/asiahaslam/viralclassifier/sequences/test.fasta"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.charAt(0) == '>') {
                    if (first) {
                        first = false;
                    }
                    else {
                        System.out.println();
                    }
                }
                else {
                    System.out.print(line);
                }
            }
        }
        System.out.println();
    }
}
