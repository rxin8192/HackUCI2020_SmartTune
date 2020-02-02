package com.example.myapplication;

import junit.framework.TestCase;

import org.junit.Test;

public class QueueTest extends TestCase {

    @Test
    public void testConstructQueue() {
        ArrayQueue queue = new ArrayQueue(10);

        assertEquals(queue.size(), 0);
    }

    @Test
    public void testEnqueue() {
        ArrayQueue queue = new ArrayQueue(10);
        for (int i = 0; i < 10; ++i) {
            queue.enqueue(i);
            assertEquals(queue.peek(), 0.0, 0.001);
            assertEquals(queue.peekend(), i, 0.001);
            assertEquals(queue.size(), i+1);
        }

        queue.poppush(20);
        assertEquals(queue.peekend(), 20, 0.001);
    }

    @Test
    public void testPopPush() {
        ArrayQueue queue = new ArrayQueue(10);
        for (int i = 0; i < 10; ++i) {
            queue.enqueue(i);
        }

        for (int i = 100; i < 110; ++i) {
            double popped = queue.poppush(i);
            assertEquals(popped, i-100, 0.001);
        }
    }


    @Test
    public void testStatistics() {
        ArrayQueue queue = new ArrayQueue(10);
        for (int i = 0; i < 10; ++i) {
            queue.enqueue(10);
        }

        assertEquals(queue.getTotal(), 100, 0.001);
        assertEquals(queue.getAverage(), 10, 0.001);
        assertEquals(queue.getMedian(), 10, 0.001);

        for (int i = 0; i < 5; ++i) {
            queue.poppush(20);
        }

        assertEquals(queue.getTotal(), 150, 0.001);
        assertEquals(queue.getAverage(), 15, 0.001);
        assertEquals(queue.getMedian(), 15, 0.001);

        queue.poppush(18);
        assertEquals(queue.getMedian(), 19, 0.001);
    }


}
