package com.vdroog1.shamans.ai;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.screen.GameScreen;

/**
 * Created by kettricken on 19.04.2015.
 */
public class AStarPathFinder {

    private Array<Node> closed = new Array<Node>();
    private Array<Node> opened = new Array<Node>();

    private TiledMapTileLayer tiledMap;

    private int maxSearchDistance;

    private Node[][] nodes;

    private boolean[][] visited;

    private AStarHeuristics heuristics;
    private String blockedKey = "blocked-top";

    public AStarPathFinder(TiledMapTileLayer tiledMap) {
        heuristics = new AStarHeuristics();
        this.tiledMap = tiledMap;

        visited = new boolean[tiledMap.getWidth()][tiledMap.getHeight()];
        nodes = new Node[tiledMap.getWidth()][tiledMap.getHeight()];
        for (int x = 0; x < tiledMap.getWidth(); x++) {
            for (int y = 0; y < tiledMap.getHeight(); y++) {
                nodes[x][y] = new Node(x,y);
            }
        }
    }

    public Path findPath(Vector2 start, Vector2 target) {
        if (isCellBlocked(target.x, target.y)) return null;

        nodes[(int)start.x][(int)start.y].cost = 0;
        nodes[(int)start.x][(int)start.y].depth = 0;
        clearVisited();
        closed.clear();
        opened.clear();
        opened.add(nodes[(int)start.x][(int)start.y]);

        nodes[(int)target.x][(int)target.y].parent = null;

        int maxDepth = 0;
        while ((maxDepth < maxSearchDistance) && (opened.size != 0)) {
            Node current = getFirstInOpen();
            if (current == nodes[(int)target.x][(int)target.y]){
                break;
            }

            removeFromOpen(current);
            addToClosed(current);

            for (int x = -6; x < 6; x++) {
                for (int y = -3; y < 3; y++) {
                    if (x == 0 && y == 0) {
                        continue;
                    }

                    int xp = x + current.x;
                    int yp = x + current.y;

                    if (!isCellBlocked(xp, yp)) {
                        continue;
                    }

                    if (isValidLocation((int) start.x, (int) start.y, xp, yp + 1)) {
                        float nextStepCost = current.cost + getMovementCost(start, xp, yp);
                        Node neighbour = nodes[xp][yp];
                        visited[xp][yp] = true;

                        if (nextStepCost < neighbour.cost) {
                            if (inOpenList(neighbour)) {
                                removeFromOpen(neighbour);
                            }
                            if (inClosedList(neighbour)) {
                                removeFromClosed(neighbour);
                            }
                        }

                        if (!inOpenList(neighbour) && !inClosedList(neighbour)) {
                            neighbour.cost = nextStepCost;
                            neighbour.heuristic = getHeuristicCost(xp, yp, (int) target.x, (int) target.y);
                            maxDepth = Math.max(maxDepth, neighbour.setParent(current));
                            addToOpen(neighbour);
                        }
                    }
                }
            }
        }

        if (nodes[(int) target.x][(int) target.y].parent == null) {
            return null;
        }

        Path path = new Path();
        Node targetNode = nodes[(int) target.x][(int) target.y];
        while (targetNode != nodes[(int) start.x][(int) start.y]) {
            path.prependStep(targetNode.x, targetNode.y);
            targetNode = targetNode.parent;
        }
        path.prependStep((int) start.x, (int) start.y);

        return path;
    }

    private void clearVisited() {
        for (int x = 0; x < tiledMap.getWidth(); x++) {
            for (int y = 0; y < tiledMap.getHeight(); y++) {
                visited[x][y] = false;
            }
        }
    }

    private void addToOpen(Node node) {
        opened.add(node);
    }

    private void removeFromClosed(Node node) {
        closed.removeValue(node, true);
    }

    private boolean inClosedList(Node node) {
        return closed.contains(node, true);
    }

    private boolean inOpenList(Node node) {
        return opened.contains(node, true);
    }

    private float getMovementCost(Vector2 start, int xp, int yp) {
        return 1;
    }

    private void addToClosed(Node current) {
        closed.add(current);
    }

    private void removeFromOpen(Node current) {
        opened.removeValue(current, true);
    }

    private Node getFirstInOpen() {
        return opened.first();
    }

    protected boolean isValidLocation(int sx, int sy, int x, int y) {
        boolean invalid = (x < 0) || (y < 0) || (x >= tiledMap.getWidth()) || (y >= tiledMap.getHeight());

        if (!invalid && ((sx != x) || (sy != y))) {
            invalid = isCellBlocked(x, y);
        }

        return !invalid;
    }

    public float getHeuristicCost(int x, int y, int tx, int ty) {
        return heuristics.getCost(tiledMap, x, y, tx, ty);
    }

    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = tiledMap.getCell(getCellX(x), getCellY(y));
        return cell != null && cell.getTile() != null
                && cell.getTile().getProperties().containsKey(blockedKey);
    }

    private int getCellX(float x) {
        return (int) (x / tiledMap.getTileWidth() / GameScreen.UNIT_SCALE);
    }

    private int getCellY(float y) {
        return (int) (y / tiledMap.getTileHeight() / GameScreen.UNIT_SCALE);
    }

    private class Node implements Comparable {
        private int x;
        private int y;
        private float cost;
        private Node parent;
        private float heuristic;
        private int depth;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int setParent(Node parent) {
            depth = parent.depth + 1;
            this.parent = parent;

            return depth;
        }

        public int compareTo(Object other) {
            Node o = (Node) other;

            float f = Node.this.heuristic + cost;
            float of = o.heuristic + o.cost;

            if (f < of) {
                return -1;
            } else if (f > of) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
