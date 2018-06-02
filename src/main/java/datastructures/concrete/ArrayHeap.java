package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;

    // Feel free to add more fields and constants.
    private int cap = 100;
    private int currentSize;

    public ArrayHeap() {
        this.heap = makeArrayOfT(cap);
        this.currentSize = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        T item = null;
        if (this.currentSize == 0) {
            throw new EmptyContainerException();
        } else if (this.currentSize == 1) {
            item = this.heap[0];
            this.heap = null;
            this.currentSize--;
        } else {
            item = this.heap[0];
            this.heap[0] = this.heap[currentSize - 1];
            this.heap[currentSize - 1] = null;
            this.currentSize--;
            //find its four children, compare and swap (recursion)
            //Base case: smaller than all children
            // 4i + n, n-th child
            this.percolateDown(0);
        }
        return item;
    }

    @Override
    public T peekMin() {
        if (this.currentSize == 0) {
            throw new EmptyContainerException();
        } else {
            return this.heap[0];
        }
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        } else {
            //put at the end
            this.heap[currentSize] = item;
            this.currentSize++;
            //find its parent compare and swap (recursion)
            //Base Case: greater than its parents
            //(fourth children: i/4 - 1; i/4 otherwise)
            this.percolateUp(currentSize - 1);
        }
        if (this.currentSize >= this.cap) {
            this.resize();
        }
    }

    @Override
    public int size() {
        return this.currentSize;
    }
    
    private void percolateUp(int i) {
        //find its parent
        int parent;
        if (i % NUM_CHILDREN == 0) {
            parent = i/NUM_CHILDREN - 1;
        } else {
            parent = i/NUM_CHILDREN;
        }
        if (parent < 0) {
            return;
        } else if (this.heap[parent].compareTo(this.heap[i]) <= 0) {
            return;
        } else { 
            T temp = this.heap[i];
            this.heap[i] = this.heap[parent];
            this.heap[parent] = temp;
            this.percolateUp(parent);
        }
    }
    
    private void percolateDown(int i) {
        int child;
        // k - 1 : number of children
        child = findMinChild(i);
        if (child == i) {
            return;
        } else {
            T temp = this.heap[i];
            this.heap[i] = this.heap[child];
            this.heap[child] = temp;
            this.percolateDown(child);
        }
    }
    
    private int findMinChild(int i) {
        int child;
        T min = this.heap[i];
        int minIndex = i;
        for (int k = 1; k <= NUM_CHILDREN; k++) {
            child = NUM_CHILDREN * i + k; 
            if (child > this.currentSize - 1) {
                break;
            } else if (this.heap[child] != null) {
                if (min.compareTo(this.heap[child]) > 0) {
                    min = this.heap[child];
                    minIndex = child;
                }
            } else {
                break;
            }
        }
        return minIndex;
    }
    
    private void resize() {
        this.cap = this.cap * 2;
        T[] newHeap;
        newHeap= makeArrayOfT(cap);
        int i = 0;
        for (T item : heap) {
            newHeap[i] = item;
            i++;
        }
        this.heap = newHeap;
    }
    
}
