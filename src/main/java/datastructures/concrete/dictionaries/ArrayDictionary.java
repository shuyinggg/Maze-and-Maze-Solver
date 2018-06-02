package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    private int size;
    private int cap;

    public ArrayDictionary() {
        this.size = 0;
        this.cap = 10; // initial capacity
        pairs = makeArrayOfPairs(cap);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        V value = null;
        int i = 0;
        for (i = 0; i < this.size; i++) {
            Pair<K, V> pair = this.pairs[i];
            if (pair.key == null && key == null) {
                return pair.value;
            } else if (pair.key != null && pair.key.equals(key)) {
                return pair.value;
            } 
        } 
        if (i == this.size) {
            throw new NoSuchKeyException();    
        } 
        return value;
    }

    @Override
    public void put(K key, V value) {
        //resize if necessary
        int i = 0;
          if (this.size == cap) {
              cap = cap * 2; 
              Pair<K, V>[] temp = makeArrayOfPairs(cap);
              for (i = 0; i < this.size; i++) {
              temp[i] = this.pairs[i];
              }
              this.pairs = temp;
          }
        //put
        for (i = 0; i < this.size; i++) {
            Pair<K, V> pair = this.pairs[i];
            if (pair.key == null && key == null) {
                pair.value = value;
                break;
            } else if (pair.key != null && pair.key.equals(key)) {
                pair.value = value;
                break;
            } 
        } 
        if (i == this.size) {
                Pair<K, V> newPair = new Pair<K, V>(key, value);
                this.pairs[this.size] = newPair;
                this.size++;
         } 
    }

    @Override
    public V remove(K key) {
        V value = null;
        int i = 0;
        for (i = 0; i < this.size; i++) {
            Pair<K, V> pair = this.pairs[i];
            if (pair.key == null && key == null) {
                this.pairs[i] = this.pairs[size - 1];
                this.size--;
                return pair.value;
            } else if (pair.key != null && pair.key.equals(key)) {
                this.pairs[i] = this.pairs[size - 1];
                this.size--;
                return pair.value;
            } 
        } 
        if (i == this.size) {
            throw new NoSuchKeyException();    
        } 
        return value;
    }

    @Override
    public boolean containsKey(K key) {  
        for (int i = 0; i < this.size; i++) {
            Pair<K, V> pair = pairs[i];
            if (pair.key == null && key == null) {
                return true;
            } else if (pair.key != null && pair.key.equals(key)) { 
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<K, V>(this.pairs, this.size);
    }
    
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[] pairs;
        private int index = 0;
        private int size;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int size) {
            this.pairs = pairs;
            this.size = size;
        }
        
        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public KVPair<K, V> next() {
            if (hasNext()) {
                KVPair<K, V> temp = new KVPair<K, V>(pairs[index].key, pairs[index].value);
                index++;
                return temp;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
  
}
