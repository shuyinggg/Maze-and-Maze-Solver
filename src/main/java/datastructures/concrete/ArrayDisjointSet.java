package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;

import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers; 
    
    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.
    private IDictionary<T, Integer> pairs;
    private int rep;
    private int cap = 100;
    
    //constructor
    public ArrayDisjointSet() {
        this.pairs = new ChainedHashDictionary<T, Integer>();
        this.pointers = new int[cap];
        this.rep = 0;    
    }

    @Override
    public void makeSet(T item) {
        if (this.pairs.containsKey(item)) {
            throw new IllegalArgumentException();
        } else {
            this.pairs.put(item, rep);
            pointers[rep] = -1;
            this.rep++;
            
            if (this.rep == this.cap) {
                this.cap *= 2;
                int[] newPointers = new int[cap];
                int p = 0;
                for (Integer i : this.pointers) {
                    newPointers[p] = i;
                    p++;
                }
                this.pointers = newPointers;
            }
            
        }
        
    }

    @Override
    public int findSet(T item) {
        if (!this.pairs.containsKey(item)) {
            throw new IllegalArgumentException();
        } else {
            int i = this.pairs.get(item);
            while (i > 0) {
                int parent = pointers[i];
                if (parent < 0) {
                    return i;
                } else {
                    i = parent;
                }
            }
            return i;
            
        }
    }

    @Override
    public void union(T item1, T item2) {
       if ((!this.pairs.containsKey(item1)) ||
               (!this.pairs.containsKey(item2))) {
           throw new IllegalArgumentException();
       } else {
           int rep1 = findSet(item1);
           int rep2 = findSet(item2);
           if (rep1 == rep2) {
               throw new IllegalArgumentException();
           } else {
               if (pointers[rep1] == pointers[rep2]) {
                   pointers[rep1] = pointers[rep1] - 1;
                   pointers[rep2] = rep1;
               } else if (pointers[rep1] < pointers[rep2]) {
                   pointers[rep2] = rep1;
               } else {
                   pointers[rep1] = rep2;
               }
           }
       }
    }
    
}
