package com.example.chris.game;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Chris on 02/12/2015.
 */
public class Map {

    private int map;
    private int[][] mapGrid;
    private int mapLength = 26;

    public Map(int mapNumber){

        loadMap(mapNumber);
    }

    private void loadMap(int mapNumber){
        try {
            Scanner s = new Scanner(new File("src\\main\\java\\com\\example\\chris\\game\\maps\\map1.txt"));
            mapGrid = new int[mapLength][mapLength];
            for (int i = 0; i < mapGrid.length; i++) {
                for (int j = 0; j < mapGrid.length; j++) {
                    mapGrid[i][j] = s.nextInt();
                    Log.i("map","idk here");
                }
            }
        } catch (FileNotFoundException e) {
            Log.i("hi","idk whats going on here");
        }

    }

    public int[][] getMap(){
        return mapGrid;
    }
}
