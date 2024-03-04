package com.example.oggyandthecockroaches;

public class Oggy {

    private int life; //Oggy's number of life
    private int position; //Oggy's position
    private int score = 0;

    public Oggy() {
        life = 3;
        position = 2;
    }


    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void hit() {
        if (life > 0) {
            life--;
        }
    }

    public int getScore () {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}