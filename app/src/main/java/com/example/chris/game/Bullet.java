package com.example.chris.game;

import android.media.MediaPlayer;

/**
 * Created by Chris on 09/12/2015.
 */
public class Bullet extends Actor{


    //SVGPath p;
    private final int BULLET_DIRECTION;
    static final int UP = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;
    static final int RIGHT = 4;



    public Bullet(Game game, float x, float y, int level, int... imageResources){
        super(game, x, y, imageResources);
        if(level == 1){
            speed = 10;
        }else{
            speed = 20;
        }

        if(game.p1.isUp){
            BULLET_DIRECTION = UP;
            isDown = isLeft = isRight = false;
            isUp = true;
            spriteFrame.setRotation(0);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(0);

        }else if(game.p1.isDown){
            BULLET_DIRECTION = DOWN;
            isUp = isLeft = isRight = false;
            isDown = true;
            spriteFrame.setRotation(180);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(180);

        }else if(game.p1.isLeft){
            BULLET_DIRECTION = LEFT;
            isDown = isUp = isRight = false;
            isLeft = true;
            spriteFrame.setRotation(270);
            spriteFrame.setRotationX(180);
            spriteFrame.setRotationY(0);

        }else {
            BULLET_DIRECTION = RIGHT;
            isDown = isLeft = isUp = false;
            isRight = true;
            spriteFrame.setRotation(90);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(0);
        }

    }
    @Override
    void collide() {

    }

    @Override
    public void update(){
        if(getBulletDirection() == Bullet.UP){
            goUp();
        }else if(getBulletDirection() == Bullet.DOWN){
            goDown();
        }else if(getBulletDirection() == Bullet.LEFT){
            goLeft();
        }else{
            goRight();
        }

        if(hasReachedLimits()){
            // game.bullets.remove(this);
            game.rl.removeView(spriteFrame);
            MediaPlayer mediaPlayer= MediaPlayer.create(game, R.raw.bullethitwall);
            mediaPlayer.start();
        }
    }

    public int getBulletDirection(){
        return BULLET_DIRECTION;
    }

    @Override
    public void goUp(){

        if(spriteFrame.getY() > yBoundsMin && game.canMoveForward()) {
            spriteFrame.setY(spriteFrame.getY() - speed);

        }else{
            reachedLimits = true;
        }

    }
    @Override
    public void goDown(){

        if(spriteFrame.getY() < yBoundsMax && game.canMoveBackward()) {
            spriteFrame.setY(spriteFrame.getY() + speed);
        }else{
            reachedLimits = true;
        }

    }
    @Override
    public void goLeft(){

        if(spriteFrame.getX() > xBoundsMin && game.canMoveLeft()) {
            spriteFrame.setX(spriteFrame.getX() - speed);
        }else{
            reachedLimits = true;
        }

    }
    @Override
    public void goRight(){

        if(spriteFrame.getX() <= xBoundsMax && game.canMoveRight()) {
            spriteFrame.setX(spriteFrame.getX() + speed);
        }else{
            reachedLimits = true;
        }

    }
}
