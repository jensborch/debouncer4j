package com.github.jensborch.debouncer4j;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;

import javax.interceptor.InvocationContext;

import com.github.jensborch.debouncer4j.Debounce.DebounceInteceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link Debounce}.
 */
@ExtendWith(MockitoExtension.class)
class DebounceTest {

    @Mock
    private InvocationContext context;

    @Mock
    private ScheduledExecutorService scheduler;

    @InjectMocks
    private DebounceInteceptor inteceptor;

    @BeforeEach
    void setUp() throws Exception {
        when(context.getMethod()).thenReturn(this.getClass().getMethod("test", String.class));
    }

    @Test
    void testSomeMethod() throws Exception {
        inteceptor.bebounce(context);
        verify(context, times(4)).getMethod();
    }

    @Debounce(delay = 100)
    public void test(String test) {
        //Do nothing...
    }

}
