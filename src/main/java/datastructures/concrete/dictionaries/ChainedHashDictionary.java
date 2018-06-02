package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int cap;
    private int currentSize;
    
    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        currentSize = 0; // initial size
        this.cap = 10; // pick a prime for capacity of initial array 
        chains = makeArrayOfChains(cap);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }
    
    public int getCode(K key) {
        if (key == null) {
            return 0;
        } else {
            return Math.abs(key.hashCode() % this.cap);
        }
    }

    @Override
    public V get(K key) {
        int hash = this.getCode(key);
        if (chains[hash] == null) {
            throw new NoSuchKeyException();
        }
        return chains[hash].get(key);
        
    }

    @Override
    public void put(K key, V value) {
        if (chains[this.getCode(key)] == null) {
            chains[this.getCode(key)] = new ArrayDictionary<K, V>();
        }
        if (chains[this.getCode(key)].containsKey(key)) {
            chains[this.getCode(key)].put(key, value);
        } else {
            chains[this.getCode(key)].put(key, value);
            this.currentSize++;
        }
        if (currentSize / cap >= 1) {
            this.resize();
        }
    }
    
    //resize the array only if lambda >= 1
    public void resize() {      
            this.cap = this.cap * 2;
            IDictionary<K, V>[] newChains = makeArrayOfChains(cap);
            for (KVPair<K, V> pair : this) {
                K key = pair.getKey();
                V value = pair.getValue();
                int newindex = this.getCode(key);
                if (newChains[newindex] == null) {
                    newChains[newindex] = new ArrayDictionary<K, V>();
                }
                newChains[newindex].put(key, value);
            }
            this.chains = newChains;          
    }

    @Override
    public V remove(K key) {
        int hash = this.getCode(key);
        if (chains[hash] == null) {
            throw new NoSuchKeyException();
        } else {
            if (chains[hash].containsKey(key)) {
                this.currentSize--;
            }
            return chains[hash].remove(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        int hash = this.getCode(key);
        if (chains[hash] == null) {
            return false;
        } else {
            return chains[hash].containsKey(key);
        }
    }

    @Override
    public int size() {
        return this.currentSize;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int index;
        private Iterator<KVPair<K, V>> currIter;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.index = 0;
            this.currIter = this.moveIter();
        }
        
        private Iterator<KVPair<K, V>> moveIter() {
                while (index < chains.length && chains[index] == null) {
                    this.index++;
                }
                if (index == chains.length) {
                    return null;
                } else {
                    return chains[index].iterator();
                }
        } 
        
        @Override
        public boolean hasNext() {
            if (this.currIter == null) {
                return false;
            } else if (this.index == chains.length - 1) {
                return currIter.hasNext();
            } else {
                if (!currIter.hasNext()) {
                    this.index++;
                    this.currIter = this.moveIter();
                    if (this.currIter == null) {
                        return false;
                    }  
                }
                return true;
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (hasNext()) {
                return currIter.next();
            } else {
                throw new NoSuchElementException();
            } 
        }           
    }
}
