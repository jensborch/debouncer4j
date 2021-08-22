package com.github.jensborch.debouncer4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Java Debouncer.
 *
 * @param <T> input type
 * @param <R> return type
 */
public class Debouncer<T, R> {

    private static final Logger LOG = LoggerFactory.getLogger(Debouncer.class);

    private final String name;
    private final ScheduledExecutorService scheduler;
    private final long delay;
    private final Function<T, R> function;
    private final Map<T, Queued<R>> queue;

    public Debouncer(final String name, final Function<T, R> operation, final long delay,
            final ScheduledExecutorService scheduler) {
        this.name = Objects.requireNonNull(name);
        this.function = Objects.requireNonNull(operation);
        this.scheduler = Objects.requireNonNull(scheduler);
        this.delay = Objects.requireNonNull(delay);
        this.queue = new HashMap<>();
    }

    public Future<R> run(final T t) {
        synchronized (queue) {
            Queued<R> queued = queue.get(t);
            Future<R> future = null;
            if (queued == null) {
                LOG.debug("Calling {}({}) immediately", name, t);
                future = CompletableFuture.completedFuture(function.apply(t));
                queue(t);
                remove(t, delay);
            } else {
                if (queued.future == null) {
                    long elapsed = queued.elapsed();
                    if (elapsed < delay) {
                        future = schedule(function, t, delay - elapsed);
                    } else {
                        future = CompletableFuture.completedFuture(function.apply(t));
                    }
                    queued.future = future;
                }
                LOG.debug("Returning queued future for {}({})", name, t);
                future = queued.future;
            }
            return future;
        }
    }

    private void remove(final T t, final long delay) {
        scheduler.schedule(() -> queue.remove(t), delay, TimeUnit.MILLISECONDS);
    }

    private Future<R> schedule(final Function<T, R> function, final T t, final long delay) {
        return scheduler.schedule(() -> {
            return function.apply(t);
        }, delay, TimeUnit.MILLISECONDS);

    }

    private void queue(final T t) {
        queue.put(t, new Queued<>());
    }

    /**
     * Queued function as a future.
     *
     * @param <R> result return type.
     */
    private static class Queued<R> {

        final long lastRunTime;
        Future<R> future;

        Queued() {
            this.lastRunTime = System.currentTimeMillis();
        }

        long elapsed() {
            return System.currentTimeMillis() - lastRunTime;
        }

    }

}
