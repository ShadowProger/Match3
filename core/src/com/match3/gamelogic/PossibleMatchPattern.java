package com.match3.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PossibleMatchPattern {
    public Vector2 napr;
    public Array<Vector2> gems;

    public PossibleMatchPattern() {
        napr = new Vector2(0, 0);
        gems = new Array<Vector2>();
    }
}
