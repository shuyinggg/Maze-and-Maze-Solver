package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    //adds the given item at the end of the list;
    public void add(T item) {
        if (front == null || back == null) {
            this.front = new Node<T>(null, item, null);
            this.back = this.front;
        } else {
            Node<T> current = this.back;
            this.back = new Node<T>(null, item, null);
            current.next = this.back;
            this.back.prev = current;
        }
        this.size++; //increment size
    }

    @Override
    //removes and returns the item from the end of the given list
    public T remove() {
        if (this.front == null || this.back == null) {
            throw new EmptyContainerException(); //empty list
        }
        else {
           Node<T> removedItem = this.back;
            this.back = this.back.prev;
            if (this.back != null) {
                this.back.next = null;
                removedItem.prev = null;
            }
            this.size--;
            return removedItem.data;
            }
    }

    @Override
    //returns the item at the given location
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        else {
 
            return findNode(index).data;
        }
    }

    @Override
    //overwrites the element located at the given index with the new item
    public void set(int index, T item) {
        if (index < 0 || this.size == 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        else {
            if (this.size == 1) {
               remove();
               add(item);
            } else {
                Node<T> current = findNode(index);
                Node<T> temp = new Node<T>(current.prev, item, current.next);
                if (index == 0) {//set the front
                    current.next.prev = temp;
                    this.front = temp;
                    current.next = null;
                } else if (index == this.size - 1){ //set the end
                    current.prev.next = temp;
                    this.back = temp;
                    current.prev = null;
                    
                } else {//set the middle
                    current.prev.next = temp;
                    current.next.prev = temp;
                    current.prev = null;
                    current.next = null;
                }
            }
            
        }
    }

    @Override
    //insert a new item at the given index
    public void insert(int index, T item) {
        if (index > this.size || index < 0) {
            throw new IndexOutOfBoundsException();
        } else if (index == this.size){//insert at end
           add(item); 
        } else {
            Node<T> current = findNode(index);
            //insert
            Node<T> temp = new Node<T>(current.prev, item, current);
            if (current.prev == null){//insert at front
                this.front = temp;
                current.prev = temp;
            } else {
                current.prev.next = temp;
                current.prev = temp;
            }
            this.size++; 
        }
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } else {
            T item = null;
            if (index == 0 && this.size != 1) {
                item = this.front.data;
                this.front = this.front.next;
                this.front.prev = null;
                this.size--;
            } else if (index == this.size - 1) {
                item = remove();
            } else {
                Node<T> current = findNode(index);
                item = current.data;
                current.prev.next = current.next;
                current.next.prev = current.prev;
                current.prev = null;
                current.next = null;
                this.size--;
            }
            return item;
        }
    }

    @Override
    public int indexOf(T item) {
        if ((this.front == null && this.back == null) || !contains(item)) {
            return -1;           
        }
        int counter = 0;
        Node<T> current = this.front;
        while (current.data != item && current.next != null) {
                  current = current.next;
                  counter++;
        }
        return counter;
 }

    @Override
    //returns the number of the elements in the list
    public int size() {
        return this.size;
    }

    @Override
    //whether the list contains the given element
    public boolean contains(T other) {
        Node<T> current = this.front;
        while (current != null) {
            if (current.data == null) {// null key
                return true;
            } else if (current.data.equals(other)){//only compares object
                return true;
            } 
                current = current.next;
            }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }


        // Feel free to add additional constructors or methods to this class.
    }
    
    //if the given index is close to the end, traverse from end; or near front, from front 
    private Node<T> findNode(int index) {
        Node<T> current;
        if (index < this.size / 2) {// near front
            current = this.front;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {//near end
            current = this.back;
            for (int i = this.size - 1; i > index; i--) {
                current = current.prev;
            }  
        }
        return current;
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (hasNext()) {
                T item = current.data;
                current = current.next;
                return item;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
