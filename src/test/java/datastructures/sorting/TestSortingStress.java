package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;


/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSortingStress extends BaseTest { 
    @Test(timeout=5*SECOND)
    public void testTopKSortLargeInput() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 500000; i++) {
            list.add(i);
        }
        IList<Integer> output = Searcher.topKSort(200000, list);
        assertEquals(200000, output.size());   
    }
    
    @Test(timeout=5*SECOND)
    public void testArrayHeapLargeInput() {
        IPriorityQueue<Integer> heap = new ArrayHeap<Integer>();
        for (int i = 0; i < 100000; i++) {
            heap.insert(i);
        }
        for (int i = 200000; i > 100000; i--) {
            heap.insert(i);
        }
        for (int i = 0; i < 200000; i++) {
            heap.removeMin();
        }  
    }
}
