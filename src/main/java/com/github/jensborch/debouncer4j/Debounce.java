package com.github.jensborch.debouncer4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Priority;
import javax.enterprise.util.Nonbinding;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;

/**
 * CDI interceptor for bebouncing method calls.
 */
@Priority(Interceptor.Priority.APPLICATION)
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Debounce {

    @Nonbinding
    int delay() default 10_000;

    @Nonbinding
    String name() default "";

    /**
     *
     */
    @Interceptor
    @Debounce
    class DebounceInteceptor {

        @Inject
        private ScheduledExecutorService scheduler;

        private final Map<Method, Debouncer<Parameters, Object>> methods = new HashMap<>();

        @AroundInvoke
        public Object bebounce(final InvocationContext context) throws Exception {
            Debouncer debouncer = methods.computeIfAbsent(
                    context.getMethod(),
                    method -> debouncer(context));
            return debouncer.run(context);
        }

        private Debouncer<Parameters, Object> debouncer(final InvocationContext context) {
            return new Debouncer(context.getMethod().getName(),
                    parameters -> proceed(context),
                    context.getMethod().getAnnotation(Debounce.class).delay(),
                    scheduler);
        }

        private Object proceed(final InvocationContext context) {
            try {
                return context.proceed();
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         *
         */
        private static class Parameters {

            final Object[] values;

            @SuppressWarnings("PMD.ArrayIsStoredDirectly")
            Parameters(final Object... values) {
                this.values = values;
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(values);
            }

            @Override
            public boolean equals(final Object other) {
                return this == other
                        || other != null
                        && getClass() == other.getClass()
                        && Arrays.equals(this.values, ((Parameters) other).values);
            }
        }
    }
}
