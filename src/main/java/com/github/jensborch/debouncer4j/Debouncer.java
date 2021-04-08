package com.github.jensborch.debouncer4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<T, Optional<R>> queue;

    public Debouncer(final String name, final Function<T, R> operation, final long delay, final ScheduledExecutorService scheduler) {
        this.name = Objects.requireNonNull(name);
        this.function = Objects.requireNonNull(operation);
        this.scheduler = Objects.requireNonNull(scheduler);
        this.delay = Objects.requireNonNull(delay);
        this.queue = new ConcurrentHashMap<>();
    }

    public R run(final T t) {
        Optional<R> result = queue.get(t);
        if (result == null) {
            LOG.debug("Calling {}({}) immediately", name, t);
            result = Optional.ofNullable(function.apply(t));
            queue(t, result);
            remove(t, delay);
        } else {
            LOG.debug("Returning queued result for {}({})", name, t);
        }
        return result.orElse(null);
    }

    private void remove(final T t, final long delay) {
        scheduler.schedule(() -> queue.remove(t), delay, TimeUnit.MILLISECONDS);
    }

    private void queue(final T t, final Optional<R> r) {
        queue.put(t, r);
    }

}
