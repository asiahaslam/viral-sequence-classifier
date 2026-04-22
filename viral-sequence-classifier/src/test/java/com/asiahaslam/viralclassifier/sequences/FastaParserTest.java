package com.asiahaslam.viralclassifier.sequences;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

public class FastaParserTest {
    @Test
    void testParseSingleSequence() throws IOException {
        ViralSequence test = FastaParser.parseSingleSequence("data/test.fasta");
        assertEquals("Test sequence", test.getName());
        assertEquals("AATATATTCAATATGGAAAGAAT", test.getSequence());
    }

    @Test
    void testParseMultipleSequences() throws IOException {
        List<ViralSequence> sequences = FastaParser.parseMultipleSequences("data/testFamily.fasta", "TestFamily");
        assertEquals("Test sequence1", sequences.get(0).getName());
        assertEquals("Test sequence2", sequences.get(1).getName());
        assertEquals("Test sequence3", sequences.get(2).getName());
        assertEquals("AATATATTTCGTCGCCGAGAAAGAAT", sequences.get(0).getSequence());
        assertEquals("AAGAGAGATCGTCATCGTACTGAG", sequences.get(1).getSequence());
        assertEquals("ACTGTCAGTCATGCCTAGATAGAA", sequences.get(2).getSequence());
    }
}
