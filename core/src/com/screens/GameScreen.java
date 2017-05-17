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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    private Vector2 gem1;
    private Vector2 gem2;
    private Vector2 swappingGem1;
    private Vector2 swappingGem2;

    private boolean isWrongSwap;
    private boolean isSwap;

    private boolean needShuffle;
    private ArrayList<Chip> chips = new ArrayList<Chip>();

    private int explosionScore;
    private boolean isBang;

    private int[][] exp_1 = {
            {0, 1, 0},
            {0, 1, 0},
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

    private TextureRegion getRegionForCell(Cell cell) {
        if (cell != null) {
            if (cell.gold) {
                return rgGold;
            }
            if (cell.box > 0) {
                if (cell.box == 1)
                    return rgBox_0;
                if (cell.box == 2)
                    return rgBox_1;
            }

        }
        return null;
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

    public void update(float delta) {

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
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
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
        // test
        {
            if (fieldArea.contains(screenX * game.scaleCoef, screenY * game.scaleCoef)) {
                matches = field.findMatches(changedCells);
                int fx = (int) ((screenX * game.scaleCoef - fieldArea.x) / CH);
                int fy = (int) ((screenY * game.scaleCoef - fieldArea.y) / CH);
                Gdx.app.log("GameInfo", "in the area (" + fx + ", " + fy + ")");
                /*
                if (fx >= 0 && fx < field.width && fy >= 0 && fy < field.height) {
                    Array<Vector2> arrV = new Array<Vector2>();
                    Vector2 v = new Vector2(fx, fy);
                    arrV.add(v);
                    matches = field.findMatches(arrV);
                }
                */
            } else {
                //field.shuffle();
                field.generateRandom();
                updateAllChips();
                //possibleMatches = field.findPossibleMatches();
            }
        }
        //
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
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
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
