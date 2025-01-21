package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.*;

public class Pathfinding {
    public static List<Vector2> findPath(Array<Array<Integer>> maze, Vector2 start, Vector2 goal) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(start, null, 0, getHeuristic(start, goal));
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            if (current.position.equals(goal)) {
                return constructPath(current);
            }

            closedList.add(current);

            for (Vector2 neighbor : getNeighbors(maze, current.position)) {
                if (closedList.contains(new Node(neighbor))) continue;

                int tentativeG = current.g + 1;
                Node neighborNode = new Node(neighbor, current, tentativeG, getHeuristic(neighbor, goal));

                if (!openList.contains(neighborNode) || tentativeG < neighborNode.g) {
                    openList.add(neighborNode);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private static List<Vector2> constructPath(Node node) {
        List<Vector2> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.position);
            node = node.parent;
        }
        return path;
    }

    private static List<Vector2> getNeighbors(Array<Array<Integer>> maze, Vector2 position) {
        List<Vector2> neighbors = new ArrayList<>();
        int x = (int) position.x;
        int y = (int) position.y;

        if (x > 0 && maze.get(x - 1).get(y) == 0) neighbors.add(new Vector2(x - 1, y));
        if (x < maze.size - 1 && maze.get(x + 1).get(y) == 0) neighbors.add(new Vector2(x + 1, y));
        if (y > 0 && maze.get(x).get(y - 1) == 0) neighbors.add(new Vector2(x, y - 1));
        if (y < maze.get(x).size - 1 && maze.get(x).get(y + 1) == 0) neighbors.add(new Vector2(x, y + 1));

        return neighbors;
    }

    private static int getHeuristic(Vector2 start, Vector2 goal) {
        return Math.abs((int) start.x - (int) goal.x) + Math.abs((int) start.y - (int) goal.y);
    }

    static class Node {
        Vector2 position;
        Node parent;
        int g; // cost from start to this node
        int h; // heuristic cost to goal

        Node(Vector2 position) {
            this.position = position;
        }

        Node(Vector2 position, Node parent, int g, int h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        int getF() {
            return g + h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return position.equals(node.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }
}
