package com.match3.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Match {
    Vector2 pos = new Vector2(0, 0);
    int gemType;
    int gemCount;
    Array<Vector2> gems = new Array<Vector2>();
}
