package com.example.oggyandthecockroaches;

public class Butterfly extends Obstacle{
    private int score = 0;

    public Butterfly(int image){
        super(image);
        this.score = 10;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore(){
        return score;
    }



}







