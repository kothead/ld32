package com.vdroog1.shamans.ai;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by kettricken on 19.04.2015.
 */
public class AStarHeuristics {

    public float getCost(TiledMapTileLayer tiledMapTileLayer,  int sx, int sy, int tx, int ty){
        float dx = tx - sx;
        float dy = ty - sy;

        float result = (float) (Math.sqrt((dx * dx)+(dy * dy)));

        return result;
    }
}
