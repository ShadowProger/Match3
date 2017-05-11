package com.match3.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FieldParam {
    public int width;
    public int height;
    public int[] generateColors;
    public Array<Vector2> spawnPoints;
    public Array<Vector2> itemPoints;
    public int[][] fieldCells;
    public int[][] backCells;
    public Cell[][] cells;
    public int[][] bounds;
    public Level level;
}
