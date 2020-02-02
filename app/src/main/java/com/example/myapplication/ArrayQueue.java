package com.example.myapplication;

import java.util.Arrays;

public class ArrayQueue {

    double[] queue;
    int head, tail;
    int size;   // number of elements in the queue
    int total;  // cumulative sum of the queue

    /**
     * Constructs an double ArrayQueue with a constant static size.
     * @param containerSize The max number of elements that will be in the Queue
     */
    public ArrayQueue(int containerSize) {
        queue = new double[containerSize];
        head = 0;
        tail = 0;
        size = 0;
        total= 0;
    }


    public int size() {
        return size;
    }

    public double peek() {
        return size > 0 ? queue[head] : 0;
    }

    public double peekend() {
        return size > 0 ? queue[(tail + queue.length - 1)%queue.length] : 0;
    }

    public double getTotal() {
        return total;
    }

    public double getAverage() {
        return total/size;
    }

    public double getMedian() {
        double[] copy = new double[size];
        for (int i = 0; i < size; ++i) {
            copy[i] = queue[(head+i)%queue.length];
        }
        Arrays.sort(copy);
        if (size%2 == 0) {
            return (copy[size/2] + copy[size/2 - 1])/2;
        } else {
            return copy[size/2];
        }
    }

    public void clear(){
        head = 0;
        tail = 0;
    }

    /**
     * Enqueues value to queue.
     * Note: dequeue from a full queue before enqueing
     * @param v The value to add to the queue
     * @return Whether the value was successfully added
     */
    public boolean enqueue(double v) {
        if (size < queue.length) {
            total += v;
            queue[tail] = v;
            tail = (tail+1)%queue.length;
            ++size;

            return true;
        } else {
            return false;
        }
    }

    /**
     * Dequeues head element from the queue. If no elements to dequeue will return 0.
     *
     * @return The value dequeued.
     */
    public double dequeue() {
        if (size > 0) {
            double popped_val = queue[head];
            head = (head+1)%queue.length;
            --size;
            total -= popped_val;
            return popped_val;
        } else {
            return 0;
        }
    }

    public double poppush(double v) {
        double val = dequeue();
        enqueue(v);
        return val;
    }
}
