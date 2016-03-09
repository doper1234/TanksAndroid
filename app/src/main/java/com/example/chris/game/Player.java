package com.example.chris.game;

import android.media.Image;
import android.media.MediaPlayer;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Chris on 09/12/2015.
 */
public class Player extends Actor{

    protected byte playerNumber;

    public Player(Game game, float x, float y, byte playerNumber, int... imageResources){
        super(game, x, y, imageResources);
        this.playerNumber = playerNumber;
        bullets = new ArrayList<>();
        speed = 20;
    }


    public void goUp(){

        if(game.writer !=null){
            game.writer.println(playerNumber + "," + UP);
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

    @Override
    public void collide(){


    }

    @Override
    public void update() {
        moveImages();
        if(isUp){
            goUp();
        }
        if(isDown){
            goDown();
        }
        if(isLeft){
            goLeft();
        }
        if(isRight){
            goRight();
        }
        for(Bullet b:bullets){
            b.update();
        }
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            if (it.next().hasReachedLimits()) {
                it.remove();
                // If you know it's unique, you could `break;` here
            }
        }
    }



    public byte getPlayerNumber(){
        return playerNumber;
    }



}
