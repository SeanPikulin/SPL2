package bgu.spl.mics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    Future<Integer> future;
    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testIsDone(){
        assertFalse(future.isDone());
        future.resolve(5);
        assertTrue(future.isDone());
    }

    @Test
    public void testGet(){
        future.resolve(5);
        assertEquals(5, future.get());
        assertNotEquals(4, future.get());
    }

    @Test
    public void testGetWithParameters(){
        future.resolve(5);
        assertEquals(5, future.get(1, TimeUnit.SECONDS));
        assertNotEquals(4, future.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void testGetWithParametersNull(){
        assertNull(future.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void testResolve(){
        assertFalse(future.isDone());
        future.resolve(5);
        assertTrue(future.isDone());
    }
}
