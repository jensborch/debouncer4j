package com.github.jensborch.debouncer4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
        debouncer = new Debouncer<>("Test", t -> t + " result", 100L, scheduler);
    }

    private void mockScheduler() {
        when(scheduler.schedule(any(Callable.class), leq(100L), eq(TimeUnit.MILLISECONDS))).thenAnswer(
                invocation -> {
                    ScheduledFuture future = mock(ScheduledFuture.class);
                    lenient().when(future.get()).thenReturn(invocation.getArgument(0, Callable.class).call());
                    return future;
                });
    }

    @Test
    void testCallImmediately() throws Exception {
        Future<String> result = debouncer.run("test");
        verify(scheduler, times(1)).schedule(any(Callable.class), eq(100L), eq(TimeUnit.MILLISECONDS));
        assertEquals("test result", result.get());
    }

    @Test
    void testCallImmediatelyTwice() throws Exception {
        mockScheduler();
        debouncer.run("test");
        Future<String> result = debouncer.run("test");
        verify(scheduler, times(2)).schedule(any(Callable.class), eq(100L), eq(TimeUnit.MILLISECONDS));
        assertEquals("test result", result.get());
    }

    @Test
    void testRunDebouncedOnce() throws Exception {
        debouncer.run("test");
        mockScheduler();
        debouncer.run("test");
        debouncer.run("test");
        Future<String> result = debouncer.run("test");
        verify(scheduler, times(2)).schedule(any(Callable.class), leq(100L), eq(TimeUnit.MILLISECONDS));
        assertEquals("test result", result.get());
    }

}
