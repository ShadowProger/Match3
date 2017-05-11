package com.match3.gamelogic;

public class Cell {
    public int value;
    public boolean canMove;
    public boolean modif;
    public boolean gold;
    public int box;
    public int chain;
    public int ice;
    public int dirt;
    public boolean hold;
    public boolean inSwap;

    public Cell() {
        value = 0;
        canMove = true;
        modif = false;
        gold = false;
        box = 0;
        chain = 0;
        ice = 0;
        dirt = 0;
        hold = false;
        inSwap = false;
    }

    public void copy(Cell cell) {
        value = cell.value;
        canMove = cell.canMove;
        modif = cell.modif;
        gold = cell.gold;
        box = cell.box;
        chain = cell.chain;
        ice = cell.ice;
        dirt = cell.dirt;
        hold = false;
        inSwap = cell.inSwap;
    }

    public void setInitial() {
        value = 0;
        canMove = true;
        modif = false;
        gold = false;
        box = 0;
        chain = 0;
        ice = 0;
        dirt = 0;
        hold = false;
        inSwap = false;
    }
}
