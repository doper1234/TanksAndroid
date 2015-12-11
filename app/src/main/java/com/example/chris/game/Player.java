package com.example.chris.game;

import android.media.Image;
import android.widget.ImageView;

/**
 * Created by Chris on 09/12/2015.
 */
public class Player extends Actor{

    public Player(Game game, float x, float y, byte playerNumber, int... imageResources){
        super(game, x, y, playerNumber, imageResources);
    }



    @Override
    public void collide(){


    }



}
