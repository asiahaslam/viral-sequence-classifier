package com.asiahaslam.viralclassifier.utils;

import com.asiahaslam.viralclassifier.sequences.ViralSequence;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandLineParserTest {
    @Test
    void testDefault() {
        String [] def = new String [2];
        def[0] = "-s";
        def[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedArguments = CommandLineParser.parsedArguments(def);
        assertEquals("STANDARD", parsedArguments.getAlgorithm().toString());
    }

    @Test
    void testStandard() {
        String [] args = new String [4];
        args[0] = "-s";
        args[1] = "AAGTC";
        args[2] = "-v";
        args[3] = "standard";
        CommandLineParser.ParsedArguments parsedArguments = CommandLineParser.parsedArguments(args);
        assertEquals("STANDARD", parsedArguments.getAlgorithm().toString());
    }

    @Test
    void testSpaceOptimized() {
        String [] args = new String [4];
        args[0] = "-s";
        args[1] = "AAGTC";
        args[2] = "-v";
        args[3] = "space";
        CommandLineParser.ParsedArguments parsedArguments = CommandLineParser.parsedArguments(args);
        assertEquals("SPACE_OPTIMIZED", parsedArguments.getAlgorithm().toString());
    }

    @Test
    void testBanded() {
        String [] args = new String [4];
        args[0] = "-s";
        args[1] = "AAGTC";
        args[2] = "-v";
        args[3] = "banded";
        CommandLineParser.ParsedArguments parsedArguments = CommandLineParser.parsedArguments(args);
        assertEquals("BANDED", parsedArguments.getAlgorithm().toString());
    }

    @Test
    void testAllVariants() {
        String [] args = new String [4];
        args[0] = "-s";
        args[1] = "AAGTC";
        args[2] = "-v";
        args[3] = "all";
        CommandLineParser.ParsedArguments parsedArguments = CommandLineParser.parsedArguments(args);
        assertEquals("ALL", parsedArguments.getAlgorithm().toString());
    }

    @Test
    void testShowMaxScore() {
        String [] withMax = new String [3];
        withMax[0] = "-s";
        withMax[1] = "AAGTC";
        withMax[2] = "-m";
        CommandLineParser.ParsedArguments parsedMax = CommandLineParser.parsedArguments(withMax);
        assertTrue(parsedMax.shouldShowMaxScore());

        String [] noMax = new String [2];
        noMax[0] = "-s";
        noMax[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedNoMax = CommandLineParser.parsedArguments(noMax);
        assertFalse(parsedNoMax.shouldShowMaxScore());
    }

    @Test
    void testShowPerformance() {
        String [] show = new String [3];
        show[0] = "-s";
        show[1] = "AAGTC";
        show[2] = "-p";
        CommandLineParser.ParsedArguments parsedPerf = CommandLineParser.parsedArguments(show);
        assertTrue(parsedPerf.shouldShowPerformanceData());

        String [] hide = new String [2];
        hide[0] = "-s";
        hide[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedNoPerf = CommandLineParser.parsedArguments(hide);
        assertFalse(parsedNoPerf.shouldShowPerformanceData());
    }

    @Test
    void testIsFile() {
        String [] pasted = new String [2];
        pasted[0] = "-s";
        pasted[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedPasted = CommandLineParser.parsedArguments(pasted);
        assertFalse(parsedPasted.isFile());

        String [] fileInput = new String [2];
        fileInput[0] = "-f";
        fileInput[1] = "data/test.fasta";
        CommandLineParser.ParsedArguments parsedFile = CommandLineParser.parsedArguments(fileInput);
        assertTrue(parsedFile.isFile());
    }

    @Test
    void testShowAlignment() {
        String [] show = new String [3];
        show[0] = "-s";
        show[1] = "AAGTC";
        show[2] = "-a";
        CommandLineParser.ParsedArguments parsedShow = CommandLineParser.parsedArguments(show);
        assertTrue(parsedShow.shouldShowAlignedSequences());

        String [] hide = new String [2];
        hide[0] = "-s";
        hide[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedHide = CommandLineParser.parsedArguments(hide);
        assertFalse(parsedHide.shouldShowAlignedSequences());
    }

    @Test
    void testBandWidth() {
        String [] custom = new String [4];
        custom[0] = "-s";
        custom[1] = "AAGTC";
        custom[2] = "-b";
        custom[3] = "25";
        CommandLineParser.ParsedArguments parsedCustom = CommandLineParser.parsedArguments(custom);
        assertEquals(25, parsedCustom.getBandWidth());

        String [] def = new String [2];
        def[0] = "-s";
        def[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedDef = CommandLineParser.parsedArguments(def);
        assertEquals(20, parsedDef.getBandWidth());
    }

    @Test
    void testConfidenceThreshold() {
        String [] custom = new String [4];
        custom[0] = "-s";
        custom[1] = "AAGTC";
        custom[2] = "-c";
        custom[3] = "0.80";
        CommandLineParser.ParsedArguments parsedCustom = CommandLineParser.parsedArguments(custom);
        assertEquals(0.80, parsedCustom.getConfidenceThreshold());

        String [] def = new String [2];
        def[0] = "-s";
        def[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedDef = CommandLineParser.parsedArguments(def);
        assertEquals(0.70, parsedDef.getConfidenceThreshold());
    }

    @Test
    void testShowHelp() {
        String [] show = new String [3];
        show[0] = "-s";
        show[1] = "AAGTC";
        show[2] = "-h";
        CommandLineParser.ParsedArguments parsedShow = CommandLineParser.parsedArguments(show);
        assertTrue(parsedShow.shouldShowHelp());

        String [] hide = new String [2];
        hide[0] = "-s";
        hide[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedHide = CommandLineParser.parsedArguments(hide);
        assertFalse(parsedHide.shouldShowHelp());
    }

    @Test
    void testShowSecondFamily() {
        String [] show = new String [3];
        show[0] = "-s";
        show[1] = "AAGTC";
        show[2] = "-t";
        CommandLineParser.ParsedArguments parsedShow = CommandLineParser.parsedArguments(show);
        assertTrue(parsedShow.shouldShowSecondFamily());

        String [] hide = new String [2];
        hide[0] = "-s";
        hide[1] = "AAGTC";
        CommandLineParser.ParsedArguments parsedHide = CommandLineParser.parsedArguments(hide);
        assertFalse(parsedHide.shouldShowSecondFamily());
    }
}
