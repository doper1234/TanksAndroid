package com.example.chris.game;

import android.widget.ImageView;

/**
 * Created by Chris on 09/12/2015.
 */
abstract class Actor {

    static final byte UP = 1;
    static final byte DOWN = 2;
    static final byte LEFT = 3;
    static final byte RIGHT = 4;
    static final byte SHOOT = 5;

    ImageView spriteFrame;
    float x,y;
    int up, down, left, right;
    int speed = 1;
    final int xBoundsMax;
    final int yBoundsMax;
    final int xBoundsMin;
    final int yBoundsMin;
    protected boolean reachedLimits = false;
    boolean isLeft, isRight, isUp, isDown;
    Game game;
    protected byte playerNumber;


    public Actor(Game game, float x, float y, byte playerNumber, int... imageResources){
        this.x = x;
        this.y = y;
        this.game = game;
        this.playerNumber = playerNumber;
        spriteFrame = new ImageView(game);
        spriteFrame.setImageResource(imageResources[0]);
        spriteFrame.setX(x);
        spriteFrame.setY(y);
        xBoundsMin = (int)(game.gameX + game.grid*2);
        yBoundsMin = (int)(game.gameY + game.grid*2);
        xBoundsMax = (int)(game.gameX + (26*game.grid));
        yBoundsMax = (int)(game.gameY + (26*game.grid));


    }

    abstract void collide();

    public void goUp(){

        if(game.writer !=null){
            game.writer.println(UP);
            game.writer.flush();
        }
        if(up == 0){
            down = left = right = 0;
            isDown = isLeft = isRight = false;
            isUp = true;
            spriteFrame.setRotation(0);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(0);
        }
        if(spriteFrame.getY() > yBoundsMin && game.canMoveForward() == true) {
               spriteFrame.setY(spriteFrame.getY() - speed);

        }else{
            reachedLimits = true;
        }
        up++;

    }
    public void goDown(){
        if(down == 0){

            if(game.writer !=null){
                game.writer.println(DOWN);
                game.writer.flush();
            }
            isUp = isLeft = isRight = false;
            isDown = true;
            up = left = right = 0;
            spriteFrame.setRotation(180);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(180);
        }
        if(spriteFrame.getY() < yBoundsMax && game.canMoveBackward()) {
            spriteFrame.setY(spriteFrame.getY() + speed);
        }else{
            reachedLimits = true;
        }
        down++;

    }
    public void goLeft(){
        if(left == 0) {
            if(game.writer !=null){
                game.writer.println(LEFT);
                game.writer.flush();
            }
            up = down = right = 0;
            isDown = isUp = isRight = false;
            isLeft = true;
            spriteFrame.setRotation(270);
            spriteFrame.setRotationX(180);
            spriteFrame.setRotationY(0);
        }
        if(spriteFrame.getX() > xBoundsMin && game.canMoveLeft()) {
            spriteFrame.setX(spriteFrame.getX() - speed);
        }else{
            reachedLimits = true;
        }
        left++;
    }
    public void goRight(){
        if(right == 0) {
            if(game.writer !=null){
                game.writer.println(RIGHT);
                game.writer.flush();
            }
            up = down = left = 0;
            isDown = isLeft = isUp = false;
            isRight = true;
            spriteFrame.setRotation(90);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(0);
        }
        if(spriteFrame.getX() <= xBoundsMax && game.canMoveRight()) {
            spriteFrame.setX(spriteFrame.getX() + speed);
        }else{
            reachedLimits = true;
        }
        right++;
    }

    public void shoot(){
        if(game.writer !=null){
            game.writer.println(SHOOT);
            game.writer.flush();
        }
    }

    public float getX(){
        return spriteFrame.getX();
    }
    public float getY(){
        return spriteFrame.getY();
    }

    public byte getPlayerNumber(){
        return playerNumber;
    }

    public boolean hasReachedLimits(){
        return reachedLimits;
    }


}
