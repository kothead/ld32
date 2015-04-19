package com.vdroog1.shamans.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by kettricken on 19.04.2015.
 */
public class Path {

    Array<Step> steps = new Array<Step>();

    public int getLength() {
        return steps.size;
    }

    public Step getStep(int index) {
        return (Step) steps.get(index);
    }

    public int getX(int index) {
        return (int) getStep(index).x;
    }

    public int getY(int index) {
        return (int) getStep(index).y;
    }

    public void appendStep(int x, int y) {
        steps.add(new Step(x, y));
    }

    public void prependStep(int x, int y) {
        steps.insert(0, new Step(x, y));
    }

    public boolean contains(int x, int y) {
        return steps.contains(new Step(x, y), false);
    }

    public void removeStep(int i) {
        steps.removeIndex(i);
    }

    private class Step extends Vector2 {

        public Step(int x, int y) {
            set(x, y);
        }

        public int hashCode() {
            return (int) (x * y);
        }

        public boolean equals(Object other) {
            if (other instanceof Step) {
                Step o = (Step) other;

                return (o.x == x) && (o.y == y);
            }
            return false;
        }
    }
}
