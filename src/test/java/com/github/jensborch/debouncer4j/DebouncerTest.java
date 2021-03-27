package com.github.jensborch.debouncer4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link Debouncer}.
 */
@ExtendWith(MockitoExtension.class)
class DebouncerTest {

    @Mock
    private ScheduledExecutorService scheduler;

    private Debouncer<String, String> debouncer;

    @BeforeEach
    void setUp() {
        debouncer = new Debouncer("Test", t -> t + " result", 1000, scheduler);
    }

    @Test
    void testRun() throws Exception {
        String result = debouncer.run("test");
        verify(scheduler, times(1)).schedule(any(Runnable.class), eq(1000L), eq(TimeUnit.MILLISECONDS));
        assertEquals("test result", result);
    }

    @Test
    void testRunDebouncedOnce() throws Exception {
        debouncer.run("test");
        debouncer.run("test");
        debouncer.run("test");
        String result = debouncer.run("test");
        verify(scheduler, times(1)).schedule(any(Runnable.class), eq(1000L), eq(TimeUnit.MILLISECONDS));
        assertEquals("test result", result);
    }

    @Test
    void testRunDebounced() throws Exception {
        when(scheduler.schedule(any(Runnable.class), eq(1000L), eq(TimeUnit.MILLISECONDS))).thenAnswer(
                invocation -> {
                    invocation.getArgument(0, Runnable.class).run();
                    return null;
                });
        debouncer.run("test");
        String result = debouncer.run("test");
        verify(scheduler, times(2)).schedule(any(Runnable.class), eq(1000L), eq(TimeUnit.MILLISECONDS));
    }

}
