package com.example.chris.game;

import android.media.MediaPlayer;
import android.widget.ImageView;

import java.util.ArrayList;

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
    int speed = 2;
    int move = 0;
    final int xBoundsMax;
    final int yBoundsMax;
    final int xBoundsMin;
    final int yBoundsMin;
    protected boolean reachedLimits = false;
    boolean isLeft, isRight, isUp, isDown;
    Game game;
    ArrayList<Bullet> bullets;
    int[] imageResources;



    public Actor(Game game, float x, float y, int... imageResources){
        this.x = x;
        this.y = y;
        this.game = game;
        this.imageResources = imageResources;
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
    abstract public void update();


    public void goUp(){

//        if(game.writer !=null){
//            game.writer.println(/*playerNumber +*/ "," + UP);
//            game.writer.flush();
//        }
        if(up == 0){
            down = left = right = 0;
            isDown = isLeft = isRight = false;
            isUp = true;
            spriteFrame.setRotation(0);
            spriteFrame.setRotationX(0);
            spriteFrame.setRotationY(0);
        }
        if(game.canMoveForward()){

        }else{
            speed = 0;
        }
        if(spriteFrame.getY() > yBoundsMin && game.canMoveForward()) {
               spriteFrame.setY(spriteFrame.getY() - speed);

        }else{
            reachedLimits = true;
        }
        up++;

    }
    public void goDown(){

        if(down == 0){

//            if(game.writer !=null){
//                game.writer.println(DOWN);
//                game.writer.flush();
//            }
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
//            if(game.writer !=null){
//                game.writer.println(LEFT);
//                game.writer.flush();
//            }
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
//            if(game.writer !=null){
//                game.writer.println(RIGHT);
//                game.writer.flush();
//            }
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
//        if(game.writer !=null){
//            game.writer.println(SHOOT);
//            game.writer.flush();
//        }
        if(bullets.isEmpty()) {
            Bullet b = new Bullet(game, getX() - (game.grid + game.grid/2), getY() - (game.grid * 2), 2, R.drawable.bullet);
            bullets.add(b);
            b.spriteFrame.setScaleX(0.2f);
            b.spriteFrame.setScaleY(0.2f);
            game.rl.addView(b.spriteFrame, game.lp);
            MediaPlayer mediaPlayer= MediaPlayer.create(game, R.raw.shoot);
            mediaPlayer.start();
        }
    }

    public float getX(){
        return spriteFrame.getX();
    }
    public float getY(){
        return spriteFrame.getY();
    }

    protected void moveImages(){
        //if(isLeft || isRight || isDown || isUp) {
            if (move == 10) {
                spriteFrame.setImageResource(imageResources[1]);
                move = 0;
            }
            if (move == 0) {
                spriteFrame.setImageResource(imageResources[0]);
            }
            move++;
        //}
    }



    public boolean hasReachedLimits(){
        return reachedLimits;
    }


}
