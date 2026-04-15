package com.asiahaslam.viralclassifier.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.function.LongSupplier;

/*
    adapted from Vlad's resources about how to use com.sun.management.ThreadMXBean
    https://github.com/vbochenin/code.vbochenin.github.io/tree/main/memory-usage
 */

public class AccurateMemoryMeasurement {

    private static final LongSupplier memoryProvider = initAllocatedMemoryProvider();

    private static LongSupplier initAllocatedMemoryProvider() {
        try {
            Class<?> internalIntf = Class.forName("com.sun.management.ThreadMXBean");
            ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
            if (!internalIntf.isAssignableFrom(bean.getClass())) {
                Method m = ManagementFactory.class.getMethod("getPlatformMXBean", Class.class);
                bean = (ThreadMXBean) m.invoke(null, internalIntf);
                if (bean == null) {
                    throw new UnsupportedOperationException("No way to access private ThreadMXBean");
                }
            }
            ThreadMXBean allocMxBean = bean;
            Method allocMxBeanGetter = internalIntf.getMethod("getCurrentThreadAllocatedBytes");
            return () -> {
                try {
                    return (long)allocMxBeanGetter.invoke(allocMxBean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (Exception e) {
            System.err.println("Thread allocation tracking not supported: " + e.getMessage());
            return () -> 0; // Fallback - return 0 if not supported
        }
    }

     // Measure memory allocated by a specific algorithm
    public static long measureAllocatedMemory(Runnable algorithm) {
        // Force GC to clean up any existing allocations
        System.gc();
        Thread.yield();
        System.gc();

        // Get baseline allocation
        long beforeAllocation = memoryProvider.getAsLong();

        // Run the algorithm
        algorithm.run();

        // Measure allocation after
        long afterAllocation = memoryProvider.getAsLong();

        return afterAllocation - beforeAllocation;
    }

     // Check if memory tracking is available
    public static boolean isMemoryTrackingSupported() {
        return memoryProvider.getAsLong() > 0;
    }
}