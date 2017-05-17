package com.match3.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Field {
    private PossibleMatchPatterns possibleMatchPatterns = new PossibleMatchPatterns();
    private int[][] colorMatrix;

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

    private Random random = new Random();



    public Field() {}



    public Field(FieldParam fieldParam) {
        width = fieldParam.width;
        height = fieldParam.height;
        generateColors = new int[fieldParam.generateColors.length];
        for (int i = 0; i < generateColors.length; i++)
            generateColors[i] = fieldParam.generateColors[i];
        spawnPoints = new Array<Vector2>(fieldParam.spawnPoints);
        itemPoints = new Array<Vector2>(fieldParam.itemPoints);
        fieldCells = new int[height][width];
        backCells = new int[height][width];
        cells = new Cell[height][width];
        colorMatrix = new int[height][width];
        bounds = new int[height + 2][width + 2];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                fieldCells[i][j] = fieldParam.fieldCells[i][j];
                backCells[i][j] = fieldParam.backCells[i][j];
                if (fieldCells[i][j] == 0)
                    cells[i][j] = null;
                else {
                    cells[i][j] = new Cell();
                    cells[i][j].copy(fieldParam.cells[i][j]);
                }
            }
        for (int i = 0; i < (height + 2); i++)
            for (int j = 0; j < (width + 2); j++) {
                bounds[i][j] = fieldParam.bounds[i][j];
            }
        level = new Level();
        level.lType = fieldParam.level.lType;
        level.count = fieldParam.level.count;
        level.mission1 = new Mission();
        level.mission1.mType = fieldParam.level.mission1.mType;
        level.mission1.color = fieldParam.level.mission1.color;
        level.mission1.count = fieldParam.level.mission1.count;
        level.mission2 = new Mission();
        level.mission2.mType = fieldParam.level.mission2.mType;
        level.mission2.color = fieldParam.level.mission2.color;
        level.mission2.count = fieldParam.level.mission2.count;

        generate(fieldParam);
    }



    private void generate(FieldParam fParam) {
        Array<Integer> colors = new Array<Integer>();
        for (int i = 0; i < generateColors.length; i++)
            colors.add(generateColors[i]);
        colors.shuffle();

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (fParam.cells[i][j] != null) {
                    int tmp = fParam.cells[i][j].value;
                    if (tmp >= 1 && tmp <= 6) {
                        int k;
                        for (k = 0; k < generateColors.length; k++)
                            if (generateColors[k] == tmp)
                                break;
                        if (k < generateColors.length)
                            colorMatrix[i][j] = colors.get(k);
                    } else
                        colorMatrix[i][j] = tmp;
                } else
                    colorMatrix[i][j] = 0;
            }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (colorMatrix[i][j] == -1) {
                    Array<Integer> arr = getPossibleValues(j, i);
                    colorMatrix[i][j] = arr.get(random.nextInt(arr.size));
                }
            }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null)
                    if ((cells[i][j].value == -1) || (cells[i][j].value >= 1 && cells[i][j].value <= 6))
                        cells[i][j].value = colorMatrix[i][j];
            }
    }



    private void addWithoutMatch(Array<Integer> arr, int value) {
        if (!arr.contains(value, false))
            arr.add(value);
    }



    private Array<Integer> getNeib(int x, int y) {
        Array<Integer> res = new Array<Integer>();
        if (isOnField(x, y)) {
            int color1;
            int color2;
            if (x >= 2) {
                color1 = colorMatrix[y][x - 1];
                color2 = colorMatrix[y][x - 2];
                if (color1 == color2)
                    addWithoutMatch(res, color1);
            }
            if (y >= 2) {
                color1 = colorMatrix[y - 1][x];
                color2 = colorMatrix[y - 2][x];
                if (color1 == color2)
                    addWithoutMatch(res, color1);
            }
            if (x < width - 2) {
                color1 = colorMatrix[y][x + 1];
                color2 = colorMatrix[y][x + 2];
                if (color1 == color2)
                    addWithoutMatch(res, color1);
            }
            if (y < height - 2) {
                color1 = colorMatrix[y + 1][x];
                color2 = colorMatrix[y + 2][x];
                if (color1 == color2)
                    addWithoutMatch(res, color1);
            }
            if ((x >= 1) && (x < width - 1)) {
                color1 = colorMatrix[y][x - 1];
                color2 = colorMatrix[y][x + 1];
                if (color1 == color2)
                    addWithoutMatch(res, color1);
            }
            if ((y >= 1) && (y < height - 1)) {
                color1 = colorMatrix[y - 1][x];
                color2 = colorMatrix[y + 1][x];
                if (color1 == color2)
                    addWithoutMatch(res, color1);
            }
        }
        return res;
    }



    private Array<Integer> getPossibleValues(int x, int y) {
        HashSet<Integer> values = new HashSet<>();
        for (int i = 0; i < generateColors.length; i++)
            values.add(generateColors[i]);
        Array<Integer> arr = getNeib(x, y);
        for (int num : arr)
            values.remove(num);

        Array<Integer> res = new Array<Integer>();
        for (Integer num : values)
            res.add(num);

        return res;
    }



    private boolean isOnField(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }


    public void shuffle() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null) {
                    colorMatrix[i][j] = cells[i][j].value;
                    if (!cells[i][j].modif) {
                        int tmp = cells[i][j].value;
                        if (tmp >= 1 && tmp <= 6)
                            colorMatrix[i][j] = -1;
                    }
                } else
                    colorMatrix[i][j] = 0;
            }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (colorMatrix[i][j] == -1) {
                    Array<Integer> arr = getPossibleValues(j, i);
                    colorMatrix[i][j] = arr.get(random.nextInt(arr.size));
                }
            }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null) {
                    if (cells[i][j].value == -1 || (cells[i][j].value >= 1 && cells[i][j].value <= 6))
                        cells[i][j].value = colorMatrix[i][j];
                }
            }
    }


    /*
    // test
    public void generateRandom() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null) {
                    colorMatrix[i][j] = cells[i][j].value;
                    if (!cells[i][j].modif) {
                        int tmp = cells[i][j].value;
                        if (tmp >= 1 && tmp <= 6)
                            colorMatrix[i][j] = -1;
                    }
                } else
                    colorMatrix[i][j] = 0;
            }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (colorMatrix[i][j] == -1) {
                    colorMatrix[i][j] = random.nextInt(6) + 1;
                }
            }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null) {
                    if (cells[i][j].value == -1 || (cells[i][j].value >= 1 && cells[i][j].value <= 6))
                        cells[i][j].value = colorMatrix[i][j];
                }
            }
    }
    //
    */


    public void loadPossibleMatchPattern(PossibleMatchPatterns pmp) {
        for (int i = 0; i < 52; i++){
            //possibleMatchPatterns.patterns[i].napr = new Vector2(pmp.patterns[i].napr);
            possibleMatchPatterns.patterns[i].napr = pmp.patterns[i].napr;
            //possibleMatchPatterns.patterns[i].napr.x = pmp.patterns[i].napr.x;
            //possibleMatchPatterns.patterns[i].napr.y = pmp.patterns[i].napr.y;
            possibleMatchPatterns.patterns[i].gems.addAll(pmp.patterns[i].gems);
        }

        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.left3[i] = pmp.left3[i];
        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.right3[i] = pmp.right3[i];
        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.up3[i] = pmp.up3[i];
        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.down3[i] = pmp.down3[i];

        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.left4[i] = pmp.left4[i];
        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.right4[i] = pmp.right4[i];
        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.up4[i] = pmp.up4[i];
        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.down4[i] = pmp.down4[i];

        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.left5[i] = pmp.left5[i];
        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.right5[i] = pmp.right5[i];
        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.up5[i] = pmp.up5[i];
        for (int i = 0; i < 4; i++)
            possibleMatchPatterns.down5[i] = pmp.down5[i];

        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.left6[i] = pmp.left6[i];
        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.right6[i] = pmp.right6[i];
        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.up6[i] = pmp.up6[i];
        for (int i = 0; i < 2; i++)
            possibleMatchPatterns.down6[i] = pmp.down6[i];

        possibleMatchPatterns.left7 = pmp.left7;
        possibleMatchPatterns.right7 = pmp.right7;
        possibleMatchPatterns.up7 = pmp.up7;
        possibleMatchPatterns.down7 = pmp.down7;
    }



    public Array<PossibleMatch> findPossibleMatches() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null) {
                    if (cells[i][j].modif) {
                        if (cells[i][j].gold || cells[i][j].box > 0 || cells[i][j].dirt > 0)
                            colorMatrix[i][j] = -1;
                        else
                            colorMatrix[i][j] = cells[i][j].value;
                    } else
                        colorMatrix[i][j] = cells[i][j].value;
                } else
                    colorMatrix[i][j] = -1;
            }

        Array<PossibleMatch> res = new Array<PossibleMatch>();
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (colorMatrix[i][j] > 0 && colorMatrix[i][j] < 7) {
                    if (!cells[i][j].modif) {
                        Array<PossibleMatch> possibleMatches = findPossibleMatchesForGem(j, i);
                        res.addAll(possibleMatches);
                    }
                }
            }
        return res;
    }



    public Array<PossibleMatch> findPossibleMatchesForGem(int x, int y) {
        Array<PossibleMatch> res = new Array<PossibleMatch>();

        // left
        int index = 0;
        int patternIndex = -1;
        boolean m3 = false, m4 = false, m5 = false, m6 = false, m7 = false;
        for (int i = 0; i < 4; i++) {
            index = possibleMatchPatterns.left3[i];
            if (checkPossibleMatchPatternForGem(x, y, index)) {
                m3 = true;
                break;
            }
        }
        if (m3) {
            patternIndex = index;
            m4 = false;
            for (int i = 0; i < 2; i++) {
                index = possibleMatchPatterns.left4[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m4 = true;
                    break;
                }
            }
            if (m4)
                patternIndex = index;
            m5 = false;
            for (int i = 0; i < 4; i++) {
                index = possibleMatchPatterns.left5[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m5 = true;
                    break;
                }
            }
            if (m5) {
                patternIndex = index;
                m6 = false;
                for (int i = 0; i < 2; i++) {
                    index = possibleMatchPatterns.left6[i];
                    if (checkPossibleMatchPatternForGem(x, y, index)) {
                        m6 = true;
                        break;
                    }
                }
                if (m6) {
                    patternIndex = index;
                    index = possibleMatchPatterns.left7;
                    if (checkPossibleMatchPatternForGem(x, y, index))
                        patternIndex = index;
                }
            }
        }
        if (patternIndex > -1) {
            PossibleMatch pm = new PossibleMatch();
            pm.x = x;
            pm.y = y;
            pm.patternIndex = patternIndex;
            res.add(pm);
        }

        // right
        patternIndex = -1;
        m3 = false;
        for (int i = 0; i < 4; i++) {
            index = possibleMatchPatterns.right3[i];
            if (checkPossibleMatchPatternForGem(x, y, index)) {
                m3 = true;
                break;
            }
        }
        if (m3) {
            patternIndex = index;
            m4 = false;
            for (int i = 0; i < 2; i++) {
                index = possibleMatchPatterns.right4[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m4 = true;
                    break;
                }
            }
            if (m4)
                patternIndex = index;
            m5 = false;
            for (int i = 0; i < 4; i++) {
                index = possibleMatchPatterns.right5[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m5 = true;
                    break;
                }
            }
            if (m5) {
                patternIndex = index;
                m6 = false;
                for (int i = 0; i < 2; i++) {
                    index = possibleMatchPatterns.right6[i];
                    if (checkPossibleMatchPatternForGem(x, y, index)) {
                        m6 = true;
                        break;
                    }
                }
                if (m6) {
                    patternIndex = index;
                    index = possibleMatchPatterns.right7;
                    if (checkPossibleMatchPatternForGem(x, y, index))
                        patternIndex = index;
                }
            }
        }
        if (patternIndex > -1) {
            PossibleMatch pm = new PossibleMatch();
            pm.x = x;
            pm.y = y;
            pm.patternIndex = patternIndex;
            res.add(pm);
        }

        // up
        patternIndex = -1;
        m3 = false;
        for (int i = 0; i < 4; i++) {
            index = possibleMatchPatterns.up3[i];
            if (checkPossibleMatchPatternForGem(x, y, index)) {
                m3 = true;
                break;
            }
        }
        if (m3) {
            patternIndex = index;
            m4 = false;
            for (int i = 0; i < 2; i++) {
                index = possibleMatchPatterns.up4[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m4 = true;
                    break;
                }
            }
            if (m4)
                patternIndex = index;
            m5 = false;
            for (int i = 0; i < 4; i++) {
                index = possibleMatchPatterns.up5[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m5 = true;
                    break;
                }
            }
            if (m5) {
                patternIndex = index;
                m6 = false;
                for (int i = 0; i < 2; i++) {
                    index = possibleMatchPatterns.up6[i];
                    if (checkPossibleMatchPatternForGem(x, y, index)) {
                        m6 = true;
                        break;
                    }
                }
                if (m6) {
                    patternIndex = index;
                    index = possibleMatchPatterns.up7;
                    if (checkPossibleMatchPatternForGem(x, y, index))
                        patternIndex = index;
                }
            }
        }
        if (patternIndex > -1) {
            PossibleMatch pm = new PossibleMatch();
            pm.x = x;
            pm.y = y;
            pm.patternIndex = patternIndex;
            res.add(pm);
        }

        // down
        patternIndex = -1;
        m3 = false;
        for (int i = 0; i < 4; i++) {
            index = possibleMatchPatterns.down3[i];
            if (checkPossibleMatchPatternForGem(x, y, index)) {
                m3 = true;
                break;
            }
        }
        if (m3) {
            patternIndex = index;
            m4 = false;
            for (int i = 0; i < 2; i++) {
                index = possibleMatchPatterns.down4[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m4 = true;
                    break;
                }
            }
            if (m4)
                patternIndex = index;
            m5 = false;
            for (int i = 0; i < 4; i++) {
                index = possibleMatchPatterns.down5[i];
                if (checkPossibleMatchPatternForGem(x, y, index)) {
                    m5 = true;
                    break;
                }
            }
            if (m5) {
                patternIndex = index;
                m6 = false;
                for (int i = 0; i < 2; i++) {
                    index = possibleMatchPatterns.down6[i];
                    if (checkPossibleMatchPatternForGem(x, y, index)) {
                        m6 = true;
                        break;
                    }
                }
                if (m6) {
                    patternIndex = index;
                    index = possibleMatchPatterns.down7;
                    if (checkPossibleMatchPatternForGem(x, y, index))
                        patternIndex = index;
                }
            }
        }
        if (patternIndex > -1) {
            PossibleMatch pm = new PossibleMatch();
            pm.x = x;
            pm.y = y;
            pm.patternIndex = patternIndex;
            res.add(pm);
        }
        return res;
    }



    public boolean checkPossibleMatchPatternForGem(int x, int y, int patternIndex) {
        if (patternIndex < 0 || patternIndex >= possibleMatchPatterns.patterns.length)
            return false;

        int vx = (int) (possibleMatchPatterns.patterns[patternIndex].napr.x + x);
        int vy = (int) (possibleMatchPatterns.patterns[patternIndex].napr.y + y);
        if (isOnField(vx, vy)) {
            if (colorMatrix[vy][vx] == -1 || cells[vy][vx].modif)
                return false;
        } else
            return false;

        boolean res = true;
        for (Vector2 gem : possibleMatchPatterns.patterns[patternIndex].gems) {
            vx = (int) (gem.x + x);
            vy = (int) (gem.y + y);
            if (isOnField(vx, vy)) {
                if (colorMatrix[y][x] != colorMatrix[vy][vx]) {
                    res = false;
                    break;
                }
            } else {
                res = false;
                break;
            }
        }
        return res;
    }



    public PossibleMatchPattern getPossibleMatchPattern(int index) {
        if (index < 0 || index >= possibleMatchPatterns.patterns.length)
            return null;
        return possibleMatchPatterns.patterns[index];
    }



    public Array<Match> findMatches(Array<Vector2> changed) {
        Array<Match> res = new Array<Match>();

        if (changed.size < 1)
            return res;

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (cells[i][j] != null) {
                    if (cells[i][j].modif) {
                        if (cells[i][j].gold || cells[i][j].box > 0 || cells[i][j].dirt > 0)
                            colorMatrix[i][j] = 0;
                        else
                            colorMatrix[i][j] = cells[i][j].value;
                    } else {
                        if (!cells[i][j].hold) {
                            if (cells[i][j].value < 7)
                                colorMatrix[i][j] = cells[i][j].value;
                            else
                                colorMatrix[i][j] = 0;
                        } else
                            colorMatrix[i][j] = 0;
                    }
                } else
                    colorMatrix[i][j] = 0;
            }

        Array<Match> matches = new Array<Match>();

        for (int i = 0; i < changed.size; i++) {
            if (colorMatrix[((int) changed.get(i).y)][((int) changed.get(i).x)] > 0) {
                int cx = (int) changed.get(i).x;
                int cy = (int) changed.get(i).y;
                Match match = getMatch(cx, cy);
                if (match.gemCount > 2)
                    matches.add(match);
            }
        }

        int mi = 0;
        while (mi < matches.size) {
            boolean f = false;
            Vector2 v = matches.get(mi).pos;
            for (int i = 0; i < res.size; i++) {
                if (matches.get(mi).gemType != res.get(i).gemType)
                    continue;
                f = res.get(i).gems.contains(v, false);
                if (f)
                    break;
            }

            if (f)
                mi++;
            else {
                res.add(matches.get(mi));
                int ri = res.size - 1;

                int gi = 1;
                while (gi < res.get(ri).gems.size) {
                    f = false;
                    int index = 0;
                    Vector2 v1 = res.get(ri).gems.get(gi);
                    for (int i = 0; i < matches.size; i++) {
                        if (matches.get(i).gemType != res.get(ri).gemType)
                            continue;
                        Vector2 v2 = matches.get(i).pos;
                        if (v1.equals(v2)) {
                            f = true;
                            index = i;
                            break;
                        }
                    }

                    if (!f) {
                        Match match = getMatch(((int) v1.x), ((int) v1.y));
                        matches.add(match);
                        index = matches.size - 1;
                    }

                    for (int i = 0; i < matches.get(index).gems.size; i++) {
                        v1 = matches.get(index).gems.get(i);
                        if (!res.get(ri).gems.contains(v1, false))
                            res.get(ri).gems.add(v1);
                    }

                    if (matches.get(index).gemCount > res.get(ri).gemCount) {
                        res.get(ri).gemCount = matches.get(index).gemCount;
                        res.get(ri).pos = matches.get(index).pos;
                    }
                    gi++;
                }
                mi++;
            }
        }
        return res;
    }



    private Match getMatch(int x, int y) {
        Match res = new Match();
        Array<Vector2> arr = new Array<Vector2>();

        res.pos.set(x, y);
        int gType = colorMatrix[y][x];
        res.gemType = gType;
        res.gems.add(res.pos);

        // left
        int gx = x;
        int gy = y;
        while (true) {
            gx--;
            if (isOnField(gx, gy)) {
                if (colorMatrix[gy][gx] == gType) {
                    Vector2 v = new Vector2(gx, gy);
                    arr.add(v);
                } else
                    break;
            } else
                break;
        }

        // right
        gx = x;
        gy = y;
        while (true) {
            gx++;
            if (isOnField(gx, gy)) {
                if (colorMatrix[gy][gx] == gType) {
                    Vector2 v = new Vector2(gx, gy);
                    arr.add(v);
                } else
                    break;
            } else
                break;
        }

        if (arr.size >= 2)
            res.gems.addAll(arr);

        // up
        arr.clear();
        gx = x;
        gy = y;
        while (true) {
            gy--;
            if (isOnField(gx, gy)) {
                if (colorMatrix[gy][gx] == gType) {
                    Vector2 v = new Vector2(gx, gy);
                    arr.add(v);
                } else
                    break;
            } else
                break;
        }

        // down
        gx = x;
        gy = y;
        while (true) {
            gy++;
            if (isOnField(gx, gy)) {
                if (colorMatrix[gy][gx] == gType) {
                    Vector2 v = new Vector2(gx, gy);
                    arr.add(v);
                } else
                    break;
            } else
                break;
        }

        if (arr.size >= 2)
            res.gems.addAll(arr);
        res.gemCount = res.gems.size;

        return res;
    }



    public void swap(Vector2 gem1, Vector2 gem2) {
        Cell tmp = new Cell();
        tmp.copy(cells[((int) gem1.y)][((int) gem1.x)]);
        cells[((int) gem1.y)][((int) gem1.x)].copy(cells[((int) gem2.y)][((int) gem2.x)]);
        cells[((int) gem2.y)][((int) gem2.x)].copy(tmp);
    }
}