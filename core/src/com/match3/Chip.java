package com.match3;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.match3.gamelogic.Cell;

public class Chip {
    private final int CHIP_SPEED = 8;
    public Vector2 cellPos = new Vector2();
    public Vector2 pos = new Vector2();
    public Vector2 newPos = new Vector2();
    public float speedX;
    public float speedY;
    public boolean isOnPlace;
    public int layer;
    public Cell cell = new Cell();
    public float alpha = 1f;
    public float scale = 1f;
    public float newAlpha = 1f;
    public float newScale = 1f;
    public float dAlpha;
    public float dScale;

    public Chip() {}
    public Chip(Cell cell) {
        this.cell.copy(cell);
    }

    public void move() {
        if (!isOnPlace) {
            pos.x = pos.x + speedX;
            if (speedX > 0)
                if (pos.x > newPos.x)
                    pos.x = newPos.x;
            if (speedX < 0)
                if (pos.x < newPos.x)
                    pos.x = newPos.x;

            pos.y = pos.y + speedY;
            if (speedY > 0)
                if (pos.y > newPos.y)
                    pos.y = newPos.y;
            if (speedY < 0)
                if (pos.y < newPos.y)
                    pos.y = newPos.y;

            if (pos.x == newPos.x && pos.y == newPos.y)
                isOnPlace = true;
        }
    }

    public void changeScale() {
        scale = scale + dScale;
        if (dScale > 0)
            if (scale > newScale)
                scale = newScale;
        if (dScale < 0)
            if (scale < newScale)
                scale = newScale;
    }

    public void changeAlpha() {
        alpha = alpha + dAlpha;
        if (dAlpha > 0)
            if (alpha > newAlpha)
                alpha = newAlpha;
        if (dAlpha < 0)
            if (alpha < newAlpha)
                alpha = newAlpha;
    }

    public void setNewScale(float newScale) {
        this.newScale = newScale;
    }

    public void setNewAlpha(float newAlpha) {
        this.newAlpha = newAlpha;
    }

    public void setCellPos(int x, int y) {
        cellPos.x = x;
        cellPos.y = y;
    }

    public void setNewPos(int x, int y) {
        newPos.x = x;
        newPos.y = y;
        if (pos.x == newPos.x && pos.y == newPos.y)
            isOnPlace = true;
        else {
            isOnPlace = false;
            if (newPos.x == pos.x)
                speedX = 0f;
            else
                if (newPos.x > pos.x)
                    speedX = CHIP_SPEED;
                else
                    speedX = -CHIP_SPEED;
            if (newPos.y == pos.y)
                speedY = 0f;
            else
                if (newPos.y > pos.y)
                    speedY = CHIP_SPEED;
                else
                    speedY = -CHIP_SPEED;
        }
    }

    public void setPos(int x, int y) {
        pos.x = x;
        pos.y = y;
        newPos.x = x;
        newPos.y = y;
        isOnPlace = true;
        speedX = 0f;
        speedY = 0f;
        /*
        if (pos.x == newPos.x && pos.y == newPos.y)
            isOnPlace = true;
        else {
            isOnPlace = false;
            if (newPos.x == pos.x)
                speedX = 0f;
            else
            if (newPos.x > pos.x)
                speedX = CHIP_SPEED;
            else
                speedX = -CHIP_SPEED;
            if (newPos.y == pos.y)
                speedY = 0f;
            else
            if (newPos.y > pos.y)
                speedY = CHIP_SPEED;
            else
                speedY = -CHIP_SPEED;
        }
        */
    }
}

