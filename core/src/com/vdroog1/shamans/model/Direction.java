package com.vdroog1.shamans.model;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by st on 12/7/14.
 *
 * The order of directions is clockwise. It affects the calculation of normals.
 * Positions of directions is also important. It affects the calculation of
 * directions by offset (getByOffset(dx, dy) method)
 */
public enum Direction {
    LEFT(-1, 0), UP_LEFT(-1, -1), UP(0, -1), UP_RIGHT(1, -1),
    RIGHT(1, 0), DOWN_RIGHT(1, 1), DOWN(0, 1), DOWN_LEFT(-1, 1);

    private static final float SEGMENT_ANGLE = MathUtils.PI / 4f;
    private static Direction[] directions;

    private Direction opposite;
    private int dx, dy;

    static {
        opposite(UP, DOWN);
        opposite(LEFT, RIGHT);
        opposite(UP_RIGHT, DOWN_LEFT);
        opposite(UP_LEFT, DOWN_RIGHT);

        directions = values();
    }

    public static Direction[] getDirections() {
        return directions;
    }

    public static Direction getByOffset(float dx, float dy) {
        float angle = MathUtils.PI + SEGMENT_ANGLE / 2f + MathUtils.atan2(dy, dx);
        int steps = (int) (angle / SEGMENT_ANGLE);
        if (steps < 0) steps += directions.length;
        steps %= directions.length;
        return directions[steps];
    }

    private static void opposite(Direction first, Direction second) {
        first.opposite = second;
        second.opposite = first;
    }

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction getOpposite() {
        return opposite;
    }

    public Direction getNormal() {
        int index = ordinal() + 2; // + 90 degrees
        index %= directions.length;
        return directions[index];
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
};
