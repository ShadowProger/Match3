package com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.match3.Match3;
import com.match3.gamelogic.Cell;
import com.match3.gamelogic.Field;
import com.match3.gamelogic.FieldParam;
import com.match3.gamelogic.Level;
import com.match3.gamelogic.Mach3Game;
import com.match3.gamelogic.Mission;

import java.io.DataInputStream;
import java.io.IOException;

public class MapScreen implements Screen, GestureDetector.GestureListener, InputProcessor {
    final Match3 game;
    private SpriteBatch batch;
    private InputMultiplexer IM;

    private TextureRegion rgFullMap;
    private TextureRegion rgPieceOfMap;
    private TextureRegion rgCompletedLevel;
    private TextureRegion rgCurrentLevel;
    private TextureRegion rgLockedLevel;
    private TextureRegion rgStartWindow;
    private TextureRegion rgCloseWindow;
    private TextureRegion rgWhiteIcon;
    private TextureRegion rgRedIcon;
    private TextureRegion rgGreenIcon;
    private TextureRegion rgBlueIcon;
    private TextureRegion rgYellowIcon;
    private TextureRegion rgPinkIcon;
    private TextureRegion rgItemIcon;
    private TextureRegion rgGoldIcon;
    private TextureRegion rgBackIcon;

    private GlyphLayout layout;

    private float dispX;
    private float speedX;
    private float dSpeedX;
    private boolean isSwap;
    private boolean isPan;

    private final int RECT_WIDTH = 140;
    private Array<Rectangle> levelsRect = new Array<Rectangle>();

    private int selectedLevel = 0;

    private Vector2 closeWindowPos;
    private Vector2 startWindowPos;

    private Vector2 iconPos1;
    private Vector2 iconPos2;
    private Vector2 iconPos3;
    private Vector2 levelPos;

    private Rectangle btnYes;
    private Rectangle btnNo;
    private Rectangle btnClose;
    private Rectangle btnPlay;

    private enum Mode {mLevel, mClose, mStart};
    Mode gameMode;

    //private Field field;

    public MapScreen(Match3 game) {
        Gdx.app.log("GameInfo", "MapScreen: Create");
        this.game = game;
        batch = game.batch;
        layout = new GlyphLayout();

        IM = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        IM.addProcessor(gd);
        IM.addProcessor(this);

        //tMap = new Texture(Gdx.files.internal("Data\\Pictures\\Map2.png"));
        rgFullMap = new TextureRegion(game.tMap, 0, 0, game.tMap.getWidth(), game.tMap.getHeight());
        rgFullMap.flip(false, true);
        rgPieceOfMap = new TextureRegion(game.tMap, 0, 0, (int) game.gameWidth, (int) game.gameHeight);
        rgPieceOfMap.flip(false, true);

        rgCompletedLevel = game.aAtlas.findRegion("CompletedLevel");
        rgCompletedLevel.flip(false, true);
        rgCurrentLevel = game.aAtlas.findRegion("CurrentLevel");
        rgCurrentLevel.flip(false, true);
        rgLockedLevel = game.aAtlas.findRegion("LockedLevel");
        rgLockedLevel.flip(false, true);
        rgStartWindow = game.aAtlas.findRegion("StartWindow");
        rgStartWindow.flip(false, true);
        rgCloseWindow = game.aAtlas.findRegion("CloseWindow_75p");
        rgCloseWindow.flip(false, true);
        rgWhiteIcon = game.aAtlas.findRegion("White");
        rgWhiteIcon.flip(false, true);
        rgRedIcon = game.aAtlas.findRegion("Red");
        rgRedIcon.flip(false, true);
        rgGreenIcon = game.aAtlas.findRegion("Green");
        rgGreenIcon.flip(false, true);
        rgBlueIcon = game.aAtlas.findRegion("Blue");
        rgBlueIcon.flip(false, true);
        rgYellowIcon = game.aAtlas.findRegion("Yellow");
        rgYellowIcon.flip(false, true);
        rgPinkIcon = game.aAtlas.findRegion("Pink");
        rgPinkIcon.flip(false, true);
        rgItemIcon = game.aAtlas.findRegion("Item");
        rgItemIcon.flip(false, true);
        rgGoldIcon = game.aAtlas.findRegion("Gold_Icon");
        rgGoldIcon.flip(false, true);
        rgBackIcon = game.aAtlas.findRegion("Back_Icon");
        rgBackIcon.flip(false, true);

        //font = new BitmapFont(Gdx.files.internal("Data\\Font\\WhiteFont.fnt"), true);

        closeWindowPos = new Vector2();
        closeWindowPos.x = (game.gameWidth - rgCloseWindow.getRegionWidth()) / 2;
        closeWindowPos.y = (game.gameHeight - rgCloseWindow.getRegionHeight()) / 2;
        startWindowPos = new Vector2();
        startWindowPos.x = (game.gameWidth - rgStartWindow.getRegionWidth()) / 2;
        startWindowPos.y = (game.gameHeight - rgStartWindow.getRegionHeight()) / 2;

        iconPos1 = new Vector2(startWindowPos.x + 101, startWindowPos.y + 180);
        iconPos2 = new Vector2(startWindowPos.x + 135, startWindowPos.y + 146);
        iconPos3 = new Vector2(startWindowPos.x + 135, startWindowPos.y + 223);
        levelPos = new Vector2(startWindowPos.x + rgStartWindow.getRegionWidth() / 2, startWindowPos.y + 15);

        btnClose = new Rectangle(startWindowPos.x + 526, startWindowPos.y + 55, 54, 54);
        btnNo = new Rectangle(closeWindowPos.x + 204, closeWindowPos.y + 241, 146, 50);
        btnPlay = new Rectangle(startWindowPos.x + 167, startWindowPos.y + 316, 240, 61);
        btnYes = new Rectangle(closeWindowPos.x + 41, closeWindowPos.y + 241, 146, 50);

        for (int i = 0; i < game.levelsCount; i++) {
            Rectangle rect = new Rectangle(game.levelsPosition.get(i).x, game.levelsPosition.get(i).y, RECT_WIDTH, RECT_WIDTH);
            levelsRect.add(rect);
        }

        setMode(Mode.mLevel);

        dispX = 0f;
        dSpeedX = 1f;
    }

    public FieldParam loadLevel(String fileName) {
        FileHandle file = Gdx.files.internal("Data\\Levels\\" + fileName);
        FieldParam fParam = null;

        try (DataInputStream dis = new DataInputStream(file.read())) {
            fParam = new FieldParam();

            fParam.width = dis.readInt();
            fParam.height = dis.readInt();

            fParam.fieldCells = new int[fParam.height][fParam.width];
            fParam.backCells = new int[fParam.height][fParam.width];
            fParam.cells = new Cell[fParam.height][fParam.width];
            fParam.bounds = new int[fParam.height + 2][fParam.width + 2];

            for (int i = 0; i < fParam.height; i++)
                for (int j = 0; j < fParam.width; j++)
                    fParam.fieldCells[i][j] = dis.readInt();

            for (int i = 0; i < fParam.height; i++)
                for (int j = 0; j < fParam.width; j++)
                    fParam.backCells[i][j] = dis.readInt();

            for (int i = 0; i < fParam.height; i++)
                for (int j = 0; j < fParam.width; j++) {
                    int intValue = dis.readInt();
                    if (intValue == 0)
                        fParam.cells[i][j] = null;
                    else {
                        fParam.cells[i][j] = new Cell();
                        fParam.cells[i][j].value = dis.readInt();
                        intValue = dis.readInt();
                        fParam.cells[i][j].canMove = intValue == 1;
                        intValue = dis.readInt();
                        fParam.cells[i][j].modif = intValue == 1;
                        intValue = dis.readInt();
                        fParam.cells[i][j].gold = intValue == 1;
                        fParam.cells[i][j].box = dis.readInt();
                        fParam.cells[i][j].chain = dis.readInt();
                        fParam.cells[i][j].ice = dis.readInt();
                        fParam.cells[i][j].dirt = dis.readInt();
                    }
                }

            for (int i = 0; i < (fParam.height + 2); i++)
                for (int j = 0; j < (fParam.width + 2); j++)
                    fParam.bounds[i][j] = dis.readInt();

            int intValue;
            intValue = dis.readInt();
            fParam.generateColors = new int[intValue];
            for (int i = 0; i < intValue; i++)
                fParam.generateColors[i] = dis.readInt();

            fParam.level = new Level();
            intValue = dis.readInt();
            if (intValue == 0)
                fParam.level.lType = Mach3Game.LevelType.ltSteps;
            else
                fParam.level.lType = Mach3Game.LevelType.ltTime;
            fParam.level.count = dis.readInt();

            fParam.level.mission1 = new Mission();
            intValue = dis.readInt();
            fParam.level.mission1.mType = Mach3Game.MissionType.values()[intValue];
            fParam.level.mission1.count = dis.readInt();
            fParam.level.mission1.color = dis.readInt();

            fParam.level.mission2 = new Mission();
            intValue = dis.readInt();
            fParam.level.mission2.mType = Mach3Game.MissionType.values()[intValue];
            fParam.level.mission2.count = dis.readInt();
            fParam.level.mission2.color = dis.readInt();

            intValue = dis.readInt();
            fParam.spawnPoints = new Array<Vector2>();
            for (int i = 0; i < intValue; i++) {
                Vector2 v = new Vector2();
                v.x = dis.readInt();
                v.y = dis.readInt();
                fParam.spawnPoints.add(v);
            }

            intValue = dis.readInt();
            fParam.itemPoints = new Array<Vector2>();
            for (int i = 0; i < intValue; i++) {
                Vector2 v = new Vector2();
                v.x = dis.readInt();
                v.y = dis.readInt();
                fParam.itemPoints.add(v);
            }

            Gdx.app.log("INFO", "Load is Successful");
        }
        catch (IOException ex) {
            Gdx.app.log("INFO", "Load is Unsuccessful");
        }
        return fParam;
    }

    private void update(float delta) {
        //rgPieceOfMap.setRegionX((int)dispX);
        rgPieceOfMap.setRegion((int)dispX, 0, (int) game.gameWidth, (int) game.gameHeight);
        rgPieceOfMap.flip(false, true);
        if (isSwap) {
            if (speedX > 0) {
                speedX -= dSpeedX;
                if (speedX < 0)
                    speedX = 0;
            }
            if (speedX < 0) {
                speedX += dSpeedX;
                if (speedX > 0)
                    speedX = 0;
            }
            if (!isPan)
                dispX = dispX + speedX;
            if (dispX < 0) {
                dispX = 0;
                speedX = 0;
            }
            if (dispX > rgFullMap.getRegionWidth() - game.gameWidth) {
                dispX = rgFullMap.getRegionWidth() - game.gameWidth;
                speedX = 0;
            }
        }
    }

    private void setMode(Mode gameMode) {
        this.gameMode = gameMode;
    }


    //region Screen_Region
    @Override
    public void show() {
        Gdx.app.log("GameInfo", "MapScreen: Show");
        Gdx.input.setInputProcessor(IM);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        //Gdx.app.log("GameInfo", "MapScreen: Render");
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        {
            batch.draw(rgPieceOfMap, 0, 0);
            String str = "";
            for (int i = 0; i < game.levelsCount; i++) {
                int x = (int) (levelsRect.get(i).x + RECT_WIDTH / 2 - rgCurrentLevel.getRegionWidth() / 2);
                int y = (int) (levelsRect.get(i).y + RECT_WIDTH / 2 - rgCurrentLevel.getRegionHeight() / 2);
                if (i + 1 == game.currentLevel) {
                    batch.draw(rgCurrentLevel, x - dispX, y);
                } else
                    if (i + 1 < game.currentLevel)
                        batch.draw(rgCompletedLevel, x - dispX, y);
                    else
                        batch.draw(rgLockedLevel, x - dispX, y);
                str = (i + 1) + "";
                layout.setText(game.font4, str);
                int tx = (int) (levelsRect.get(i).x + RECT_WIDTH / 2 - dispX - layout.width / 2);
                int ty = (int) (levelsRect.get(i).y + RECT_WIDTH / 2  - layout.height / 2);
                game.font4.draw(batch, str, tx, ty);
            }
            game.font.draw(batch, dispX + "", 30, 30);
            game.font.draw(batch, speedX + "", 30, 70);
            game.font.draw(batch, isSwap + "", 30, 110);
            game.font.draw(batch, selectedLevel + "", 30, 150);

            if (gameMode == Mode.mClose) {
                batch.draw(rgCloseWindow, closeWindowPos.x, closeWindowPos.y);
            }

            if (gameMode == Mode.mStart) {
                batch.draw(rgStartWindow, startWindowPos.x, startWindowPos.y);
                str = "Уровень " + selectedLevel;
                layout.setText(game.font4, str);
                game.font4.draw(batch, str, levelPos.x - layout.width / 2, levelPos.y);
                if (game.fParam.level.mission2.mType == Mach3Game.MissionType.mtNone) {
                    TextureRegion reg = rgBackIcon;
                    switch (game.fParam.level.mission1.mType) {
                        case mtDirt:
                            reg = rgBackIcon;
                            str = "Очистите все клетки";
                            break;
                        case mtGold:
                            reg = rgGoldIcon;
                            str = "Соберите все золотые\nсамородки";
                            break;
                    }
                    batch.draw(reg, iconPos1.x - reg.getRegionWidth() / 2, iconPos1.y - reg.getRegionHeight() / 2);
                    layout.setText(game.font3, str);
                    game.font3.draw(batch, str, iconPos1.x + 30, iconPos1.y - layout.height / 2);
                } else {
                    TextureRegion reg1 = rgBackIcon;
                    TextureRegion reg2 = rgBackIcon;
                    String str1 = "";
                    String str2 = "";
                    switch (game.fParam.level.mission1.mType) {
                        case mtDirt:
                            reg1 = rgBackIcon;
                            str1 = "Очистите все клетки";
                            break;
                        case mtGold:
                            reg1 = rgGoldIcon;
                            str1 = "Соберите все золотые\nсамородки";
                            break;
                    }
                    switch (game.fParam.level.mission2.mType) {
                        case mtDirt:
                            reg2 = rgBackIcon;
                            str2 = "Очистите все клетки";
                            break;
                        case mtGold:
                            reg2 = rgGoldIcon;
                            str2 = "Соберите все золотые\nсамородки";
                            break;
                    }
                    batch.draw(reg1, iconPos2.x - reg1.getRegionWidth() / 2, iconPos2.y - reg1.getRegionHeight() / 2);
                    layout.setText(game.font2, str1);
                    game.font2.draw(batch, str1, iconPos2.x + 30, iconPos2.y - layout.height / 2);

                    batch.draw(reg2, iconPos3.x - reg2.getRegionWidth() / 2, iconPos3.y - reg2.getRegionHeight() / 2);
                    layout.setText(game.font2, str2);
                    game.font2.draw(batch, str2, iconPos3.x + 30, iconPos3.y - layout.height / 2);
                }
            }
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
        if (keycode == Input.Keys.BACK) {
            if (gameMode == Mode.mLevel) {
                setMode(Mode.mClose);
            }
        }
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
        //Gdx.app.log("GameInfo", "touchDown_IP");
        if (gameMode == Mode.mLevel)
            isSwap = false;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.log("GameInfo", "touchUp");
        //game.fParam = loadLevel("2.lvl");
        //game.setScreen(new GameScreen(game));
        float sx = 0;
        float sy = 0;

        switch (gameMode) {
            case mLevel:
                boolean f = false;
                if (!isSwap) {
                    for (int i = 0; i < levelsRect.size; i++) {
                        sx = (screenX + dispX) * game.scaleCoef;
                        sy = screenY * game.scaleCoef;
                        if (levelsRect.get(i).contains(sx, sy)) {
                            selectedLevel = i + 1;
                            f = true;
                            break;
                        }
                    }

                    if (f) {
                        if (selectedLevel < 11 && selectedLevel <= game.currentLevel) {
                            game.fParam = loadLevel(selectedLevel + ".lvl");
                            //game.setScreen(new GameScreen(game));
                            setMode(Mode.mStart);
                        }
                    }
                }
                break;

            case mClose:
                sx = screenX * game.scaleCoef;
                sy = screenY * game.scaleCoef;

                if (btnNo.contains(sx, sy)) {
                    setMode(Mode.mLevel);
                }

                if (btnYes.contains(sx, sy)) {
                    Gdx.app.exit();
                }
                break;

            case mStart:
                sx = screenX * game.scaleCoef;
                sy = screenY * game.scaleCoef;

                if (btnPlay.contains(sx, sy)) {
                    game.setScreen(new GameScreen(game));
                }

                if (btnClose.contains(sx, sy)) {
                    setMode(Mode.mLevel);
                }
                break;
        }

        //field = new Field(game.fParam);
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
        //Gdx.app.log("GameInfo", "touchDown_GL");
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
        //Gdx.app.log("GameInfo", "Pan");
        //Gdx.app.log("GameInfo", "X = " + deltaX);
        if (gameMode == Mode.mLevel) {
            dispX = dispX - deltaX;
            speedX = -deltaX;
            if (dispX < 0)
                dispX = 0;
            if (dispX > rgFullMap.getRegionWidth() - game.gameWidth)
                dispX = rgFullMap.getRegionWidth() - game.gameWidth;
            isSwap = true;
            isPan = true;
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        //Gdx.app.log("GameInfo", "PanStop");
        if (gameMode == Mode.mLevel)
            isPan = false;
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
