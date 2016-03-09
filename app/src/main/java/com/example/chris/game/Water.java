package com.example.chris.game;

import android.media.Image;

/**
 * Created by Chris on 31/12/2015.
 */
public class Water extends Actor {

    int[] imageResources;
    static int update;

    public Water(Game game, float x, float y, int... imageResources) {
        super(game, x, y, imageResources);
        this.imageResources = imageResources;
        update = 0;
    }

    @Override
    void collide() {

    }
    public void update(){
        //spriteFrame.bringToFront();
        if(update == 10){
            spriteFrame.setImageResource(imageResources[1]);
        }else if(update == 20){
            spriteFrame.setImageResource(imageResources[2]);
        } else if(update == 30){
            update = 0;
            spriteFrame.setImageResource(imageResources[0]);
        }




    }
}
