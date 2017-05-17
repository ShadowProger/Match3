package com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
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

    TextureRegion rgFullMap;
    TextureRegion rgPieceOfMap;

    private float dispX;
    private float speedX;
    private float dSpeedX;
    private boolean isSwap;
    private boolean isPan;

    //private Field field;

    public MapScreen(Match3 game) {
        Gdx.app.log("GameInfo", "MapScreen: Create");
        this.game = game;
        batch = game.batch;

        IM = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        IM.addProcessor(gd);
        IM.addProcessor(this);

        //tMap = new Texture(Gdx.files.internal("Data\\Pictures\\Map2.png"));
        rgFullMap = new TextureRegion(game.tMap, 0, 0, game.tMap.getWidth(), game.tMap.getHeight());
        rgFullMap.flip(false, true);
        rgPieceOfMap = new TextureRegion(game.tMap, 0, 0, (int) game.gameWidth, (int) game.gameHeight);
        rgPieceOfMap.flip(false, true);

        //font = new BitmapFont(Gdx.files.internal("Data\\Font\\WhiteFont.fnt"), true);

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

            fParam.itemPoints = new Array<Vector2>();

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

    //region Screen_Region
    @Override
    public void show() {
        Gdx.app.log("GameInfo", "MapScreen: Show");
        Gdx.input.setInputProcessor(IM);
        //Gdx.input.setCatchBackKey(true);
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
            game.font.draw(batch, dispX + "", 30, 30);
            game.font.draw(batch, speedX + "", 30, 70);
            game.font.draw(batch, isSwap + "", 30, 110);
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
        Gdx.app.log("GameInfo", "touchDown_IP");
        isSwap = false;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("GameInfo", "touchUp");
        game.fParam = loadLevel("2.lvl");
        game.setScreen(new GameScreen(game));
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
        Gdx.app.log("GameInfo", "touchDown_GL");
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
        Gdx.app.log("GameInfo", "Pan");
        //Gdx.app.log("GameInfo", "X = " + deltaX);
        dispX = dispX - deltaX;
        speedX = -deltaX;
        if (dispX < 0)
            dispX = 0;
        if (dispX > rgFullMap.getRegionWidth() - game.gameWidth)
            dispX = rgFullMap.getRegionWidth() - game.gameWidth;
        isSwap = true;
        isPan = true;
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("GameInfo", "PanStop");
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
