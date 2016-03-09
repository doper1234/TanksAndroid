package com.example.chris.game;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
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
    private final Handler handler = new Handler();
    Button start1PGameButton, startOnlineGameButton;
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
    TextView textViewStage, playerLocationView, battleLabel, cityLabel;
    ArrayList<Bullet> bullets;
    MediaPlayer mp;
    int[][] mapGrid;
    ArrayList<Water> waters;
    ArrayList<Enemy> enemies;
    final int BRICKS = 1;
    final int STEEL = 2;
    final int TREES = 3;
    final int FLAG = 4;
    final int WATER = 5;
    final int ICE = 6;
    final int PLAYER_SPAWN_POINT = 8;
    final int BLANK = 0;

    Socket connectToServer;
    BufferedReader reader;
    PrintWriter writer;
    Thread readerThread;
    //String ip = "85.159.209.9";//linode
    String ip = "192.168.1.103";
    String player1IP= "searching...", player2IP = "searching...";
    int searchingCount = 0;
    Timer searchingTimer;
    TimerTask searchingTimerTask;
    boolean player1Found = false;
    boolean player2Found = false;

    boolean connected = false;
    boolean initializing = true;
    byte playerNumber;

    int blankImage,waterImage, waterImage2, waterImage3,brickImage,steelImage,treeImage,borderImage,iceImage,flagImage,
            p1Image,p2Image,e1Image,e2Image,e3Image,e4Image,
            p1bImage,p2bImage,e1bImage,e2bImage,e3bImage,e4bImage;

    private TextView startOnlineGameView, player1View, player1IPView, player2View,
            player2IPView;
    private Button connectToServerButton, startGameButton, cancelButton;
    private ImageView player1ImageView, player2ImageView;
    private boolean stopped;
    private boolean startGame = false;
    private boolean gameStarted = false;

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
        mp = MediaPlayer.create(this, R.raw.move);

        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        setButtons();
        stageScreenY = start1PGameButton.getX();
    }

    private void setButtons(){
        String fontPath = "fonts/emulogic.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        bullets = new ArrayList<>();
        player1ImageView = (ImageView) findViewById(R.id.player1ImageView);
        player1ImageView.setVisibility(View.INVISIBLE);
        player1View = (TextView) findViewById(R.id.player1View);
        player1View.setVisibility(View.INVISIBLE);
        player1View.setTypeface(tf);
        player1IPView = (TextView) findViewById(R.id.player1IPView);
        player1IPView.setVisibility(View.INVISIBLE);
        player1IPView.setTypeface(tf);
        player2ImageView = (ImageView) findViewById(R.id.player2ImageView);
        player2ImageView.setVisibility(View.INVISIBLE);
        player2View = (TextView) findViewById(R.id.player2View);
        player2View.setVisibility(View.INVISIBLE);
        player2View.setTypeface(tf);
        player2IPView = (TextView) findViewById(R.id.player2IPView);
        player2IPView.setVisibility(View.INVISIBLE);
        player2IPView.setTypeface(tf);
        textViewStage = (TextView) findViewById(R.id.textViewStage);
        textViewStage.setText("Stage " + mapNumber);
        textViewStage.setVisibility(View.INVISIBLE);
        playerLocationView = (TextView) findViewById(R.id.playerLocationView);
        playerLocationView.setText(width + " " + height);
        battleLabel = (TextView) findViewById(R.id.battleLabel);
        battleLabel.setTypeface(tf);
        cityLabel  = (TextView) findViewById(R.id.cityLabel);
        cityLabel.setTypeface(tf);
        upBtn = (ImageButton) findViewById(R.id.imageButtonUp);
        upBtn.setVisibility(View.INVISIBLE);
        upBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getApplicationContext(), event.getAction() + " ", Toast.LENGTH_LONG);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //pMe.goUp();
                    pMe.isUp = true;
                    if (!mp.isPlaying()) {
                        mp.start();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                    pMe.isUp = false;
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        downBtn = (ImageButton) findViewById(R.id.imageButtonDown);
        downBtn.setVisibility(View.INVISIBLE);
        downBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //pMe.goUp();
                    pMe.isDown = true;
                    if (!mp.isPlaying()) {
                        mp.start();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                    pMe.isDown = false;
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
//        downBtn.setOnHoverListener(new View.OnHoverListener() {
//            @Override
//            public boolean onHover(View v, MotionEvent event) {
//                pMe.goDown();
//                if (!mp.isPlaying()) {
//                    mp.start();
//                }
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mp.pause();
//                }
//                return event.getDeviceId() == R.id.imageButtonUp;
//            }
//        });
        leftBtn = (ImageButton) findViewById(R.id.imageButtonLeft);
        leftBtn.setVisibility(View.INVISIBLE);
        leftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //pMe.goUp();
                    pMe.isLeft = true;
                    if (!mp.isPlaying()) {
                        mp.start();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                    pMe.isLeft = false;
                }
                return event.getDeviceId() == R.id.imageButtonUp;
            }
        });
        rightBtn = (ImageButton) findViewById(R.id.imageButtonRight);
        rightBtn.setVisibility(View.INVISIBLE);
        rightBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //pMe.goUp();
                    pMe.isRight = true;
                    if (!mp.isPlaying()) {
                        mp.start();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mp.pause();
                    pMe.isRight = false;
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
                p1.shoot();
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
        start1PGameButton.setTypeface(tf);
        start1PGameButton.requestFocus();
        startOnlineGameButton = (Button) findViewById(R.id.onlineButton);
        startOnlineGameButton.setTypeface(tf);
        startOnlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                readerThread = new Thread(new IncomingReader());
//                readerThread.start();
//                setControllerButtonsVisible();
//                setupTanksPlayArea();
//                startTimer();
                setOnlineScreen();

            }
        });
        if(Build.VERSION.SDK_INT <= 15){//Janek tab

            resolution = 1.8f; //needs to be set based on screen size, for emulator 1, tablet 1.5
            grid = resolution * 8; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
            battleLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,70f);
            cityLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP,70f);
            start1PGameButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20f);
            startOnlineGameButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20f);

        }

        float textSize = (width/20);
        float smallTextSize = (width/30);
        resolution = 0.5f; //needs to be set based on screen size, for emulator 1, tablet 1.5
        grid = resolution * 16; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
        battleLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        cityLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        start1PGameButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,smallTextSize);
        startOnlineGameButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, smallTextSize);
    }

    private void setOnlineButtons(){
        //startOnlineGameView = (TextView) findViewById(R.id.startOnlineGameView);
        //player1View
        final OnlineScreen onlineScreen = new OnlineScreen(this);
        setContentView(R.layout.online_screen);
        player1IPView = (TextView) findViewById(R.id.player1IPView);
        player1IPView.setTextColor(050505);
        player2IPView = (TextView) findViewById(R.id.player2IPView);
        player2IPView.setTextColor(050505);
        connectToServerButton = (Button) findViewById(R.id.connectToServerButton);
        connectToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServerButton.setBackgroundColor(8);
                startGameButton.setEnabled(true);
                onlineScreen.setupNetworking();
            }
        });
        startGameButton = (Button) findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineScreen.startGame();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_game);
                setButtons();
                cancelButton.setText("clicked");
            }
        });
    }

    private void setControllerButtonsVisible(){
        battleLabel.setVisibility(View.INVISIBLE);
        cityLabel.setVisibility(View.INVISIBLE);
        textViewStage.setVisibility(View.VISIBLE);
        upBtn.setVisibility(View.VISIBLE);
        downBtn.setVisibility(View.VISIBLE);
        leftBtn.setVisibility(View.VISIBLE);
        rightBtn.setVisibility(View.VISIBLE);
        centerBtn.setVisibility(View.VISIBLE);
        shootBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
        start1PGameButton.setVisibility(View.GONE);
        startOnlineGameButton.setVisibility(View.GONE);
    }

    private void setOnlineScreen(){
        final OnlineScreen onlineScreen = new OnlineScreen(this);
        connectToServerButton = start1PGameButton;
        final Button cancelButton = startOnlineGameButton;
        player1ImageView.setVisibility(View.VISIBLE);
        player1View.setVisibility(View.VISIBLE);
        player1IPView.setVisibility(View.VISIBLE);
        player2ImageView.setVisibility(View.VISIBLE);
        player2View.setVisibility(View.VISIBLE);
        player2IPView.setVisibility(View.VISIBLE);
        cityLabel.setText("city online");
        connectToServerButton.setText("connect to server");
        //connectToServerButton.setBackgroundColor(getResources().getColor(R.color.noConnectionColor));//6553600 // red 65280// green
        connectToServerButton.setTextColor(getResources().getColor(R.color.noConnectionColor));
        connectToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onlineScreen.setupNetworking();
                //connectToServerButton.setBackgroundColor(getResources().getColor(R.color.connectedColor));
                readerThread = new Thread(new IncomingReader());
                readerThread.start();
                startSearchingTimer();
//                setControllerButtonsVisible();
//                setupTanksPlayArea();
//                startTimer();
//                if (connected) {
//                    connectToServerButton.setTextColor(getResources().getColor(R.color.connectedColor));
//                    connectToServerButton.setText("connected!");
//                    player1IPView.setText(player1IP);
//                    player2IPView.setText(player2IP);
//                } else {
//                    connectToServerButton.setText("Error. Try again?");
//                }

            }
        });
        cancelButton.setText("back");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onlineScreen.setupNetworking();
                //connectToServerButton.setBackgroundColor(getResources().getColor(R.color.colorBack));
                closeConnection();
                connectToServerButton.setTextColor(getResources().getColor(R.color.buttonColor));
                connectToServerButton.setText("one player game");
                cancelButton.setText("online");
                cityLabel.setText("city");
                player1IPView.setText("not connected");
                player2IPView.setText("not connected");
                player1Found = false;
                player2Found = false;
                //hidePlayerIPWeirdness();
                setButtons();
            }
        });


    }

    private void closeConnection(){
        stopSearchingTimerTask();
        connected = false;
        stopped = true;
        try {
            if(connectToServer != null){
                connectToServer.close();
            }
        } catch (IOException e) {

        }
    }
    private void hidePlayerIPWeirdness(){
        player1ImageView.setVisibility(View.INVISIBLE);
        player1View.setVisibility(View.INVISIBLE);
        player1IPView.setVisibility(View.INVISIBLE);
        player2ImageView.setVisibility(View.INVISIBLE);
        player2View.setVisibility(View.INVISIBLE);
        player2IPView.setVisibility(View.INVISIBLE);
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
//        Display display = getWindowManager().getDefaultDisplay();
//        float width = display.getWidth();
//        float height = display.getHeight();
        textViewStage.setText(size + ""/*+ " size" + stageWidth +", " + stageHeight*/);

        if(width <=800) {//Emulator Tablet

            resolution = 0.9f;
            grid = resolution * 16f;
            loadSmallImages();

        } else if(width == 960){
            resolution = 0.2f;
            grid = resolution * 70;
            loadLargeImages();

        }

        else if(width > 800){//Samsungtablet
            resolution = 0.5f;
            grid = resolution * 96;
            loadLargeImages();

//            resolution = 1.5f;
//            grid = resolution *32;
//            loadSmallImages();
        }
        else{//Janek tablet

            resolution = 1.5f; //needs to be set based on screen size, for emulator 1, tablet 1.5
            grid = resolution * 8; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
            loadSmallImages();

        }

        if(Build.VERSION.SDK_INT <= 15){//Janek tab

            resolution = 1.8f; //needs to be set based on screen size, for emulator 1, tablet 1.5
            grid = resolution * 8; // also needs to be set based on screen size, for emulator *16, tablet *8, res + 1.6
            battleLabel.setText("why");
            battleLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP ,20f);
            cityLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20f);
        }

        textViewStage.setText("API " +Build.VERSION.SDK_INT);
        startNewStageAnimation();
        //getMap();

    }

    private void loadSmallImages(){
        blankImage = R.drawable.blank;
        waterImage = R.drawable.water;
        waterImage2= R.drawable.water2;
        waterImage3= R.drawable.water3;
        brickImage = R.drawable.bricks;
        steelImage = R.drawable.steel;
        treeImage = R.drawable.trees;
        borderImage = R.drawable.border;
        iceImage = R.drawable.ice;
        flagImage = R.drawable.flag;
        p1Image = R.drawable.p1;
        p2Image = R.drawable.p2;
        e1Image = R.drawable.e1;
        e2Image = R.drawable.e2;
        e3Image = R.drawable.e3;
        e4Image = R.drawable.e4;
        p1bImage = R.drawable.p1b;
        p2bImage = R.drawable.p2b;
        e1bImage = R.drawable.e1b;
        e2bImage = R.drawable.e2b;
        e3bImage = R.drawable.e3b;
        e4bImage = R.drawable.e4b;
    }

    private void loadLargeImages(){
        blankImage = R.drawable.blankhd;
        waterImage = R.drawable.water1hd;
        waterImage2= R.drawable.water2hd;
        waterImage3= R.drawable.water3hd;
        brickImage = R.drawable.brickshd;
        steelImage = R.drawable.steelhd;
        treeImage = R.drawable.treeshd;
        borderImage = R.drawable.borderhd;
        iceImage = R.drawable.icehd;
        flagImage = R.drawable.flaghd;
        p1Image = R.drawable.p1hd;
        p2Image = R.drawable.p2hd;
        e1Image = R.drawable.e1hd;
        e2Image = R.drawable.e2hd;
        e3Image = R.drawable.e3hd;
        e4Image = R.drawable.e4hd;
        p1bImage = R.drawable.p1bhd;
        p2bImage = R.drawable.p2bhd;
        e1bImage = R.drawable.e1bhd;
        e2bImage = R.drawable.e2bhd;
        e3bImage = R.drawable.e3bhd;
        e4bImage = R.drawable.e4bhd;
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

        waters = new ArrayList<>();
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
                    boxView.setImageResource(borderImage);

                    if(j == 0){
                        boxView.setY(((y + grid) + i * grid) - grid);
                        boxView.setX((x - grid) + j * grid);
                    }else {
                        boxView.setY(((y + grid) + i * grid) - grid);
                        boxView.setX((x + grid) + j * grid);
                    }//draw border


                    boxView.setScaleX(resolution);
                    boxView.setScaleY(resolution);
                    rl.addView(boxView,lp);
                }
                if(i == 0 || i == mapGrid.length -1){
                    ImageView boxView = new ImageView(this);
                    boxView.setImageResource(borderImage);

                    if(i == 0){
                        boxView.setX(((x + grid) + j * grid) - grid);
                        boxView.setY((y - grid) + i * grid);
                    }else {
                        boxView.setX(((x + grid) + j * grid) - grid);
                        boxView.setY((y + grid) + i * grid);
                    }//draw border


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
             //       mapTerrainView.setImageResource(blankImage);
                }
                else if (mapGrid[i][j] == BRICKS) {
                    mapTerrainView.setImageResource(brickImage);
                } else if (mapGrid[i][j] == STEEL) {
                    mapTerrainView.setImageResource(brickImage);
                } else if(mapGrid[i][j] == TREES){
                    mapTerrainView.setImageResource(treeImage);
                    mapTerrainView.bringToFront();
                } else if(mapGrid[i][j] == FLAG){
                    mapTerrainView.setImageResource(flagImage);
                    mapTerrainView.setScaleX(resolution*2);
                    mapTerrainView.setScaleY(resolution * 2);
                    mapTerrainView.setX((x + grid/2) + j * grid);
                    mapTerrainView.setY((y + grid/2) + i * grid);
                } else if(mapGrid[i][j] == WATER){
                    mapTerrainView.setImageResource(waterImage);
                    waters.add(new Water(this, x + j * grid, y + i * grid, waterImage, waterImage2, waterImage3));

                }else if(mapGrid[i][j] == ICE) {
                    mapTerrainView.setImageResource(iceImage);
                } else if(mapGrid[i][j] == PLAYER_SPAWN_POINT){
                    if(playerNumber == 1){

                        p1 = new Player(this,(x + grid/2) + j * grid, (y + grid/2) + i * grid, (byte)1 , p1Image,p1bImage);
                        p2 = new Player(this,(x + grid) + j * grid, (y + grid) + i * grid, (byte)2, p2Image,p2bImage);
                        pMe = p1;
                    }else if (playerNumber == 2){
                        p1 = new Player(this,(x + grid/2) + j * grid, (y + grid/2) + i * grid, (byte)2 , p1Image,p1bImage);
                        p2 = new Player(this,(x + grid) + j * grid, (y + grid) + i * grid, (byte)1, p2Image,p2bImage);
                        pMe = p2;
                    }else{
                        p1 = new Player(this,(x + grid/2) + j * grid, (y + grid/2) + i * grid, (byte)1 , p1Image,p1bImage);
                        pMe = p1;
                    }
                    Log.e("Player number:", "Should be 2 if its two: " + playerNumber);

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
        addEnemies();

    }
    private void addEnemies(){

        enemies = new ArrayList<>();
        enemies.add(new Enemy(this, gameX, gameY, 1, e1Image, e1bImage));
        enemies.add(new Enemy(this, gameX + grid*resolution*8, gameY, 2, e2Image, e2bImage));
        enemies.add(new Enemy(this, gameX + grid * resolution * 16, gameY, 3, e3Image, e3bImage));
        enemies.add(new Enemy(this, gameX + grid * resolution * 4, gameY, 4, e4Image, e4bImage));
        for(Enemy e : enemies){
            e.spriteFrame.setScaleX(resolution*2);
            e.spriteFrame.setScaleY(resolution * 2);
            rl.addView(e.spriteFrame,lp);
        }

    }
    private void loadMap(){
        int x = (int)width/2 -(int)(1.5 * grid*26);
        //int x = gameX;
        //int y = gameY;
        int y = (int)(grid * 1.5);
        for(Water w : waters){
            w.spriteFrame.setImageResource(blankImage);
        }
        waters.clear();
        mapGrid = getMap(mapNumber);
        for(int i = 0; i < mapGrid.length; i++){
            for(int j = 0; j < mapGrid.length; j++) {
                ImageView mapTerrainView = mapTerrainViews[i][j];

             //   mapTerrainView.setScaleX(resolution);
              //  mapTerrainView.setScaleY(resolution);

                if(mapGrid[i][j] == 0){
                    mapTerrainView.setImageResource(blankImage);
                }
                else if (mapGrid[i][j] == 1) {
                    mapTerrainView.setImageResource(brickImage);
                } else if (mapGrid[i][j] == 2) {
                    mapTerrainView.setImageResource(steelImage);
                } else if(mapGrid[i][j] == 3){
                    mapTerrainView.setImageResource(treeImage);
                } else if (mapGrid[i][j] == 4){
                    mapTerrainView.setImageResource(flagImage);
                //    mapTerrainView.setScaleX(resolution*2);
                //    mapTerrainView.setScaleY(resolution * 2);

                } else if(mapGrid[i][j] == 5){
                    //mapTerrainView.setImageResource(R.drawable.water);
                    waters.add(new Water(this, x + j * grid, y + i * grid, waterImage, waterImage2, waterImage3));


                }else if(mapGrid[i][j] == 6) {
                    mapTerrainView.setImageResource(iceImage);
                }

            }

        }
        for(Water w: waters){
            w.spriteFrame.setScaleX(resolution);
            w.spriteFrame.setScaleY(resolution);
            rl.addView(w.spriteFrame, lp);
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
    public void startSearchingTimer(){
        searchingTimer = new Timer();
        initializeSearchingTimerTask();
        searchingTimer.schedule(searchingTimerTask, 5, 10);
    }
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 50, 100); //

    }
    public void stopSearchingTimerTask() {
        //stop the timer, if it's not already null
        if (searchingTimer != null) {

            searchingTimer.cancel();
            searchingTimer = null;
        }

    }
    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {

            timer.cancel();
            timer = null;
        }

    }
    public void initializeSearchingTimerTask(){
        final int searchingCountInterval = 50;

        searchingTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        if (connected && !startGame) {
                            connectToServerButton.setTextColor(getResources().getColor(R.color.connectedColor));
                            connectToServerButton.setText("connected!");
                            if(!player1Found){
                                if(searchingCount / searchingCountInterval == 0){
                                    player1IP = "searching";
                                }else if(searchingCount / searchingCountInterval == 1){
                                    player1IP = "searching.";
                                }else if(searchingCount / searchingCountInterval == 2){
                                    player1IP = "searching..";
                                }else if(searchingCount / searchingCountInterval == 3){
                                    player1IP = "searching...";
                                }else if(searchingCount / searchingCountInterval == 4){
                                    searchingCount = 0;
                                }
                            }
                            player1IPView.setText(player1IP);
                            if(!player2Found){
                                if(searchingCount / searchingCountInterval == 0){
                                    player2IP = "searching";
                                }else if(searchingCount / searchingCountInterval == 1){
                                    player2IP = "searching.";
                                }else if(searchingCount / searchingCountInterval == 2){
                                    player2IP = "searching..";
                                }else if(searchingCount / searchingCountInterval == 3){
                                    player2IP = "searching...";
                                }
                            }
                            player2IPView.setText(player2IP);
                        } else if(!stopped && !startGame) {
                            connectToServerButton.setText("Error. Try again?");
                        } else if(startGame && !gameStarted){
                            setControllerButtonsVisible();
                            setupTanksPlayArea();
                            startTimer();
                            gameStarted = true;

                        }
                        searchingCount++;
                    }

                });
            }

        };
    }
    public void initializeTimerTask() {


        stageScreenY = 500;
        //stageScreenYDown;
        timerTask = new TimerTask() {


            public void run() {
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
                        Water.update++;
                        for(Water w: waters){
                            w.update();
                        }
                        for(Enemy e:enemies){
                            e.update();
                        }
                        p1.update();


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
        //playerLocationView.setText("XLoc" + x + ", YLoc" + y+ " " + mapGrid[y][x - 1] + " " +  "");
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
                connected = true;
                InputStreamReader streamReader = new InputStreamReader(connectToServer.getInputStream());
                reader = new BufferedReader(streamReader);
                writer = new PrintWriter(connectToServer.getOutputStream());
                //writer.println("Hello from Android!");
                //writer.flush();


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("Error", "something happened maybe disconnected from server");
                //JOptionPane.showMessageDialog(null, "Error: disconnected from the server");
            }


    }

    public class IncomingReader implements Runnable {

        boolean waitingForAnotherPlayer = true;
        @Override
        public void run() {
            String message;

            connectToServer();

            if(reader!= null) {
                while(true) {

                    try {
                        if(waitingForAnotherPlayer){
                            while ((message = reader.readLine()) != null) {
                                String[] result = message.split(" ");
                                Log.e("From server: ", message);
                                if(result.length > 1){
                                    if(Integer.parseInt(result[0]) == 1){
                                        player1IP = result[1];
                                        player1Found = true;
                                    }else{
                                        player2IP =(result[1]);
                                        player2Found = true;
                                    }
                                }else{
                                    if(result[0].equals("start")){
                                        startGame = true;
                                    }
                                }
                            }
                        }else {


                                while ((message = reader.readLine()) != null) {
                                    Log.e("Got player number? ", message + " from server. Player number should be " + message);
                                    String[] result = message.split(",");
                                    if (initializing == true) {
                                        playerNumber = Byte.parseByte(result[0]);
                                        initializing = false;
                                        Log.e("Got player number?", playerNumber + "player number");
                                    }
                                    //if(result.length == 1){
                                    //  playerNumber = Byte.parseByte(result[0]);
                                    //} else{
                                    if (result.length == 2) {
                                        handleOtherPlayerMovement(Byte.parseByte(result[0]), Byte.parseByte(result[1]));
                                    }
                                    //}
                                }

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
