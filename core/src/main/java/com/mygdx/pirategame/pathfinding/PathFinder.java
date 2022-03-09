package com.mygdx.pirategame.pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.pirategame.screen.GameScreen;

import java.util.*;

public class PathFinder {

    /**
     * Used to determine if debug information about the pathfinder should be displayed
     */
    public static final boolean PATHFINDERDEBUG = true;

    private final GameScreen gameScreen;
    private final float tileSize;

    /**
     * NOTE: the greater the gradient, the less accurate the result is to the true result but results will be generated faster
     *
     * @param gameScreen The gameScreen for the world
     * @param tileSize   How small each tile in the tilemap should be (ie 5 game units large tiles)
     */
    public PathFinder(GameScreen gameScreen, float tileSize) {
        this.gameScreen = gameScreen;
        this.tileSize = tileSize;

    }

    /**
     * @param sourceX      the x source location
     * @param sourceY      the y source location
     * @param destinationX the x destination
     * @param destinationY the y destination
     * @return the path as a list of checkpoint, or null if no path could be found
     */
    public List<Checkpoint> getPath(float sourceX, float sourceY, float destinationX, float destinationY, float width, float height) {

        // checking if the start or finish location is an invalid
        if (!isTraversable(sourceX, sourceY, width, height) || !isTraversable(destinationX, destinationY, width, height)) {
            return null;
        }

        destinationX = destinationX - (destinationX % tileSize);
        destinationY = destinationY - (destinationY % tileSize);
        sourceX = sourceX - (sourceX % tileSize);
        sourceY = sourceY - (sourceY % tileSize);

        // converting locations into tilemap equivalents
        Checkpoint source = new Checkpoint(sourceX, sourceY, tileSize);
        Checkpoint dest = new Checkpoint(destinationX, destinationY, tileSize);

        PriorityQueue<PathNode> open = new PriorityQueue<>();

        List<PathNode> close = new ArrayList<>();

        open.add(new PathNode(source, 0, dest, null));
        int count = 0;
        PathNode solutionNode = null;
        while (!open.isEmpty()) {
            count++;
            if (count > 1000) {
                break;
            }
            PathNode currentNode = open.poll();
            if (currentNode.checkpoint.equals(dest)) {
                // if we have found the solution
                solutionNode = currentNode;
                break;
            }

            // looping through successor nodes
            for (PathNode successorNode : successor(currentNode, width, height)) {
                if (open.contains(successorNode)) {
                    // already in the fringe
                    continue;
                } else if (close.contains(successorNode)) {
                    // already visited
                    continue;
                } else {
                    // path cost is too great, limiting scope
                    if (successorNode.fx > 10000) {
                        continue;
                    }
                    open.add(successorNode);
                }

            }

        }

        if (solutionNode == null) {
            return null;
        }

        List<Checkpoint> checkpoints = new ArrayList<>();
        checkpoints.add(dest);
        PathNode nextNode = solutionNode;

        while (nextNode.parent != null) {
            checkpoints.add(nextNode.parent.checkpoint);
            nextNode = nextNode.parent;
        }

        Collections.reverse(checkpoints);
        return checkpoints;
    }

    /**
     * Used to lookup a location in the tilemap to check if the location is traversable
     *
     * @param xb     the x coord in game space
     * @param yb     the y coord in game space
     * @param width  the width of the object
     * @param height the height of the object
     * @return if the location is traversable
     */
    public boolean isTraversable(float xb, float yb, float width, float height) {

        float hw = width * 4;
        float hh = height * 4;
        Vector2[] vlst = new Vector2[4];
        vlst[0] = new Vector2(xb - hw, yb - hh);
        vlst[1] = new Vector2(xb + hw, yb - hh);
        vlst[2] = new Vector2(xb - hw, yb + hh);
        vlst[3] = new Vector2(xb + hw, yb + hh);
        for (Vector2 v : vlst) {
            int x = (int) (v.x / tileSize);
            int y = (int) (v.y / tileSize);
            TiledMapTileLayer islands = (TiledMapTileLayer) gameScreen.getMap().getLayers().get("islands");

            if (isColliding(islands, x, y)) {
                return false;
            }


            TiledMapTileLayer rocks = (TiledMapTileLayer) gameScreen.getMap().getLayers().get("rocks + leaves");

            if (isColliding(rocks, x, y)) {
                return false;
            }

        }

        return true;
    }

    private boolean isColliding(TiledMapTileLayer layer, int x, int y) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) {
            return false;
        }

        return cell.getTile().getId() != 0;
    }

    /**
     * Used to get a list of all successor nodes of the provided node
     *
     * @param node The node to get the successors of
     * @return The list of successors
     */
    private List<PathNode> successor(PathNode node, float width, float height) {
        List<PathNode> toReturn = new ArrayList<>();
        int mapWidth = gameScreen.getTileMapWidth();
        int mapHeight = gameScreen.getTileMapHeight();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {

                if (x == 0 && y == 0) {
                    // cannot travel to itself as a successor
                    continue;
                }

                float calcX = node.checkpoint.x + (x * tileSize);
                float calcY = node.checkpoint.y + (y * tileSize);

                if (calcX >= mapWidth * tileSize) {
                    continue;
                }
                if (calcY >= mapHeight * tileSize) {
                    continue;
                }

                if (isTraversable(calcX, calcY, width, height)) {
                    toReturn.add(new PathNode(new Checkpoint(calcX, calcY, tileSize), (float) Math.sqrt(Math.abs(x * tileSize) + Math.abs(y * tileSize)), node.dest, node));
                }
            }
        }
        return toReturn;
    }

    /**
     * Used as to avoid repeated calculations and to store the checkpoint within the priority list
     */
    private class PathNode implements Comparable<PathNode> {
        float fx;
        float gx;
        Checkpoint checkpoint;
        Checkpoint dest;
        PathNode parent;

        public PathNode(Checkpoint checkpoint, float gx, Checkpoint dest, PathNode parent) {
            this.checkpoint = checkpoint;
            this.dest = dest;
            this.parent = parent;
            if (parent == null) {
                this.gx = gx;
            } else {
                this.gx = gx + parent.gx;
            }
            fx = hx(dest) + this.gx;
        }

        private float hx(Checkpoint dest) {
            return (float) Math.sqrt(Math.pow(checkpoint.x - dest.x, 2) + Math.pow(checkpoint.y - dest.y, 2));
        }

        /**
         * comparator for the priority list
         *
         * @param o the other PathNode to compare it to
         * @return The comparison of fx
         */
        @Override
        public int compareTo(PathNode o) {
            return (int) (fx - o.fx);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathNode pathNode = (PathNode) o;
            return checkpoint.equals(pathNode.checkpoint);
        }

        @Override
        public String toString() {
            return "PathNode{" +
                    "fx=" + fx +
                    ", gx=" + gx +
                    ", checkpoint=" + checkpoint +
                    ", dest=" + dest +
                    ", parent=" + parent +
                    '}';
        }
    }
}
