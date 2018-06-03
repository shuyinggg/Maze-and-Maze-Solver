package mazes.generators.maze;

import datastructures.concrete.ChainedHashSet;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.graphs.Graph;

import java.util.Random;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.

        //throw new NotYetImplementedException();
        Random rand = new Random();
        ISet<Wall> toRemove = new ChainedHashSet<>();
        
        //iterators assume user won't modify the data
        for (Wall wall : maze.getWalls()) {
            if (!maze.getUntouchableWalls().contains(wall)) {
                toRemove.add(wall);
            }
        }
        //assign random distance to each edge
        for (Wall wall : toRemove) {
            wall.setDistance(rand.nextDouble());
        }
        //create a graph (rooms, walls) and find the mst to get to each room
        Graph<Room, Wall> graph = new Graph<>(maze.getRooms(), toRemove);
        return graph.findMinimumSpanningTree();
        
    }
}
