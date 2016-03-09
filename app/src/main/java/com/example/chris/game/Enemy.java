package com.example.chris.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Chris on 31/12/2015.
 */


public class Enemy extends Actor {

    Random random;
    final int DOWN = 1;
    final int UP = 2;
    final int LEFT = 3;
    final int RIGHT = 4;
    final int SHOOT = 5;

    public Enemy(Game game, float x, float y, int level, int... imageResources) {
        super(game, x, y, imageResources);
        bullets = new ArrayList<>();
        spriteFrame.setRotation(180);
        spriteFrame.setRotationX(0);
        spriteFrame.setRotationY(180);
        if(level == 2){
            speed = 20;
        }else if(level ==4){
            speed = 5;
        }else{
            speed = 10;
        }
        random = new Random();
    }
    public void update(){
        moveImages();
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
        int r = random.nextInt(6);

        if(r == 1){
            int direction = random.nextInt(4);
            if(direction == UP){
                goUp();
            }else if(direction == DOWN){
                goDown();
            }else if(direction==LEFT){
                goLeft();
            }else{
                goRight();
            }
        } else if(r==2){
            shoot();
        } else{
            if(isLeft){
                goLeft();
            }
            if(isRight){
                goRight();
            }
            if(isUp){
                goUp();
            }
            if(isDown){
                goDown();
            }
        }


//        if(hasReachedLimits()){
//
//            goLeft();
//        }else if(!isLeft){
//            goRight();
//        }else{
//            goLeft();
//        }
    }

    @Override
    void collide() {

    }
}
