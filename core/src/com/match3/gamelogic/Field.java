package com.match3.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Field {
    private PossibleMatchPatterns possibleMatchPatterns;
    private int[][] colorMatrix;

    int width;
    int height;
    int[] generateColors;
    Array<Vector2> spawnPoints;
    Array<Vector2> itemPoints;
    int[][] fieldCells;
    int[][] backCells;
    Cell[][] cells;
    int[][] bounds;
    Level level;

    public Field() {}

    public Field(FieldParam fieldParam) {
        width = fieldParam.width;
        height = fieldParam.height;
        generateColors = new int[fieldParam.generateColors.length];
    }

    /*
    private void addWithoutMatch(int[] arr, int value) {}
    private int[] getNeib() {}
    private () {}
    private () {}
    private () {}
    private () {}
    */

    /*
    procedure AddWithoutMatch(var Arr: TIntArray; Value: integer);
    function GetNeib(X, Y: integer; var Mat: TIntMatrix): TIntArray;
    function GetPossibleValues(X, Y: integer; var Mat: TIntMatrix): TIntArray;
    function IsOnField(v: TVector): boolean;
    function GetMatch(v: TVector): TMatch;
    function IsInArr(v: TVector; var arr: TVecArray): boolean;
    */

    /*
    constructor Create(); overload;
    constructor Create(fParam: TFieldParam); overload;
    procedure Shuffle;
    procedure LoadPossibleMatchPatterns(mp: TPossibleMatchPatterns);
    function FindPossibleMatches: TPossibleMatches;
    function FindPossibleMatchesForGem(v: TVector): TPossibleMatches;
    function CheckPossibleMatchPatternForGem(Gem: TVector; IndexPattern: integer): boolean;
    function GetPossibleMatchPattern(Index: integer): TPossibleMatchPattern;
    function FindMatches(Changed: TVecArray): TMatches;
    procedure Swap(Gem1, Gem2: TVector);
    */
}
