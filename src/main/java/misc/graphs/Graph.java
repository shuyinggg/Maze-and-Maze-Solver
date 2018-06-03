package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;


/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */

    private IDictionary<V, IList<E>> graph;
    private IDisjointSet<V> paths;
    private IList<E> edges;
    private IList<V> vertices;
    
    public Graph(IList<V> vertices, IList<E> edges) {
        this.graph = new ChainedHashDictionary<V, IList<E>>();
        this.paths = new ArrayDisjointSet<V>();
        this.vertices = vertices;
        //exceptions
        for (E edge : edges) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            } 
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            if (!vertices.contains(v1) || !vertices.contains(v2)) {
                throw new IllegalArgumentException();
            }
        }
        
        for (V vertex : vertices) {
            this.paths.makeSet(vertex);
            IList<E> myEdges = new DoubleLinkedList<>();
            for (E edge : edges) {
                if (edge.getVertex1().equals(vertex) 
                        || edge.getVertex2().equals(vertex)) {
                    myEdges.add(edge);
                }
            }
            this.graph.put(vertex, myEdges);
            //initialize each vertex to be a connected component
            
        }
        //sort the edges by weight
        this.edges = Searcher.topKSort(edges.size(), edges);
        
       
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.graph.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> mst = new ChainedHashSet<>();
        for (E edge : this.edges) {
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            if (this.paths.findSet(v1) != this.paths.findSet(v2)) {
               mst.add(edge); 
               this.paths.union(v1, v2);
            }
        }
        return mst;    
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        //throw new NotYetImplementedException();
        //Dijkstra's algorithm, infinity: Double.POSITIVE_INFINITY
        //this.graph contains all the vertex and the vertices it connects to;
        //Dijkstra
        if (start.equals(end)) {
            return new DoubleLinkedList<>();
        }
        IList<E> output = this.dijkstra(start, end, this.graph);
        if (output.isEmpty()) {
            throw new NoPathExistsException();
        } else {
            return output;
        }
    }
    
    private IList<E> dijkstra(V start, V end, IDictionary<V, IList<E>> maze) {
        //change all the vertices in the graph into newV
        //initialize the distance and source.dist = 0
        //initialize the MPQ
        IList<NewV<V, E>> newVertices = new DoubleLinkedList<>();
        IPriorityQueue<NewV<V, E>> toProcess = new ArrayHeap<>();
        ISet<V> processed = new ChainedHashSet<>();
        IList<E> output = new DoubleLinkedList<>();
        for (KVPair<V, IList<E>> pair : this.graph) {
            V vertex = pair.getKey();
            if (vertex.equals(start)) {
                newVertices.add(new NewV<V, E>(vertex, 0.0));
                toProcess.insert(new NewV<V, E>(vertex, 0.0));
            } else {
                newVertices.add(new NewV<V, E>(vertex));
            } 
        }
        while (toProcess != null && toProcess.size() != 0) {
            NewV<V, E> u = toProcess.peekMin();
            V uprime = u.getVertex();
            if (uprime.equals(end)) {
                return u.path;
            } else  if (processed.contains(uprime)) {
                toProcess.removeMin();
            } else {
                IList<E> currEdges = this.graph.get(uprime);
                for (E edge : currEdges) {
                    V vprime = edge.getOtherVertex(uprime);
                    for (NewV<V, E> v: newVertices) {
                        if (v.getVertex().equals(vprime)) {
                            if (u.getDistance() + edge.getWeight() < v.getDistance()) {
                                IList<E> path = new DoubleLinkedList<>();
                                for (E e : u.path) {
                                    path.add(e);
                                }
                                path.add(edge);
                                NewV<V, E> dupe = new NewV<>(vprime, u.getDistance() + edge.getWeight(), path);
                                toProcess.insert(dupe);
                               
                            }
                            break;
                        } 
                    }   
                }
                processed.add(toProcess.removeMin().getVertex());
            }
            
            }
            
             
            
        
        return output;    
    }
    private static class NewV<V, E> implements Comparable<NewV<V, E>> {
        public V vertex;
        public double distance;
        IList<E> path;
        
        public NewV(V vertex) {
            this.vertex = vertex;
            this.distance = Double.POSITIVE_INFINITY;
            this.path = new DoubleLinkedList<>();
        }
        
        public NewV(V vertex, Double value) {
            this.vertex = vertex;
            this.distance = value;
            this.path = new DoubleLinkedList<>();
        }
        
        public NewV(V vertex, Double value, IList<E> path) {
            this.vertex = vertex;
            this.distance = value;
            this.path = path;
        }
        
        public V getVertex() {
            return this.vertex;
        }
        public double getDistance() {
            return this.distance;
        }
        
        @Override
        public int compareTo(NewV<V, E> newVertex) {
            if (this.distance == newVertex.getDistance()) {
                return 0;
            } else if (this.distance > newVertex.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
  
}
