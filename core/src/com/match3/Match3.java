package com.match3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.match3.gamelogic.Cell;
import com.match3.gamelogic.FieldParam;
import com.match3.gamelogic.Level;
import com.match3.gamelogic.Mach3Game;
import com.match3.gamelogic.Mission;
import com.match3.gamelogic.PossibleMatchPattern;
import com.match3.gamelogic.PossibleMatchPatterns;
import com.screens.GameScreen;
import com.screens.MapScreen;

import java.io.DataInputStream;
import java.io.IOException;

public class Match3 extends Game {
    OrthographicCamera camera;
    public SpriteBatch batch;

    public Texture tMap;
    public Texture tBackground;
    public TextureAtlas aBounds;
    public TextureAtlas aTiles;
    public BitmapFont font;

    public float gameWidth;
    public float gameHeight;
    public float scaleCoef;
    public float screenWidth;
    public float screenHeight;

    public FieldParam fParam;
    public PossibleMatchPatterns PMPatterns;

    @Override
    public void create () {
        Gdx.app.log("GameInfo", "Match3: Create");

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        gameHeight = 720f;
        gameWidth = screenWidth * gameHeight / screenHeight;

        scaleCoef = gameWidth / screenWidth;

        camera = new OrthographicCamera();
        camera.setToOrtho(true, gameWidth, gameHeight);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        loadAssets();
        loadPMPatterns();



        this.setScreen(new MapScreen(this));
        //this.setScreen(new GameScreen(this));
    }

    public void loadAssets() {
        tMap = new Texture(Gdx.files.internal("Data\\Pictures\\Map.png"));
        tBackground = new Texture(Gdx.files.internal("Data\\Pictures\\Background.png"));
        aBounds = new TextureAtlas(Gdx.files.internal("Data\\Pictures\\Bounds.atlas"));
        aTiles = new TextureAtlas(Gdx.files.internal("Data\\Pictures\\Tiles.atlas"));
        font = new BitmapFont(Gdx.files.internal("Data\\Font\\WhiteFont.fnt"), true);
    }

    public void loadPMPatterns() {
        FileHandle file = Gdx.files.internal("Data\\PMPatterns.bin");
        PMPatterns = new PossibleMatchPatterns();

        try (DataInputStream dis = new DataInputStream(file.read())) {
            int patternsCount = dis.readInt();

            for (int i = 0; i < patternsCount; i++) {
                PMPatterns.patterns[i] = new PossibleMatchPattern();
                PMPatterns.patterns[i].napr.x = dis.readInt();
                PMPatterns.patterns[i].napr.y = dis.readInt();
                int gemsCount = dis.readInt();
                for (int j = 0; j < gemsCount; j++) {
                    Vector2 v = new Vector2();
                    v.x = dis.readInt();
                    v.y = dis.readInt();
                    PMPatterns.patterns[i].gems.add(v);
                }
            }

            int count = 0;
            // 3
            for (int i = 0; i < PMPatterns.left3.length; i++) {
                PMPatterns.left3[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.right3.length; i++) {
                PMPatterns.right3[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.up3.length; i++) {
                PMPatterns.up3[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.down3.length; i++) {
                PMPatterns.down3[i] = count;
                count++;
            }
            // 4
            for (int i = 0; i < PMPatterns.left4.length; i++) {
                PMPatterns.left4[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.right4.length; i++) {
                PMPatterns.right4[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.up4.length; i++) {
                PMPatterns.up4[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.down4.length; i++) {
                PMPatterns.down4[i] = count;
                count++;
            }
            // 5
            for (int i = 0; i < PMPatterns.left5.length; i++) {
                PMPatterns.left5[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.right5.length; i++) {
                PMPatterns.right5[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.up5.length; i++) {
                PMPatterns.up5[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.down5.length; i++) {
                PMPatterns.down5[i] = count;
                count++;
            }
            // 6
            for (int i = 0; i < PMPatterns.left6.length; i++) {
                PMPatterns.left6[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.right6.length; i++) {
                PMPatterns.right6[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.up6.length; i++) {
                PMPatterns.up6[i] = count;
                count++;
            }
            for (int i = 0; i < PMPatterns.down6.length; i++) {
                PMPatterns.down6[i] = count;
                count++;
            }
            // 7
            PMPatterns.left7 = count;
            count++;
            PMPatterns.right7 = count;
            count++;
            PMPatterns.up7 = count;
            count++;
            PMPatterns.down7 = count;

            Gdx.app.log("INFO", "Load is Successful");
        }
        catch (IOException ex) {
            Gdx.app.log("INFO", "Load is Unsuccessful");
        }
    }
}
