package com.example.spring_boot_example_for_isolated_and_parallel_tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractTest {

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static volatile boolean beforeTestRunExecuted = false;

    @BeforeAll
    private static void beforeAll() {
        if (!beforeTestRunExecuted) {
            LOCK.lock();
            try {
                beforeTestRun();
                beforeTestRunExecuted = true;
            } finally {
                LOCK.unlock();
            }
        }
        beforeTestClass();
    }

    // Stop the container
    @AfterAll
    private static void afterAll() {

    }

    // Test initialization that is run once for the whole test run.
    // Optional, but used for example for building container images.
    private static void beforeTestRun() {
        System.out.println("This ran only once for the whole test run.");
    }

    // Test initialization that is run for each test class.
    // Used for starting containers.
    private static void beforeTestClass() {
        System.out.println("This ran once for each test class.");
    }

}
