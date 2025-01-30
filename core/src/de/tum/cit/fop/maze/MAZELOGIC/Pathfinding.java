package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.*;

/**
 * The {@code Pathfinding} class provides functionality to find a path between two points
 * within a maze using the A* (A-star) algorithm. It operates on a 2D grid represented
 * by an {@link Array} of {@link Array} of {@code Integer}, where {@code 0} denotes a walkable
 * tile and any other value denotes an obstacle.
 * <p>
 * This class is designed to be used statically without the need for instantiation.
 * </p>
 *
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>{@code
 * Array<Array<Integer>> maze = new Array<>();
 * // Initialize maze with appropriate values
 * Vector2 start = new Vector2(0, 0);
 * Vector2 goal = new Vector2(5, 5);
 * List<Vector2> path = Pathfinding.findPath(maze, start, goal);
 * }*</pre>
 * </p>
 *
 * @see Vector2
 * @see Array
 */
public class Pathfinding {
    /**
     * Finds a path from the {@code start} position to the {@code goal} position within the given {@code maze}.
     * <p>
     * The method uses the A* algorithm to compute the shortest path based on Manhattan distance heuristic.
     * </p>
     *
     * @param maze  a 2D grid representing the maze, where {@code 0} indicates a walkable tile and any other value indicates an obstacle
     * @param start the starting position within the maze
     * @param goal  the target position within the maze
     * @return a {@link List} of {@link Vector2} representing the path from {@code start} to {@code goal}.         Returns an empty list if no path is found.
     * @throws IllegalArgumentException if {@code maze}, {@code start}, or {@code goal} is {@code null}
     */
    public static List<Vector2> findPath(Array<Array<Integer>> maze, Vector2 start, Vector2 goal) {
        if (maze == null) {
            throw new IllegalArgumentException("Maze cannot be null.");
        }
        if (start == null || goal == null) {
            throw new IllegalArgumentException("Start and goal positions cannot be null.");
        }

        // Boundary checks
        if (!isPositionValid(maze, start) || !isPositionValid(maze, goal)) {
            System.out.println("dash");
//            throw new IllegalArgumentException("Start or goal position is out of maze bounds or not walkable.");
        }

        // PriorityQueue to select the node with the lowest f score
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        // Map to keep track of nodes in the open list for quick access and updates
        Map<Vector2, Node> openMap = new HashMap<>();
        // Set to keep track of visited nodes
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(start, null, 0, getHeuristic(start, goal));
        openList.add(startNode);
        openMap.put(start.cpy(), startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            openMap.remove(current.position);

            // Check if the goal has been reached
            if (current.position.equals(goal)) {
                return constructPath(current);
            }

            closedList.add(current);

            // Iterate through all valid neighbors
            for (Vector2 neighborPos : getNeighbors(maze, current.position)) {
                if (closedList.contains(new Node(neighborPos))) {
                    continue;
                }

                int tentativeG = current.g + 1;
                Node existingNeighbor = openMap.get(neighborPos);

                if (existingNeighbor == null) {
                    // Neighbor not in open list, add it
                    Node neighborNode = new Node(neighborPos, current, tentativeG, getHeuristic(neighborPos, goal));
                    openList.add(neighborNode);
                    openMap.put(neighborPos.cpy(), neighborNode);
                } else if (tentativeG < existingNeighbor.g) {
                    // Found a better path to the neighbor, update it
                    openList.remove(existingNeighbor);
                    Node updatedNeighbor = new Node(neighborPos, current, tentativeG, existingNeighbor.h);
                    openList.add(updatedNeighbor);
                    openMap.put(neighborPos.cpy(), updatedNeighbor);
                }
            }
        }

        // No path found
        return Collections.emptyList();
    }

    /**
     * Constructs the path from the goal node back to the start node by traversing the parent links.
     * <p>
     * The path is returned in order from start to goal.
     * </p>
     *
     * @param node the goal node from which to start constructing the path
     * @return a {@link List} of {@link Vector2} representing the path from start to goal
     */
    private static List<Vector2> constructPath(Node node) {
        List<Vector2> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.position.cpy()); // Add to the beginning to reverse the order
            node = node.parent;
        }
        return path;
    }

    /**
     * Retrieves all valid neighboring positions (up, down, left, right) that are walkable.
     * <p>
     * A neighbor is considered valid if it is within the maze bounds and the corresponding tile is walkable.
     * </p>
     *
     * @param maze     a 2D grid representing the maze
     * @param position the current position within the maze
     * @return a {@link List} of {@link Vector2} representing all walkable neighboring positions
     */
    private static List<Vector2> getNeighbors(Array<Array<Integer>> maze, Vector2 position) {
        List<Vector2> neighbors = new ArrayList<>();
        int x = (int) position.x;
        int y = (int) position.y;

        // Define potential directions: up, down, left, right
        int[][] directions = {
                { -1, 0 }, // Left
                { 1, 0 },  // Right
                { 0, -1 }, // Down
                { 0, 1 }   // Up
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Check bounds
            if (newX >= 0 && newX < maze.size && newY >= 0 && newY < maze.get(newX).size) {
                // Check if the tile is walkable (0)
                if (maze.get(newX).get(newY) == 0) {
                    neighbors.add(new Vector2(newX, newY));
                }
            }
        }

        return neighbors;
    }

    /**
     * Calculates the heuristic value (Manhattan distance) between two positions.
     * <p>
     * The Manhattan distance is used as the heuristic for the A* algorithm, representing the minimum number of moves
     * required to reach the goal from the current position without considering obstacles.
     * </p>
     *
     * @param start the starting position
     * @param goal  the target position
     * @return the Manhattan distance between {@code start} and {@code goal}
     */
    private static int getHeuristic(Vector2 start, Vector2 goal) {
        return Math.abs((int) start.x - (int) goal.x) + Math.abs((int) start.y - (int) goal.y);
    }

    /**
     * Validates whether a given position is within the maze bounds and is walkable.
     *
     * @param maze     the maze grid
     * @param position the position to validate
     * @return {@code true} if the position is valid and walkable, {@code false} otherwise
     */
    private static boolean isPositionValid(Array<Array<Integer>> maze, Vector2 position) {
        int x = (int) position.x;
        int y = (int) position.y;
        return x >= 0 && x < maze.size && y >= 0 && y < maze.get(x).size && maze.get(x).get(y) == 0;
    }

    /**
     * The {@code Node} class represents a single position within the maze for the A* algorithm.
     * <p>
     * Each node contains its position, a reference to its parent node, the cost from the start node (g),
     * the heuristic cost to the goal (h), and a method to calculate the total cost (f = g + h).
     * </p>
     */
    static class Node {
        /**
         * The position of the node within the maze.
         */
        final Vector2 position;

        /**
         * The parent node from which this node was reached.
         */
        final Node parent;

        /**
         * The cost from the start node to this node.
         */
        final int g;

        /**
         * The heuristic cost estimate from this node to the goal.
         */
        final int h;

        /**
         * Constructs a new {@code Node} with only a position. Used primarily for containment checks.
         *
         * @param position the position of the node
         */
        Node(Vector2 position) {
            this.position = position.cpy();
            this.parent = null;
            this.g = 0;
            this.h = 0;
        }

        /**
         * Constructs a new {@code Node} with the specified parameters.
         *
         * @param position the position of the node
         * @param parent   the parent node from which this node was reached
         * @param g        the cost from the start node to this node
         * @param h        the heuristic cost estimate from this node to the goal
         */
        Node(Vector2 position, Node parent, int g, int h) {
            this.position = position.cpy();
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        /**
         * Calculates the total cost of the node.
         *
         * @return the sum of the cost from the start node and the heuristic cost to the goal
         */
        int getF() {
            return g + h;
        }

        /**
         * Overrides the {@code equals} method to compare nodes based on their positions.
         *
         * @param o the object to compare with
         * @return {@code true} if the positions are equal, {@code false} otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return position.equals(node.position);
        }

        /**
         * Overrides the {@code hashCode} method to generate a hash based on the node's position.
         *
         * @return the hash code of the node
         */
        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }
}
