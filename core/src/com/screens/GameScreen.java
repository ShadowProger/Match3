package com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.match3.Chip;
import com.match3.Match3;
import com.match3.gamelogic.Cell;
import com.match3.gamelogic.Field;
import com.match3.gamelogic.Mach3Game;
import com.match3.gamelogic.Match;
import com.match3.gamelogic.PossibleMatch;
import com.match3.gamelogic.PossibleMatchPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GameScreen implements Screen, GestureDetector.GestureListener, InputProcessor {
    final Match3 game;
    private SpriteBatch batch;
    private InputMultiplexer IM;

    TextureRegion rgBackground;
    TextureRegion[] rgBounds;
    TextureRegion[] rgCellValues;
    TextureRegion rgBack_0;
    TextureRegion rgBack_1;
    TextureRegion rgBack_2;
    TextureRegion rgBox_0;
    TextureRegion rgBox_1;
    TextureRegion rgChain_0;
    TextureRegion rgChain_1;
    TextureRegion rgDirt_0;
    TextureRegion rgDirt_1;
    TextureRegion rgGold;
    TextureRegion rgIce_0;
    TextureRegion rgIce_1;

    private final int CH = 60;
    private Field field;
    private Rectangle playArea;
    private Rectangle fieldArea;
    private Vector2 chipsZero;

    private Array<Vector2> changedCells;

    private int[][] moveMatrix;
    private int[][] expBlockMatrix;
    private int[][] explosionMatrix;

    private int levelSteps;
    private float levelTime;
    private int explosionBarValue;
    private int expMult;
    private int superBonusValue;
    private boolean isGem1Selected;
    private boolean isGem2Selected;
    private int swapPhase;
    private float hintTime;
    private final int bonus1Score = 10;
    private final int bonus2Score = 20;
    private final int bonus3Score = 30;
    private final int bonus4Score = 40;
    private final float MAX_HINT_TIME = 5f;
    private final int EXPLOSION_BAR_MAX_VALUE = 360;

    private Array<PossibleMatch> possibleMatches = new Array<PossibleMatch>();
    private Array<Match> matches = new Array<Match>();
    private PossibleMatch possibleMatch;
    private boolean hintPossibleMatch;

    private Vector2 gem1 = new Vector2(0, 0);
    private Vector2 gem2 = new Vector2(0, 0);
    private Vector2 swappingGem1 = new Vector2(0, 0);
    private Vector2 swappingGem2 = new Vector2(0, 0);

    private boolean isWrongSwap;
    private boolean isSwap;

    private boolean needShuffle;
    private ArrayList<Chip> chips = new ArrayList<Chip>();

    private int explosionScore;
    private boolean isBang;

    private int[][] exp_1 = {
            {0, 1, 0},
            {1, 1, 1},
            {0, 1, 0}};
    private int[][] exp_2 = {
            {0, 1, 1, 1, 0},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {0, 1, 1, 1, 0}};
    private int[][] exp_3 = {
            {0, 0, 1, 1, 1, 0, 0},
            {0, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 1, 1, 0, 0}};
    private int[][] exp_4 = {
            {0, 0, 1, 1, 1, 1, 1, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 1, 1, 1, 1, 0, 0}};

    Sprite sprite;

    private float tx, ty;
    private boolean isGest;

    private Random random = new Random();

    private float dblClickTime = 0;
    private final float DBL_CLICK_MAX_TIME = 0.5f;
    private int dbcX;
    private int dbcY;
    private boolean isClick = false;

    static Comparator<Chip> chipSortForDraw = new Comparator<Chip>() {
        public int compare(Chip c1, Chip c2) {
            return Integer.compare(c1.layer, c2.layer);
        }
    };

    static Comparator<Chip> chipSort = new Comparator<Chip>() {
        public int compare(Chip c1, Chip c2) {
            int value1 = (int) (c1.cellPos.y * 15 + c1.cellPos.x);
            int value2 = (int) (c2.cellPos.y * 15 + c2.cellPos.x);
            return Integer.compare(value1, value2);
        }
    };



    public GameScreen(Match3 game) {
        Gdx.app.log("GameInfo", "GameScreen: Create");
        this.game = game;
        batch = game.batch;

        IM = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        IM.addProcessor(gd);
        IM.addProcessor(this);

        rgBackground = new TextureRegion(game.tBackground,
                (int)(game.tBackground.getWidth() - game.gameWidth) / 2, 0, (int) game.gameWidth, (int) game.gameHeight);
        rgBackground.flip(false, true);
        rgBounds = new TextureRegion[46];
        for (int i = 0; i < 46; i++) {
            rgBounds[i] = game.aBounds.findRegion(i + "");
            rgBounds[i].flip(false, true);
        }
        rgCellValues = new TextureRegion[13];
        for (int i = 0; i < 13; i++) {
            rgCellValues[i] = game.aTiles.findRegion(i + "");
            rgCellValues[i].flip(false, true);
        }
        rgBack_0 = game.aTiles.findRegion("Back", 0);
        rgBack_0.flip(false, true);
        rgBack_1 = game.aTiles.findRegion("Back", 1);
        rgBack_1.flip(false, true);
        rgBack_2 = game.aTiles.findRegion("Back", 2);
        rgBack_2.flip(false, true);
        rgBox_0 = game.aTiles.findRegion("Box", 0);
        rgBox_0.flip(false, true);
        rgBox_1 = game.aTiles.findRegion("Box", 1);
        rgBox_1.flip(false, true);
        rgChain_0 = game.aTiles.findRegion("Chain", 0);
        rgChain_0.flip(false, true);
        rgChain_1 = game.aTiles.findRegion("Chain", 1);
        rgChain_1.flip(false, true);
        rgDirt_0 = game.aTiles.findRegion("Dirt", 0);
        rgDirt_0.flip(false, true);
        rgDirt_1 = game.aTiles.findRegion("Dirt", 1);
        rgDirt_1.flip(false, true);
        rgGold = game.aTiles.findRegion("Gold");
        rgGold.flip(false, true);
        rgIce_0 = game.aTiles.findRegion("Ice", 0);
        rgIce_0.flip(false, true);
        rgIce_1 = game.aTiles.findRegion("Ice", 1);
        rgIce_1.flip(false, true);

        sprite = new Sprite(rgGold);

        startNewLevel();

        playArea = new Rectangle(160, 0, game.gameWidth - 160, game.gameHeight);
        int w = field.width * CH;
        int h = field.height * CH;
        int x = (int) (playArea.x + (playArea.width - w) / 2);
        int y = (int) (playArea.y + (playArea.height - h) / 2);
        fieldArea = new Rectangle(x, y, w, h);
        chipsZero = new Vector2(fieldArea.x + CH / 2, fieldArea.y + CH / 2);

        changedCells = new Array<Vector2>();
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j ++) {
                if (field.fieldCells[i][j] == 1)
                    changedCells.add(new Vector2(j, i));
            }
    }



    private void startNewLevel() {
        field = new Field(game.fParam);
        field.loadPossibleMatchPattern(game.PMPatterns);
        moveMatrix = new int[field.height][field.width];
        expBlockMatrix = new int[field.height][field.width];
        explosionMatrix = new int[field.height][field.width];

        addAllChips();

        explosionBarValue = 0;
        expMult = 0;
        superBonusValue = 0;
        hintTime = 0f;
        isGem1Selected = false;
        isGem2Selected = false;
        isGest = false;
        swapPhase = 0;
        if (field.level.lType == Mach3Game.LevelType.ltSteps)
            levelSteps = field.level.count;
        else
            levelTime = field.level.count;
    }



    private void setRegionForChip(TextureRegion region, Chip chip) {
        sprite.setRegion(region);
        sprite.setOrigin(region.getRegionWidth() / 2, region.getRegionHeight() / 2);
        sprite.setScale(chip.scale);
        sprite.setAlpha(chip.alpha);
        sprite.setCenter(chipsZero.x + chip.pos.x, chipsZero.y + chip.pos.y);
        //sprite.setPosition(fieldArea.x + chip.pos.x, fieldArea.y + chip.pos.y);
    }


    /*
    private void drawChip(Chip chip) {
        if (chip.cell != null) {
            if (chip.cell.gold) {
                setRegionForChip(rgGold, chip);
                sprite.draw(batch);
                return;
            }
            if (chip.cell.box > 0) {
                if (chip.cell.box == 1) {
                    setRegionForChip(rgBox_0, chip);
                    sprite.draw(batch);
                    return;
                }
                if (chip.cell.box == 2) {
                    setRegionForChip(rgBox_1, chip);
                    sprite.draw(batch);
                    return;
                }
            }
            if (chip.cell.dirt > 0) {
                if (chip.cell.dirt == 1) {
                    setRegionForChip(rgDirt_0, chip);
                    sprite.draw(batch);
                }
                if (chip.cell.dirt == 2) {
                    setRegionForChip(rgDirt_1, chip);
                    sprite.draw(batch);
                }
            }
            if (chip.cell.value > 0) {
                setRegionForChip(rgCellValues[chip.cell.value - 1], chip);
                sprite.draw(batch);
            }
            if (chip.cell.chain > 0) {
                if (chip.cell.chain == 1) {
                    setRegionForChip(rgChain_0, chip);
                    sprite.draw(batch);
                    return;
                }
                if (chip.cell.chain == 2) {
                    setRegionForChip(rgChain_1, chip);
                    sprite.draw(batch);
                    return;
                }
            }
            if (chip.cell.ice > 0) {
                if (chip.cell.ice == 1) {
                    setRegionForChip(rgIce_0, chip);
                    sprite.draw(batch);
                    return;
                }
                if (chip.cell.ice == 2) {
                    setRegionForChip(rgIce_1, chip);
                    sprite.draw(batch);
                    return;
                }
            }
        }
    }
    */
    private void drawChip(Chip chip) {
        Cell cell = field.cells[((int) chip.cellPos.y)][((int) chip.cellPos.x)];
        if (cell != null) {
            if (cell.gold) {
                setRegionForChip(rgGold, chip);
                sprite.draw(batch);
                return;
            }
            if (cell.box > 0) {
                if (cell.box == 1) {
                    setRegionForChip(rgBox_0, chip);
                    sprite.draw(batch);
                    return;
                }
                if (cell.box == 2) {
                    setRegionForChip(rgBox_1, chip);
                    sprite.draw(batch);
                    return;
                }
            }
            if (cell.dirt > 0) {
                if (cell.dirt == 1) {
                    setRegionForChip(rgDirt_0, chip);
                    sprite.draw(batch);
                }
                if (cell.dirt == 2) {
                    setRegionForChip(rgDirt_1, chip);
                    sprite.draw(batch);
                }
            }
            if (cell.value > 0) {
                setRegionForChip(rgCellValues[cell.value - 1], chip);
                sprite.draw(batch);
            }
            if (cell.chain > 0) {
                if (cell.chain == 1) {
                    setRegionForChip(rgChain_0, chip);
                    sprite.draw(batch);
                    return;
                }
                if (cell.chain == 2) {
                    setRegionForChip(rgChain_1, chip);
                    sprite.draw(batch);
                    return;
                }
            }
            if (cell.ice > 0) {
                if (cell.ice == 1) {
                    setRegionForChip(rgIce_0, chip);
                    sprite.draw(batch);
                    return;
                }
                if (cell.ice == 2) {
                    setRegionForChip(rgIce_1, chip);
                    sprite.draw(batch);
                    return;
                }
            }
        }
    }



    private void addChip(Chip chip) {
        chips.add(chip);
    }



    private void addAllChips() {
        chips.clear();
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                if (field.cells[i][j] != null) {
                    if (field.cells[i][j].modif || field.cells[i][j].value > 0) {
                        Chip chip = new Chip(field.cells[i][j]);
                        chip.setCellPos(j, i);
                        chip.setPos(j * CH, i * CH);
                        chip.setNewPos(j * CH, i * CH);
                        addChip(chip);
                        //chips.add(chip);
                    }
                }
            }
    }



    private void updateAllChips() {
        for (Chip chip : chips) {
            int x = (int) chip.cellPos.x;
            int y = (int) chip.cellPos.y;
            chip.cell.copy(field.cells[y][x]);
        }
    }



    private boolean isOnField(int x, int y) {
        return x >= 0 && y >= 0 && x < field.width && y < field.height;
    }



    private boolean isEngaged(Cell cell) {
        if (cell == null)
            return true;
        boolean res = true;
        if (cell.modif)
            res = true;
        else {
            if (cell.value == 0)
                res = false;
            else
                res = true;
        }
        return res;
    }



    private boolean isActive(Cell cell) {
        if (cell == null)
            return false;
        boolean res = false;
        if (cell.modif) {
            if (cell.box > 0 || cell.chain > 0)
                res = false;
            else
                res = true;
        } else {
            if (cell.value == 0)
                res = false;
            else
                res = true;
        }
        return res;
    }



    private boolean isEmpty(Cell cell) {
        if (cell == null)
            return false;
        boolean res = false;
        if (cell.modif == false && cell.value == 0)
            res = true;
        else
            res = false;
        return res;
    }



    private boolean isAllOnPlace() {
        boolean res = true;
        for (Chip chip : chips) {
            if (!chip.isOnPlace) {
                res = false;
                break;
            }
        }
        return res;
    }



    private boolean isEmptySpawnPoints() {
        boolean res = false;
        for (int i = 0; i < field.spawnPoints.size; i++) {
            int x = (int) field.spawnPoints.get(i).x;
            int y = (int) field.spawnPoints.get(i).y;
            Cell cell = field.cells[y][x];
            if (cell != null)
                if (cell.modif == false && cell.value == 0) {
                    res = true;
                    break;
                }
        }
        return res;
    }



    private boolean isSuperBonus(Cell cell) {
        boolean res = false;
        if (cell != null)
            if (cell.modif == false && cell.value == 11)
                res = true;
        return res;
    }



    private boolean isBomb(Cell cell) {
        boolean res = false;
        if (cell != null)
            if (cell.modif == false)
                res = cell.value >= 7 && cell.value <= 10;
        return res;
    }



    private void spawnNewChips() {
        for (int i = 0; i < field.spawnPoints.size; i++) {
            int x = (int) field.spawnPoints.get(i).x;
            int y = (int) field.spawnPoints.get(i).y;
            if (!isEngaged(field.cells[y][x])) {
                field.cells[y][x].setInitial();
                int r = random.nextInt(field.generateColors.length);
                field.cells[y][x].value = field.generateColors[r];
                field.cells[y][x].hold = true;
                Chip chip = new Chip(field.cells[y][x]);
                chip.setCellPos(x, y);
                chip.setPos(x * CH, (y - 1) * CH);
                chip.setNewPos(x * CH, y * CH);
                addChip(chip);
            }
        }
    }



    private void buildMoveMatrix() {
        int value = 0;
        for (int j = 0; j < field.width; j++) {
            int lastValue = 0;
            boolean f = false;
            for (int i = 0; i < field.height; i++) {
                Cell cell = field.cells[i][j];
                if (cell != null) {
                    if (!isEngaged(cell)) {
                        switch (lastValue) {
                            case 2:
                            case 3:
                                value = 3;
                                break;
                            case 0:
                            case 1:
                            case 4:
                            case 5:
                                value = 0;
                                if (isOnField(j - 1, i - 1))
                                    if (isActive(field.cells[i - 1][j - 1]))
                                        value = 5;
                                if (isOnField(j + 1, i - 1))
                                    if (isActive(field.cells[i - 1][j + 1]))
                                        value = 4;
                                break;
                        }
                    } else {
                        if (isActive(cell))
                            value = 2;
                        else
                            value = 1;
                    }
                } else
                    value = 1;
                moveMatrix[i][j] = value;
                lastValue = value;
            }
        }
    }



    private void buildExpBlockMatrix() {
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++)
                expBlockMatrix[i][j] = 0;
        for (int i = 0; i < matches.size; i++)
            for (int j = 0; j < matches.get(i).gems.size; j++) {
                int x = (int) matches.get(i).gems.get(j).x;
                int y = (int) matches.get(i).gems.get(j).y;
                expBlockMatrix[y][x] = 1;
            }
    }



    private Array<Vector2> buildExplosionMatrix(int x, int y) {
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++)
                explosionMatrix[i][j] = 0;

        explode(x, y);

        Array<Vector2> res = new Array<Vector2>();
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                if (explosionMatrix[i][j] == 1) {
                    Vector2 v = new Vector2(j, i);
                    res.add(v);
                }
            }

        return res;
    }



    private void explode(int x, int y) {
        if (!isOnField(x, y))
            return;
        if (field.cells[y][x] == null)
            return;
        if (expBlockMatrix[y][x] == 1 || explosionMatrix[y][x] == 1)
            return;

        explosionMatrix[y][x] = 1;
        Cell cell = field.cells[y][x];
        if (isBomb(cell)) {
            expMult++;
            switch (cell.value) {
                case 7:
                    explosionScore = explosionScore + bonus1Score;
                    for (int i = 0; i < 3; i++)
                        for (int j = 0; j < 3; j++) {
                            if (exp_1[i][j] == 1) {
                                int ex = x + j - 1;
                                int ey = y + i - 1;
                                explode(ex, ey);
                            }
                        }
                    break;
                case 8:
                    explosionScore = explosionScore + bonus2Score;
                    for (int i = 0; i < 5; i++)
                        for (int j = 0; j < 5; j++) {
                            if (exp_2[i][j] == 1) {
                                int ex = x + j - 2;
                                int ey = y + i - 2;
                                explode(ex, ey);
                            }
                        }
                    break;
                case 9:
                    explosionScore = explosionScore + bonus3Score;
                    for (int i = 0; i < 7; i++)
                        for (int j = 0; j < 7; j++) {
                            if (exp_3[i][j] == 1) {
                                int ex = x + j - 3;
                                int ey = y + i - 3;
                                explode(ex, ey);
                            }
                        }
                    break;
                case 10:
                    explosionScore = explosionScore + bonus4Score;
                    for (int i = 0; i < 9; i++)
                        for (int j = 0; j < 9; j++) {
                            if (exp_4[i][j] == 1) {
                                int ex = x + j - 4;
                                int ey = y + i - 4;
                                explode(ex, ey);
                            }
                        }
                    break;
            }
        }
    }



    private Chip getChip(int x, int y) {
        Chip chip = null;
        boolean f = false;
        for (int i = 0; i < chips.size(); i++) {
            chip = chips.get(i);
            if (chip.cellPos.x == x && chip.cellPos.y == y) {
                f = true;
                break;
            }
        }
        if (f)
            return chip;
        else
            return null;
    }



    private Array<Vector2> getChipOneColor(int color) {
        Array<Vector2> res = new Array<Vector2>();
        if (!((color >= 1 && color <= 6) || color == 12))
            return res;
        for (Chip chip : chips) {
            Cell cell = field.cells[((int) chip.cellPos.y)][((int) chip.cellPos.x)];
            if (cell != null) {
                boolean f = true;
                if (cell.modif) {
                    if (cell.gold || cell.box > 0 || cell.dirt > 0)
                        f = false;
                }
                if (f && cell.value == color) {
                    Vector2 v = new Vector2(chip.cellPos);
                    res.add(v);
                }
            }
        }
        return res;
    }



    private void removeChip(int x, int y) {
        Chip chip = null;
        boolean f = false;
        for (int i = 0; i < chips.size(); i++) {
            chip = chips.get(i);
            if (chip.cellPos.x == x && chip.cellPos.y == y) {
                f = true;
                break;
            }
        }
        if (f)
            chips.remove(chip);
    }



    private void removeMatches(boolean randomPos) {
        if (matches.size < 1)
            return;

        int[][] m = new int[field.height][field.width];
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++)
                m[i][j] = 0;

        for (int i = 0; i < matches.size; i++)
            for (int j = 0; j < matches.get(i).gems.size; j++) {
                int x = (int) matches.get(i).gems.get(j).x;
                int y = (int) matches.get(i).gems.get(j).y;
                if (!field.cells[y][x].modif) {
                    if (isOnField(x - 1, y))
                        if (m[y][x - 1] != 1)
                            m[y][x - 1] = 2;
                    if (isOnField(x + 1, y))
                        if (m[y][x + 1] != 1)
                            m[y][x + 1] = 2;
                    if (isOnField(x, y - 1))
                        if (m[y - 1][x] != 1)
                            m[y - 1][x] = 2;
                    if (isOnField(x, y + 1))
                        if (m[y + 1][x] != 1)
                            m[y + 1][x] = 2;
                }
                m[y][x] = 1;
            }

        Array<Vector2> arrV = new Array<Vector2>();
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                if (m[i][j] == 2) {
                    Vector2 v = new Vector2(j, i);
                    arrV.add(v);
                }
            }

        for (int i = 0; i < matches.size; i++) {
            int len = matches.get(i).gems.size;
            int count = len;

            if (count > 7)
                count = 7;

            int bonus = 0;
            switch (count) {
                case 4:
                    bonus = 7;
                    break;
                case 5:
                    bonus = 8;
                    break;
                case 6:
                    bonus = 9;
                    break;
                case 7:
                    bonus = 10;
                    break;
            }

            int bx = 0;
            int by = 0;
            if (count > 3) {
                if (randomPos) {
                    do {
                        int r = random.nextInt(len);
                        bx = (int) matches.get(i).gems.get(r).x;
                        by = (int) matches.get(i).gems.get(r).y;
                    } while (field.cells[by][bx].modif);
                } else {
                    bx = (int) matches.get(i).pos.x;
                    by = (int) matches.get(i).pos.y;
                }
            }

            for (int j = 0; j < len; j++) {
                int x = (int) matches.get(i).gems.get(j).x;
                int y = (int) matches.get(i).gems.get(j).y;
                if (field.cells[y][x].modif) {
                    if (field.cells[y][x].chain > 0) {
                        field.cells[y][x].chain--;
                        if (field.cells[y][x].chain == 0)
                            field.cells[y][x].modif = false;
                    } else {
                        if (field.cells[y][x].ice > 0) {
                            field.cells[y][x].ice--;
                            if (field.cells[y][x].ice == 0)
                                field.cells[y][x].modif = false;
                        }
                    }
                } else {
                    field.cells[y][x].setInitial();
                    removeChip(x, y);
                    if (field.backCells[y][x] > 0)
                        field.backCells[y][x]--;
                }
            }

            if (count > 3) {
                field.cells[by][bx].value = bonus;
                Chip chip = new Chip(field.cells[by][bx]);
                chip.setCellPos(bx, by);
                chip.setPos(bx * CH, by * CH);
                chip.setNewPos(bx * CH, by * CH);
                addChip(chip);
            }
        }

        for (int i = 0; i < arrV.size; i++) {
            int x = (int) arrV.get(i).x;
            int y = (int) arrV.get(i).y;
            if (field.cells[y][x] != null) {
                if (field.cells[y][x].modif) {
                    if (field.cells[y][x].gold) {
                        field.cells[y][x].setInitial();
                        removeChip(x, y);
                    } else {
                        if (field.cells[y][x].box > 0) {
                            field.cells[y][x].box--;
                            if (field.cells[y][x].box == 0) {
                                if (field.cells[y][x].value == 0) {
                                    field.cells[y][x].setInitial();
                                    removeChip(x, y);
                                } else
                                    if (field.cells[y][x].ice == 0)
                                        field.cells[y][x].modif = false;
                            }
                            //Chip chip = getChip(x, y);
                            //if (chip != null)
                              //  chip.cell.copy();
                        } else {
                            if (field.cells[y][x].dirt > 0) {
                                field.cells[y][x].dirt--;
                                if (field.cells[y][x].dirt == 0) {
                                    if (field.cells[y][x].value == 0) {
                                        field.cells[y][x].setInitial();
                                        removeChip(x, y);
                                    } else
                                        field.cells[y][x].modif = false;
                                }
                            }
                        }
                    }
                } else {
                    if (field.cells[y][x].value == 12) {
                        field.cells[y][x].setInitial();
                        removeChip(x, y);
                    }
                }
            }
        }
    }



    private void removeExpCells(Array<Vector2> gems) {
        for (int i = 0; i < gems.size; i++) {
            int x = (int) gems.get(i).x;
            int y = (int) gems.get(i).y;
            Cell cell = field.cells[y][x];
            if (cell != null) {
                if (cell.modif) {
                    if (cell.gold) {
                        cell.setInitial();
                        removeChip(x, y);
                    } else {
                        if (cell.box > 0) {
                            cell.box--;
                            if (cell.box == 0) {
                                if (cell.value == 0) {
                                    cell.setInitial();
                                    removeChip(x, y);
                                } else if (cell.ice == 0)
                                    cell.modif = false;
                            }
                        }else {
                            if (cell.dirt > 0) {
                                cell.dirt--;
                                if (cell.dirt == 0) {
                                    if (cell.value == 0) {
                                        cell.setInitial();
                                        removeChip(x, y);
                                    } else
                                        cell.modif = false;
                                }
                            } else {
                                if (cell.chain > 0) {
                                    cell.chain--;
                                    if (cell.chain == 0)
                                        cell.modif = false;
                                } else {
                                    if (cell.ice > 0) {
                                        cell.ice--;
                                        if (cell.ice == 0)
                                            cell.modif = false;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (cell.value == 0) {
                        if (field.backCells[y][x] > 0)
                            field.backCells[y][x]--;
                    } else
                        if (!(cell.value == 11 || cell.value == 13)) {
                            cell.setInitial();
                            if (field.backCells[y][x] > 0)
                                field.backCells[y][x]--;
                            removeChip(x, y);
                        }
                }
            }
        }
    }



    private void removeColorChips(Array<Vector2> gems) {
        int[][] m = new int[field.height][field.width];
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++)
                m[i][j] = 0;

        for (int i = 0; i < gems.size; i++) {
            int x = (int) gems.get(i).x;
            int y = (int) gems.get(i).y;
            if (!field.cells[y][x].modif) {
                if (isOnField(x - 1, y))
                    if (m[y][x - 1] != 1)
                        m[y][x - 1] = 2;
                if (isOnField(x + 1, y))
                    if (m[y][x + 1] != 1)
                        m[y][x + 1] = 2;
                if (isOnField(x, y - 1))
                    if (m[y - 1][x] != 1)
                        m[y - 1][x] = 2;
                if (isOnField(x, y + 1))
                    if (m[y + 1][x] != 1)
                        m[y + 1][x] = 2;
            }
            m[y][x] = 1;
        }

        Array<Vector2> arrV = new Array<Vector2>();
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                if (m[i][j] == 2) {
                    Vector2 v = new Vector2(j, i);
                    arrV.add(v);
                }
            }

        for (int i = 0; i < gems.size; i++) {
            int x = (int) gems.get(i).x;
            int y = (int) gems.get(i).y;
            if (field.cells[y][x].modif) {
                if (field.cells[y][x].chain > 0) {
                    field.cells[y][x].chain--;
                    if (field.cells[y][x].chain == 0)
                        field.cells[y][x].modif = false;
                } else {
                    if (field.cells[y][x].ice > 0) {
                        field.cells[y][x].ice--;
                        if (field.cells[y][x].ice == 0)
                            field.cells[y][x].modif = false;
                    }
                }
            } else {
                field.cells[y][x].setInitial();
                removeChip(x, y);
                if (field.backCells[y][x] > 0)
                    field.backCells[y][x]--;
            }
        }

        for (int i = 0; i < arrV.size; i++) {
            int x = (int) arrV.get(i).x;
            int y = (int) arrV.get(i).y;
            if (field.cells[y][x] != null) {
                if (field.cells[y][x].modif) {
                    if (field.cells[y][x].gold) {
                        field.cells[y][x].setInitial();
                        removeChip(x, y);
                    } else {
                        if (field.cells[y][x].box > 0) {
                            field.cells[y][x].box--;
                            if (field.cells[y][x].box == 0) {
                                if (field.cells[y][x].value == 0) {
                                    field.cells[y][x].setInitial();
                                    removeChip(x, y);
                                } else
                                if (field.cells[y][x].ice == 0)
                                    field.cells[y][x].modif = false;
                            }
                        } else {
                            if (field.cells[y][x].dirt > 0) {
                                field.cells[y][x].dirt--;
                                if (field.cells[y][x].dirt == 0) {
                                    if (field.cells[y][x].value == 0) {
                                        field.cells[y][x].setInitial();
                                        removeChip(x, y);
                                    } else
                                        field.cells[y][x].modif = false;
                                }
                            }
                        }
                    }
                } else {
                    if (field.cells[y][x].value == 12) {
                        field.cells[y][x].setInitial();
                        removeChip(x, y);
                    }
                }
            }
        }
    }



    private Array<Vector2> explodeWholeField() {
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                explosionMatrix[i][j] = 0;
                expBlockMatrix[i][j] = 0;
            }

        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                explode(j, i);
            }

        Array<Vector2> res = new Array<Vector2>();
        for (int i = 0; i < field.height; i++)
            for (int j = 0; j < field.width; j++) {
                if (explosionMatrix[i][j] == 1) {
                    Vector2 v = new Vector2(j, i);
                    res.add(v);
                }
            }

        return res;
    }



    private void proc() {
        for (Chip chip : chips) {
            int x = (int) chip.cellPos.x;
            int y = (int) chip.cellPos.y;
            Cell cell = field.cells[y][x];

            if (cell.inSwap)
                continue;

            if (chip.isOnPlace && isActive(cell)) {
                if (isOnField(x, y + 1)) {
                    if (!isEngaged(field.cells[y + 1][x])) {
                        chip.setNewPos(x * CH, (y + 1) * CH);
                        field.cells[y + 1][x].copy(cell);
                        cell.setInitial();
                        field.cells[y + 1][x].hold = true;
                        chip.setCellPos(x, y + 1);
                        chip.cell.copy(field.cells[y + 1][x]);
                        if (isOnField(x, y - 1)) {
                            if (isActive(field.cells[y - 1][x]) || isEmpty(field.cells[y - 1][x]))
                                moveMatrix[y][x] = 3;
                            else
                                moveMatrix[y][x] = 0;
                        }
                        continue;
                    }
                }

                if (isOnField(x - 1, y + 1)) {
                    if (!isEngaged(field.cells[y + 1][x - 1]) && (moveMatrix[y + 1][x - 1] == 0 || moveMatrix[y + 1][x - 1] == 4)) {
                        chip.setNewPos((x - 1) * CH, (y + 1) * CH);
                        field.cells[y + 1][x - 1].copy(cell);
                        cell.setInitial();
                        field.cells[y + 1][x - 1].hold = true;
                        chip.setCellPos(x - 1, y + 1);
                        chip.cell.copy(field.cells[y + 1][x - 1]);
                        if (isOnField(x, y - 1)) {
                            if (isActive(field.cells[y - 1][x]))
                                moveMatrix[y][x] = 3;
                            else
                                moveMatrix[y][x] = 0;
                        }
                        continue;
                    }
                }

                if (isOnField(x + 1, y + 1)) {
                    if (!isEngaged(field.cells[y + 1][x + 1]) && (moveMatrix[y + 1][x + 1] == 0 || moveMatrix[y + 1][x + 1] == 5)) {
                        chip.setNewPos((x + 1) * CH, (y + 1) * CH);
                        field.cells[y + 1][x + 1].copy(cell);
                        cell.setInitial();
                        field.cells[y + 1][x + 1].hold = true;
                        chip.setCellPos(x + 1, y + 1);
                        chip.cell.copy(field.cells[y + 1][x + 1]);
                        if (isOnField(x, y - 1)) {
                            if (isActive(field.cells[y - 1][x]))
                                moveMatrix[y][x] = 3;
                            else
                                moveMatrix[y][x] = 0;
                        }
                        continue;
                    }
                }
            }
        }
    }



    public void update(float delta) {
        spawnNewChips();
        buildMoveMatrix();

        for (Chip chip : chips) {
            int x = (int) chip.cellPos.x;
            int y = (int) chip.cellPos.y;
            Cell cell = field.cells[y][x];
            chip.move();
            if (chip.isOnPlace)
                cell.hold = false;
        }

        Collections.sort(chips, chipSort);

        // задать номер слоя отрисовки
        for (Chip chip : chips) {
            if (chip.layer != 2 && chip.layer != 3) {
                if (chip.isOnPlace)
                    chip.layer = 0;
                else
                    chip.layer = 1;
            }
        }

        // if isSwap
        if (isSwap) {
            int sg1x = (int) swappingGem1.x;
            int sg1y = (int) swappingGem1.y;
            int sg2x = (int) swappingGem2.x;
            int sg2y = (int) swappingGem2.y;
            Chip chip1 = getChip(sg1x, sg1y);
            Chip chip2 = getChip(sg2x, sg2y);

            boolean f1 = false;
            if (chip1 != null) {
                Cell cell = field.cells[((int) chip1.cellPos.y)][((int) chip1.cellPos.x)];
                if (chip1.isOnPlace && cell.inSwap) {
                    cell.inSwap = false;
                    f1 = true;
                }
            } else
                f1 = true;

            boolean f2 = false;
            if (chip1 != null) {
                Cell cell = field.cells[((int) chip2.cellPos.y)][((int) chip2.cellPos.x)];
                if (chip2.isOnPlace && cell.inSwap) {
                    cell.inSwap = false;
                    f2 = true;
                }
            } else
                f2 = true;

            if (f1 || f2) {
                if (!isWrongSwap) {
                    isSwap = false;
                    Array<Vector2> sGems = new Array<Vector2>();
                    sGems.add(swappingGem1);
                    sGems.add(swappingGem2);

                    if (superBonusValue == 0) {
                        matches = field.findMatches(sGems);
                        buildExpBlockMatrix();
                        removeMatches(false);
                    }

                    if (isBang) {
                        f1 = isBomb(field.cells[sg1y][sg1x]) && expBlockMatrix[sg1y][sg1x] == 0;
                        f2 = isBomb(field.cells[sg2y][sg2x]) && expBlockMatrix[sg2y][sg2x] == 0;
                        int bx = 0;
                        int by = 0;
                        if (f1) {
                            bx = sg1x;
                            by = sg1y;
                        } else
                            if (f2) {
                                bx = sg2x;
                                by = sg2y;
                            }
                        if (f1 || f2) {
                            expMult = 0;
                            explosionScore = 0;
                            Array<Vector2> arrV = buildExplosionMatrix(bx, by);
                            removeExpCells(arrV);
                            explosionBarValue = explosionBarValue + explosionScore * expMult;

                            if (explosionBarValue >= EXPLOSION_BAR_MAX_VALUE) {
                                // Появление супер-бонуса
                                //...
                            }

                            isBang = false;
                        }
                    }

                    if (superBonusValue > 0) {
                        if (superBonusValue == 1) {
                            Cell cell1 = field.cells[sg1y][sg1x];
                            Cell cell2 = field.cells[sg2y][sg2x];
                            int color = 0;
                            int sx = 0;
                            int sy = 0;
                            if (isSuperBonus(cell1)) {
                                sx = sg1x;
                                sy = sg1y;
                                color = cell2.value;
                            } else {
                                sx = sg2x;
                                sy = sg2y;
                                color = cell1.value;
                            }
                            Array<Vector2> arrV = getChipOneColor(color);
                            removeColorChips(arrV);
                            removeChip(sx, sy);
                            field.cells[sy][sx].setInitial();
                            if (field.backCells[sy][sx] > 0)
                                field.backCells[sy][sx]--;
                            superBonusValue = 0;
                        }

                        if (superBonusValue == 2) {
                            expMult = 0;
                            explosionScore = 0;
                            Array<Vector2> arrV = explodeWholeField();
                            removeExpCells(arrV);
                            explosionBarValue = explosionBarValue + explosionScore * expMult;
                            removeChip(sg1x, sg1y);
                            field.cells[sg1y][sg1x].setInitial();
                            removeChip(sg2x, sg2y);
                            field.cells[sg2y][sg2x].setInitial();
                            if (field.backCells[sg1y][sg1x] > 0)
                                field.backCells[sg1y][sg1x]--;
                            if (field.backCells[sg2y][sg2x] > 0)
                                field.backCells[sg2y][sg2x]--;
                            superBonusValue = 0;
                        }
                    }

                    if (chip1 != null)
                        chip1.layer = 0;
                    if (chip2 != null)
                        chip2.layer = 0;

                    levelSteps--;
                } else {
                    if (swapPhase == 1) {
                        isSwap = false;
                        swapPhase = 0;
                        if (chip1 != null)
                            chip1.layer = 0;
                        if (chip2 != null)
                            chip2.layer = 0;
                    } else {
                        swapPhase++;
                        field.cells[sg1y][sg1x].inSwap = true;
                        field.cells[sg2y][sg2x].inSwap = true;
                        chip1.setNewPos(sg1x * CH, sg1y * CH);
                        chip2.setNewPos(sg2x * CH, sg2y * CH);
                        field.cells[sg1y][sg1x].hold = true;
                        field.cells[sg2y][sg2x].hold = true;
                        chip1.layer = 2;
                        chip2.layer = 3;
                    }
                }
            }
        }

        proc();

        if (superBonusValue == 0) {
            matches = field.findMatches(changedCells);
            removeMatches(true);
        }

        for (Chip chip : chips) {
            int x = (int) chip.cellPos.x;
            int y = (int) chip.cellPos.y;
            Cell cell = field.cells[y][x];
            if (chip.isOnPlace && cell.inSwap)
                cell.inSwap = false;
        }

        possibleMatches = field.findPossibleMatches();
        if (isAllOnPlace() && !isEmptySpawnPoints()) {
            while (possibleMatches.size == 0) {
                field.shuffle();
                possibleMatches = field.findPossibleMatches();
            }
            hintTime = hintTime + delta;
            if (hintTime > MAX_HINT_TIME) {
                if (hintPossibleMatch == false) {
                    int possibleMatchNumber = random.nextInt(possibleMatches.size);
                    possibleMatch = possibleMatches.get(possibleMatchNumber);
                }
                hintTime = MAX_HINT_TIME;
                hintPossibleMatch = true;
            }
        } else {
            hintTime = 0;
            hintPossibleMatch = false;
        }

        //swap
        if (isGem1Selected && isGem2Selected) {
            isSwap = true;
            swapPhase = 0;
            isWrongSwap = true;
            int gem1X = (int) gem1.x;
            int gem1Y = (int) gem1.y;
            int gem2X = (int) gem2.x;
            int gem2Y = (int) gem2.y;
            for (int i = 0; i < possibleMatches.size; i++) {
                int v1X = possibleMatches.get(i).x;
                int v1Y = possibleMatches.get(i).y;
                PossibleMatchPattern pmPattern = field.getPossibleMatchPattern(possibleMatches.get(i).patternIndex);
                int v2X = (int) (v1X + pmPattern.napr.x);
                int v2Y = (int) (v1Y + pmPattern.napr.y);
                if ((gem1X == v1X && gem1Y == v1Y && gem2X == v2X && gem2Y == v2Y) ||
                        (gem1X == v2X && gem1Y == v2Y && gem2X == v1X && gem2Y == v1Y)) {
                    isWrongSwap = false;
                    break;
                }
            }

            Chip chip1 = getChip(gem1X, gem1Y);
            Chip chip2 = getChip(gem2X, gem2Y);
            Cell cell1 = field.cells[gem1Y][gem1X];
            Cell cell2 = field.cells[gem2Y][gem2X];

            if (chip2 != null) {
                // проверка на супер-бонус
                if (isSuperBonus(cell1) && !isBomb(cell2)) {
                    superBonusValue = 1;
                    isWrongSwap = false;
                    if (isSuperBonus(cell2))
                        superBonusValue = 2;
                } else {
                    if (isSuperBonus(cell2) && !isBomb(cell1)) {
                        superBonusValue = 1;
                        isWrongSwap = false;
                        if (isSuperBonus(cell1))
                            superBonusValue = 2;
                    }
                }

                // проверка на бомбу
                if (isBomb(cell1)) {
                    isWrongSwap = false;
                    isBang = true;
                }
                if (isBomb(cell2)) {
                    isWrongSwap = false;
                    isBang = true;
                }
            }

            if (!isWrongSwap) {
                field.cells[gem1Y][gem1X].inSwap = true;
                chip1 = getChip(gem1X, gem1Y);
                chip2 = getChip(gem2X, gem2Y);
                swappingGem1.set(gem1);
                swappingGem2.set(gem2);
                field.swap(gem1, gem2);
                chip1.setNewPos(gem2X * CH, gem2Y * CH);
                chip1.setCellPos(gem2X, gem2Y);
                field.cells[gem2Y][gem2X].hold = true;
                chip1.layer = 3;
                chip1.cell.copy(field.cells[gem2Y][gem2X]);
                if (!isEmpty(field.cells[gem1Y][gem1X])) {
                    chip2.setNewPos(gem1X * CH, gem1Y * CH);
                    chip2.setCellPos(gem1X, gem1Y);
                    field.cells[gem1Y][gem1X].hold = true;
                    field.cells[gem1Y][gem1X].inSwap = true;
                    chip2.layer = 2;
                    chip2.cell.copy(field.cells[gem1Y][gem1X]);
                }
                isGem1Selected = false;
                isGem2Selected = false;
            } else {
                if (!isEmpty(field.cells[gem2Y][gem2X])) {
                    chip1 = getChip(gem1X, gem1Y);
                    chip2 = getChip(gem2X, gem2Y);
                    field.cells[gem1Y][gem1X].inSwap = true;
                    field.cells[gem2Y][gem2X].inSwap = true;
                    swappingGem1.set(gem1);
                    swappingGem2.set(gem2);
                    chip1.setNewPos(gem2X * CH, gem2Y * CH);
                    chip2.setNewPos(gem1X * CH, gem1Y * CH);
                    field.cells[gem1Y][gem1X].hold = true;
                    field.cells[gem2Y][gem2X].hold = true;
                    chip1.layer = 3;
                    chip2.layer = 2;
                } else
                    isSwap = false;
                isGem1Selected = false;
                isGem2Selected = false;
            }
        }

        levelTime = levelTime - delta;
        dblClickTime = dblClickTime + delta;
        if (dblClickTime > DBL_CLICK_MAX_TIME)
            isClick = false;
    }



    //region Screen_Region
    @Override
    public void show() {
        Gdx.app.log("GameInfo", "GameScreen: Show");
        Gdx.input.setInputProcessor(IM);
        //Gdx.input.setCatchBackKey(true);
    }



    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        {
            batch.draw(rgBackground, 0, 0);

            // Подложка
            for (int i = 0; i < field.height; i++)
                for (int j = 0; j < field.width; j ++) {
                    if (field.fieldCells[i][j] == 1)
                        batch.draw(rgBack_0, fieldArea.x + j * CH, fieldArea.y + i * CH);
                }

            // Грязь
            for (int i = 0; i < field.height; i++)
                for (int j = 0; j < field.width; j ++) {
                    switch (field.backCells[i][j]) {
                        case 1:
                            batch.draw(rgBack_1, fieldArea.x + j * CH, fieldArea.y + i * CH);
                            break;
                        case 2:
                            batch.draw(rgBack_2, fieldArea.x + j * CH, fieldArea.y + i * CH);
                            break;
                    }
                }

            // Границы
            for (int i = 0; i < field.height + 2; i++)
                for (int j = 0; j < field.width + 2; j ++) {
                    int tileNumber = field.bounds[i][j];
                    if (tileNumber > 0)
                        batch.draw(rgBounds[tileNumber - 1], fieldArea.x + j * CH - CH, fieldArea.y + i * CH - CH);
                }

            // Фишки
            Collections.sort(chips, chipSortForDraw);
            for (Chip chip : chips) {
                drawChip(chip);
            }

            game.font.draw(batch, chips.size() + "", 30, 20);
            game.font.draw(batch, "PM: " + possibleMatches.size, 30, 65);
            game.font.draw(batch, "M: " + matches.size, 30, 110);
            game.font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), game.gameWidth - 190, 20);

            if (field.level.lType == Mach3Game.LevelType.ltSteps) {
                game.font.draw(batch, "" + levelSteps, 30, 155);
            } else {
                int val = (int) levelTime;
                game.font.draw(batch, val / 60 + ":" + val % 60, 30, 155);
            }
        }
        batch.end();
    }



    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
    //endregion



    //region InputProcessor_Region
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    //endregion



    //region GestureListener_Region
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (fieldArea.contains(x * game.scaleCoef, y * game.scaleCoef)) {
            tx = 0;
            ty = 0;
            isGest = false;
            int fx = (int) ((x * game.scaleCoef - fieldArea.x) / CH);
            int fy = (int) ((y * game.scaleCoef - fieldArea.y) / CH);

            if (isOnField(fx, fy)) {

                // dblClick
                if (!isClick) {
                    isClick = true;
                    dbcX = fx;
                    dbcY = fy;
                    dblClickTime = 0f;
                } else {
                    isClick = false;
                    if (dbcX == fx && dbcY == fy) {
                        Chip chip = getChip(dbcX, dbcY);
                        if (chip != null) {
                            Cell cell = field.cells[((int) chip.cellPos.y)][((int) chip.cellPos.x)];
                            if (cell.modif == false && cell.value >= 7 && cell.value <= 10) {
                                for (int i = 0; i < field.height; i++)
                                    for (int j = 0; j < field.width; j++)
                                        expBlockMatrix[i][j] = 0;
                                expMult = 0;
                                explosionScore = 0;
                                Array<Vector2> arrV = buildExplosionMatrix(dbcX, dbcY);
                                removeExpCells(arrV);

                                explosionBarValue = explosionBarValue + explosionScore * expMult;
                                if (explosionBarValue >= EXPLOSION_BAR_MAX_VALUE) {
                                    //...
                                }

                                isGem1Selected = false;
                                isGem2Selected = false;

                                levelSteps--;
                            }
                        }
                    }
                }
                //

                // swap
                if (isGem1Selected) {
                    if (field.cells[fy][fx] != null) {
                        if (!field.cells[fy][fx].modif) {
                            if ((Math.abs(gem1.x - fx) == 1 && Math.abs(gem1.y - fy) == 0) ||
                                    (Math.abs(gem1.x - fx) == 0 && Math.abs(gem1.y - fy) == 1)) {
                                gem2.x = fx;
                                gem2.y = fy;
                                isGem2Selected = true;
                            } else
                                isGem1Selected = false;
                        }
                    }
                }

                if (!isGem1Selected) {
                    if (field.cells[fy][fx] != null) {
                        if (field.cells[fy][fx].modif == false && field.cells[fy][fx].value > 0) {
                            gem1.x = fx;
                            gem1.y = fy;
                            isGem1Selected = true;
                            isGem2Selected = false;
                        }
                    }
                }
                //
            }
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        if (fieldArea.contains(x * game.scaleCoef, y * game.scaleCoef)) {
            tx = x;
            ty = y;
            int fx = (int) ((x * game.scaleCoef - fieldArea.x) / CH);
            int fy = (int) ((y * game.scaleCoef - fieldArea.y) / CH);

            if (isOnField(fx, fy)) {
                field.cells[fy][fx].setInitial();
                field.cells[fy][fx].value = 8;
                Chip chip = getChip(fx, fy);
                if (chip != null)
                    chip.cell.copy(field.cells[fy][fx]);
            }
        }
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (!isGest) {
            tx += deltaX;
            ty += deltaY;
            //Gdx.app.log("GameInfo", tx + ", " + ty);
            int r = 30;
            if (Math.abs(tx) > r || Math.abs(ty) > r) {
                isGest = true;
                int dx = 0;
                int dy = 0;
                if (Math.abs(tx) > r) {
                    if (tx > r)
                        dx = 1;
                    else
                        dx = -1;
                } else {
                    if (Math.abs(ty) > r) {
                        if (ty > r)
                            dy = 1;
                        else
                            dy = -1;
                    }
                }
                if (isGem1Selected) {
                    int fx = (int) (gem1.x + dx);
                    int fy = (int) (gem1.y + dy);
                    if (isOnField(fx, fy)) {
                        if (field.cells[fy][fx] != null) {
                            if (!field.cells[fy][fx].modif) {
                                if ((Math.abs(gem1.x - fx) == 1 && Math.abs(gem1.y - fy) == 0) ||
                                        (Math.abs(gem1.x - fx) == 0 && Math.abs(gem1.y - fy) == 1)) {
                                    gem2.x = fx;
                                    gem2.y = fy;
                                    isGem2Selected = true;
                                } else
                                    isGem1Selected = false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
    //endregion
}