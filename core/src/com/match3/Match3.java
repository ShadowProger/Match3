package com.match3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.match3.gamelogic.FieldParam;
import com.screens.MapScreen;

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

        this.setScreen(new MapScreen(this));
    }

    public void loadAssets() {
        tMap = new Texture(Gdx.files.internal("Data\\Pictures\\Map2.png"));
        tBackground = new Texture(Gdx.files.internal("Data\\Pictures\\Background.png"));
        aBounds = new TextureAtlas(Gdx.files.internal("Data\\Pictures\\Bounds.atlas"));
        aTiles = new TextureAtlas(Gdx.files.internal("Data\\Pictures\\Tiles.atlas"));
        font = new BitmapFont(Gdx.files.internal("Data\\Font\\WhiteFont.fnt"), true);
    }
}
