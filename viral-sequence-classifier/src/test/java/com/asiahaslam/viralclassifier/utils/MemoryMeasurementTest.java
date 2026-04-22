package com.asiahaslam.viralclassifier.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemoryMeasurementTest {
    @Test
    void testArrayAllocation() {
        long testMemory = MemoryMeasurement.measureAllocatedMemory(() -> {
            // something we know the size of
            double[] testArray = new double[1000]; // Should be 8000 bytes
            for (int i = 0; i < testArray.length; i++) {
                testArray[i] = i; // Force actual allocation
            }
        });
        assertEquals(8000, testMemory, 100);
    }
}
