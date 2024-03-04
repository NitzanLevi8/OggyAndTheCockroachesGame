package com.example.oggyandthecockroaches;

import java.util.Random;

public class Obstacle {

    private int image;

    // Obstacles matrix
    private int row;
    private final int COL; // The column remains constant, while the row changes.

    public Obstacle(int image, int row, int COL) {
        this.image = image;
        this.row = row;
        this.COL = COL;
    }

    //Random selection of a column from which the obstacle will start to move.
    public Obstacle (){
        Random rnd = new Random();
        COL= rnd.nextInt(5);
    }

    public Obstacle (int image){
        Random rnd = new Random();
        row = 0;
        COL= rnd.nextInt(5);
        this.image = image;

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setTheNextRow() { //What is the next row?
        row++;
    }

    public int getCOL() {
        return COL;
    }
}
