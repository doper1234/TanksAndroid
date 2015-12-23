package com.example.chris.game;

import android.app.Activity;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Game extends Activity {

    Display display;
    float width;
    float height;
    int thing = 26;
    int gameX = -100;
    int gameY = 32;
    float grid;
    Map map;
    RelativeLayout rl;
    RelativeLayout.LayoutParams lp;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    Button start1PGameButton, start2PGameButton, startOnlineGameButton;
    float stageScreenY;
    private float resolution = 2.5f;
    ImageButton upBtn, downBtn, leftBtn, rightBtn, centerBtn, shootBtn, pauseBtn;
    boolean up, down, left, right, pause, shoot;
    Player p1, p2, pMe;
    int p1X;// = (int)(p1.getX() + 76)/12;//13 min -76
    int p1Y;//= (int)(p1.getY() - 56)/12;//13 min 52
    ImageView stageScreenTop, stageScreenBottom;
    private int mapNumber = 1;
    private ImageView[][] mapTerrainViews;
    TextView textViewStage, playerLocationView;
    private ArrayList<Bullet> bullets;
    MediaPlayer mp;
    int[][] mapGrid;

    Socket connectToServer;
    BufferedReader reader;
    PrintWriter writer;
    Thread readerThread;
    EditText ipText;
    String ip = "172.16.32.158";
    boolean connected = false;
    boolean initializing = true;
    byte playerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_game);

//        View decorView = getWindow().getDecorView();
//       int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        rl = (RelativeLayout)findViewById(R.id.activity_game);
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //setupTanksMainScreen();
        //setupTanksPlayArea();
        setButtons();

        stageScreenY = start1PGameButton.getX();
        mp = MediaPlayer.create(this, R.raw.move);

        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();






    }

    private void setButtons(){
        bullets = new ArrayList<>();
        ipText = (EditText) findViewById(R.id.ipText);
        ipText.setText(ip);
        textViewStage = (TextView) findViewById(R.id.textViewStage);
        textViewStage.setText("Stage " + mapNumber);
        textViewStage.setVisibility(View.INVISIBLE);
        playerLocationView = (TextView) findViewById(R.id.playerLocationView);
        upBtn = (ImageButton) findViewById(R.id.imageButtonUp);
        upBtn.setVisibility(View.INVISIBLE);
        upBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                pMe.goUp();
                if (!mp.isPlaying()) {
                    mp.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                }

                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        downBtn = (ImageButton) findViewById(R.id.imageButtonDown);
        downBtn.setVisibility(View.INVISIBLE);
        downBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                pMe.goDown();
                if (!mp.isPlaying()) {
                    mp.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        downBtn.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                pMe.goDown();
                if (!mp.isPlaying()) {
                    mp.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        leftBtn = (ImageButton) findViewById(R.id.imageButtonLeft);
        leftBtn.setVisibility(View.INVISIBLE);
        leftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                pMe.goLeft();
                if (!mp.isPlaying()) {
                    mp.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        rightBtn = (ImageButton) findViewById(R.id.imageButtonRight);
        rightBtn.setVisibility(View.INVISIBLE);
        rightBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                pMe.goRight();
                if (!mp.isPlaying()) {
                    mp.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        centerBtn = (ImageButton) findViewById(R.id.imageButtonCenter);
        centerBtn.setVisibility(View.INVISIBLE);
        shootBtn = (ImageButton) findViewById(R.id.imageButtonShoot);
        shootBtn.setVisibility(View.INVISIBLE);
        shootBtn.setSoundEffectsEnabled(false);
        shootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bullets.isEmpty()) {
                    Bullet b = new Bullet(Game.this, pMe.getX() - (grid + grid/2), pMe.getY() - (grid * 2), (byte)1, R.drawable.bullet);
                    bullets.add(b);
                    rl.addView(b.spriteFrame, lp);
                    MediaPlayer mediaPlayer= MediaPlayer.create(Game.this, R.raw.shoot);
                    mediaPlayer.start();
                }
            }
        });
        pauseBtn= (ImageButton) findViewById(R.id.imageButtonPause);
        pauseBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mapNumber++;
                textViewStage.setText("Stage " + mapNumber);
                //getMap(mapNumber);
                //setupMap();
                loadMap();

            }
        });
        pauseBtn.setVisibility(View.INVISIBLE);

        start1PGameButton = (Button) findViewById(R.id.onePlayerButton);
        start1PGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setControllerButtonsVisible();
                setupTanksPlayArea();
                startTimer();

            }
        });
        start2PGameButton = (Button) findViewById(R.id.twoPlayerButton);
        start2PGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setControllerButtonsVisible();
                setupTanksPlayArea();

            }
        });
        startOnlineGameButton = (Button) findViewById(R.id.onlineButton);
        startOnlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //connectToServer();
                ip = ipText.getText().toString();
                readerThread = new Thread(new IncomingReader());
                readerThread.start();
                //if(connected){
                    setControllerButtonsVisible();
                    setupTanksPlayArea();
                    startTimer();
                //}



            }
        });
    }

    private void setControllerButtonsVisible(){
        textViewStage.setVisibility(View.VISIBLE);
        ipText.setVisibility(View.INVISIBLE);
        upBtn.setVisibility(View.VISIBLE);
        downBtn.setVisibility(View.VISIBLE);
        leftBtn.setVisibility(View.VISIBLE);
        rightBtn.setVisibility(View.VISIBLE);
        centerBtn.setVisibility(View.VISIBLE);
        shootBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
        start1PGameButton.setVisibility(View.GONE);
        start2PGameButton.setVisibility(View.GONE);
        startOnlineGameButton.setVisibility(View.GONE);
    }

    public void setupTanksMainScreen(){

        TextView hiScores, battle, city, disclaimer;
        Button onePlayerButton;

        hiScores = new TextView(this);
        hiScores.setText("Hi Score:   00");
        rl.addView(hiScores, lp);

        battle = new TextView(this);
        battle.setText("Battle");
        rl.addView(battle, lp);

        city = new TextView(this);
        city.setText("City");
        rl.addView(city, lp);

        onePlayerButton = new Button(this);
        onePlayerButton.setText("One Player Game");
        rl.addView(onePlayerButton, lp);

        disclaimer = new TextView(this);
        disclaimer.setText("Namecot");
        rl.addView(disclaimer, lp);






    }
    private void setupControlButtons(){

        Button leftBtn, rightBtn, upBtn, downBtn, shootBtn;
        leftBtn = new Button(this);
        leftBtn.setText("L");
        rl.addView(leftBtn, lp);

        rightBtn = new Button(this);
        rightBtn.setText("R");
        rl.addView(rightBtn, lp);

        upBtn = new Button(this);
        upBtn.setText("U");
        rl.addView(upBtn, lp);

        downBtn = new Button(this);
        downBtn.setText("D");
        rl.addView(downBtn, lp);

        shootBtn = new Button(this);
        shootBtn.setText("U");
        rl.addView(shootBtn, lp);



    }
    private void setupTanksPlayArea(){

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        Display display = getWindowManager().getDefaultDisplay();
//        float width = display.getWidth();
//        float height = display.getHeight();
        //textViewStage.setText(size + " size" + stageWidth +", " + stageHeight);

        if(width <=800) {

            resolution = 0.9f;
            grid = resolution * 16f;
        } else{

            resolution = 1.5f; //needs to be set based on screen size, for emulator 1, tablet 1.5
            grid = resolution * 8; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
        }

        if(Build.VERSION.SDK_INT <= 15){
             //needs to be set based on screen size, for emulator 1, tablet 1.5
            //grid = resolution * 8; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
            resolution = 1.8f; //needs to be set based on screen size, for emulator 1, tablet 1.5
            grid = resolution * 8; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
        }

        textViewStage.setText("API " +Build.VERSION.SDK_INT);
        startNewStageAnimation();
        //getMap();

    }

    private void startNewStageAnimation(){

        stageScreenTop = new ImageView(this);
        stageScreenTop.setImageResource(R.drawable.stagescreen);
        //stageScreenTop.setScaleX(resolution);
        //stageScreenTop.setScaleY(resolution);
        rl.addView(stageScreenTop, lp);
        stageScreenBottom = new ImageView(this);
        stageScreenBottom.setImageResource(R.drawable.stagescreen);
       // stageScreenBottom.setScaleX(resolution);
       // stageScreenBottom.setScaleY(resolution);
        rl.addView(stageScreenBottom, lp);

       ImageView stageBorder = new ImageView(this);
        stageBorder.setImageResource(R.drawable.stageborder);
       // stageBorder.setScaleX(resolution);
       // stageBorder.setScaleY(resolution);
//        setupTanksPlayArea();
       // rl.addView(stageBorder, lp);
        setupMap();
        //startTimer();

    }

    private void setupMap(){

        int x = (int)width/2 -(int)(1.5 * grid*26);
        //int x = gameX;
        //int y = gameY;
        int y = (int)(grid * 1.5);
        gameX = x;
        gameY = y;

        //map = new Map(1);
        //int[][] mapGrid = getMap1();
        mapGrid = getMap(mapNumber);
        mapTerrainViews = new ImageView[mapGrid.length][mapGrid.length];
        MediaPlayer mediaPlayer= MediaPlayer.create(Game.this, R.raw.theme);
        mediaPlayer.start();
        for(int i = 0; i < mapGrid.length; i++){


            for(int j = 0; j < mapGrid.length; j++) {
                ImageView mapTerrainView = new ImageView(this);



                if(j == 0 || j == mapGrid.length -1){
                    ImageView boxView = new ImageView(this);
                    boxView.setImageResource(R.drawable.border);

                    if(j == 0){
                        boxView.setY(((y + grid) + i * grid) - grid);
                        boxView.setX((x - grid) + j * grid);
                    }else {
                        boxView.setY(((y + grid) + i * grid) - grid);
                        boxView.setX((x + grid) + j * grid);
                    }


                    boxView.setScaleX(resolution);
                    boxView.setScaleY(resolution);
                    rl.addView(boxView,lp);
                }
                if(i == 0 || i == mapGrid.length -1){
                    ImageView boxView = new ImageView(this);
                    boxView.setImageResource(R.drawable.border);

                    if(i == 0){
                        boxView.setX(((x + grid) + j * grid) - grid);
                        boxView.setY((y - grid) + i * grid);
                    }else {
                        boxView.setX(((x + grid) + j * grid) - grid);
                        boxView.setY((y + grid) + i * grid);
                    }


                    boxView.setScaleX(resolution);
                    boxView.setScaleY(resolution);
                    rl.addView(boxView,lp);
                }


                mapTerrainView.setScaleX(resolution);
                mapTerrainView.setScaleY(resolution);
                mapTerrainView.setX(x + j * grid);
                mapTerrainView.setY(y + i * grid);

                mapTerrainViews [i][j] = mapTerrainView;
                if(mapGrid[i][j] == 0){
                    mapTerrainView.setImageResource(R.drawable.blank);
                }
                else if (mapGrid[i][j] == 1) {
                    mapTerrainView.setImageResource(R.drawable.bricks);
                } else if (mapGrid[i][j] == 2) {
                    mapTerrainView.setImageResource(R.drawable.steel);
                } else if(mapGrid[i][j] == 3){
                    mapTerrainView.setImageResource(R.drawable.trees);
                    mapTerrainView.bringToFront();
                } else if(mapGrid[i][j] == 4){
                    mapTerrainView.setImageResource(R.drawable.flag);
                    mapTerrainView.setScaleX(resolution*2);
                    mapTerrainView.setScaleY(resolution * 2);
                    mapTerrainView.setX((x + grid/2) + j * grid);
                    mapTerrainView.setY((y + grid/2) + i * grid);
                } else if(mapGrid[i][j] == 5){
                    mapTerrainView.setImageResource(R.drawable.water);
                }else if(mapGrid[i][j] == 6) {
                    mapTerrainView.setImageResource(R.drawable.ice);
                } else if(mapGrid[i][j] == 8){
                    if(playerNumber == 1){

                        p1 = new Player(this,(x + grid/2) + j * grid, (y + grid/2) + i * grid, (byte)1 , R.drawable.p1);
                        p2 = new Player(this,(x + grid) + j * grid, (y + grid) + i * grid, (byte)2, R.drawable.p2);
                        pMe = p1;
                    }else if (playerNumber == 2){
                        p1 = new Player(this,(x + grid/2) + j * grid, (y + grid/2) + i * grid, (byte)2 , R.drawable.p1);
                        p2 = new Player(this,(x + grid) + j * grid, (y + grid) + i * grid, (byte)1, R.drawable.p2);
                        pMe = p1;
                    }else{
                        p1 = new Player(this,(x + grid/2) + j * grid, (y + grid/2) + i * grid, (byte)1 , R.drawable.p1);
                        pMe = p1;
                    }

                    p1.spriteFrame.setScaleX(resolution*2);
                    p1.spriteFrame.setScaleY(resolution * 2);
                    rl.addView(p1.spriteFrame, 0, lp);
                    if(p2 != null){
                        p2.spriteFrame.setScaleX(resolution*2);
                        p2.spriteFrame.setScaleY(resolution * 2);
                        rl.addView(p2.spriteFrame, 0, lp);
                    }

                    p1X = (int)(p1.getX() + 76)/12;//13 min -76
                    p1Y = (int)(p1.getY() - 56)/12;//13 min 52
                }


                rl.addView(mapTerrainView, lp);

            }

        }

    }
//    private int[][] getMap1(){
//        int[][] map1  =
//
//                {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                        {       0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,2,2,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,2,2,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
//                        {   0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
//                        {   1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1},
//                        {   2,2,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,2,2},
//                        {   0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
//                        {   0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,1,1,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,1,1,0,0,1,1,0,0,0,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0},
//                        {   0,0,0,0,0,0,0,0,8,0,0,1,4,0,1,0,9,0,0,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0}};
//        return map1;
//    }

    private void loadMap(){
         mapGrid = getMap(mapNumber);
        for(int i = 0; i < mapGrid.length; i++){
            for(int j = 0; j < mapGrid.length; j++) {
                ImageView mapTerrainView = mapTerrainViews[i][j];

             //   mapTerrainView.setScaleX(resolution);
              //  mapTerrainView.setScaleY(resolution);

                if(mapGrid[i][j] == 0){
                    mapTerrainView.setImageResource(R.drawable.blank);
                }
                else if (mapGrid[i][j] == 1) {
                    mapTerrainView.setImageResource(R.drawable.bricks);
                } else if (mapGrid[i][j] == 2) {
                    mapTerrainView.setImageResource(R.drawable.steel);
                } else if(mapGrid[i][j] == 3){
                    mapTerrainView.setImageResource(R.drawable.trees);
                } else if (mapGrid[i][j] == 4){
                    mapTerrainView.setImageResource(R.drawable.flag);
                //    mapTerrainView.setScaleX(resolution*2);
                //    mapTerrainView.setScaleY(resolution * 2);

                } else if(mapGrid[i][j] == 5){
                    mapTerrainView.setImageResource(R.drawable.water);
                }else if(mapGrid[i][j] == 6) {
                    mapTerrainView.setImageResource(R.drawable.ice);
                }

            }

        }
    }

    private int[][] getMap(int mapNumber){
        int[][] tempMap;
        final int totalMaps = 35;
        tempMap = new int[26][26];

        int mapToLoad = mapNumber;
        if(mapNumber > totalMaps){
            mapToLoad = (mapNumber % totalMaps);
        }

        try
        {
            InputStream instream = getResources().openRawResource(getResources().getIdentifier("raw/map" + mapToLoad, "raw", getPackageName()));
            if (instream != null){
                InputStreamReader inputreader = new InputStreamReader(instream);

                BufferedReader reader = new BufferedReader(inputreader);
		        for (int i = 0; i < tempMap.length; i++) {
		            String[] items = reader.readLine().split(" ");
                    for (int j = 0; j < tempMap.length; j++) {
                      tempMap[i][j] = Integer.parseInt(items[j]);
                       // Log.e("Map:", "Here: " + tempMap[i][j]);
                    }
                } //Log.e("Map:", "");
            }
        }
        catch (Exception e)
        {
            String error="";
            error=e.getMessage();
        }

        return tempMap;
    }

    @Override
    protected void onResume() {

        super.onResume();



        //onResume we start our timer so it can start when the app comes from the background

        //startTimer();

    }
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 50, 100); //

    }
    public void stoptimertask(View v) {

        //stop the timer, if it's not already null
        if (timer != null) {

            timer.cancel();
            timer = null;
        }

    }
    public void initializeTimerTask() {


        stageScreenY = 500;
        //stageScreenYDown;
        timerTask = new TimerTask() {


            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {

                    public void run() {
                        //stageScreenY-=5;
                        //stageScreenTop.setY(stageScreenY);
                        //stageScreenBottom.setY(-stageScreenY + 500);
//                        if(p1 != null){
//                            p1.spriteFrame.setX(p1.spriteFrame.getX() - 1);
//                        }

                        int x = (int)(p1.getX() + 76)/12;//13 min -76
                        int y = (int)(p1.getY() - 56)/12;//13 min 52
                       // playerLocationView.setText(x +"," + y + " " + mapGrid[x][y]);
                        for(Bullet b:bullets){
                            if(b.getBulletDirection() == Bullet.UP){
                                b.goUp();
                            }else if(b.getBulletDirection() == Bullet.DOWN){
                                b.goDown();
                            }else if(b.getBulletDirection() == Bullet.LEFT){
                                b.goLeft();
                            }else{
                                b.goRight();
                            }

                            if(b.hasReachedLimits()){
                                bullets.clear();
                                rl.removeView(b.spriteFrame);
                                MediaPlayer mediaPlayer= MediaPlayer.create(Game.this, R.raw.bullethitwall);
                                mediaPlayer.start();
                            }
                        }

                    }

                });
            }

        };
    }

    public boolean canMoveForward(){
        int x;
        int y;
//        if(Build.VERSION.SDK_INT <=15){
//            x = (int)(p1.getX() + 132)/13;//207
//            y = (int)(p1.getY() -47)/15;//387
//        }else{
            x = (int)((p1.getX() +132)/13.5);
            y = (int)((p1.getY() -49)/13.5);
        //}
        //boolean b =  mapGrid[y][x - 1] == 0;
        //Log.e("", p1X + "," + p1Y);
        playerLocationView.setText("XLoc" + x + ", YLoc" + y+ " " + mapGrid[y][x - 1] + " " +  "");
        //return mapGrid[p1X][p1Y - 1] != 0 || mapGrid[p1X][p1Y - 1] != 3;
        //return mapGrid[x][y - 1] == 0 || mapGrid[x][y - 1] == 3;
        return true;
    }
    public boolean canMoveBackward(){
        float x = p1.getX();
        float y = p1.getY();
        return true;
    }
    public boolean canMoveLeft(){
        float x = p1.getX();
        float y = p1.getY();
        return true;
    }
    public boolean canMoveRight(){
        float x = p1.getX();
        float y = p1.getY();
        return true;
    }

    public void connectToServer(){
            try {
                connectToServer = new Socket(ip, 3074);
                //connected = true;
                InputStreamReader streamReader = new InputStreamReader(connectToServer.getInputStream());
                reader = new BufferedReader(streamReader);
                writer = new PrintWriter(connectToServer.getOutputStream());
                writer.println("Hello from Android!");
                writer.flush();



            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("Error", "something happened maybe disconnected from server");
                //JOptionPane.showMessageDialog(null, "Error: disconnected from the server");
            }


    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String message;

            connectToServer();

            if(reader!= null) {
                while(true) {
                    try {

                        while ((message = reader.readLine()) != null) {

                            Log.e("Got player number?", message + " from server. Player number should be " + message);


                            String[] result = message.split(",");
                            if(initializing == true) {
                                playerNumber = Byte.parseByte(result[0]);
                                initializing = false;
                                Log.e("Got player number?", playerNumber + "player number");
                            }
                            //if(result.length == 1){
                              //  playerNumber = Byte.parseByte(result[0]);
                            //} else{
                            if(result.length < 1) {
                                handleOtherPlayerMovement(Byte.parseByte(result[0]), Byte.parseByte(result[1]));
                            }
                            //}



                        }
                    } catch (IOException | NumberFormatException ex) {
                        ex.printStackTrace();

                    }
                }
            }
        }

        private void handleOtherPlayerMovement(byte player,byte movement){

            if(player != playerNumber) {
                Player p;
                if (player == 1) {
                    p = p1;
                } else {
                    p = p2;
                }
                if (movement == Actor.UP) {
                    p.goUp();
                } else if (movement == Actor.DOWN) {
                    p.goDown();
                } else if (movement == Actor.LEFT) {
                    p.goLeft();
                } else {
                    p.goRight();
                }
            }
        }
    }



}
