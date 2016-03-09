package com.example.chris.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.net.Socket;

/**
 * Created by Chris on 09/02/2016.
 */
public class OnlineScreen {
    private Game game;
    private TextView startOnlineGameView, player1View, player1IPView, player2View, player2IPView;
    private Button connectToServerButton, startGameButton, cancelButton;
    private Socket connectToServer;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;

    public OnlineScreen(Game game){
        this.game = game;
        //setButtons();
    }

    private void setOnlineButtons(){
        //startOnlineGameView = (TextView) findViewById(R.id.startOnlineGameView);
        //player1View
        game.setContentView(R.layout.online_screen);
        player1IPView = (TextView) game.findViewById(R.id.player1IPView);
        player1IPView.setTextColor(050505);
        player2IPView = (TextView) game.findViewById(R.id.player2IPView);
        player1IPView.setTextColor(050505);
        connectToServerButton = (Button) game.findViewById(R.id.connectToServerButton);
        connectToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServerButton.setBackgroundColor(8);
                setupNetworking();
            }
        });
        startGameButton = (Button) game.findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
        cancelButton = (Button) game.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.setContentView(R.layout.activity_game);
                cancelButton.setText("clicked");
            }
        });
    }

    public void startGame() {

    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String message;
            try {
                if (reader != null) {
                    while ((message = reader.readLine()) != null) {
                        //System.out.println(readerThread.isAlive() + " after reader");
                        System.out.println(message);
                        String[] result = reader.readLine().split(" ");
                        try{

                            int player = Integer.parseInt(result[0]);
                            System.out.println(player + "player number");
                            if(player == 1){
                                //playerOneIPLabel.setText(result[1]);
                            }else if(player == 2){
                                //playerTwoIPLabel.setText(result[1]);
                                startGameButton.setEnabled(true);
                            }
                        }catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
                            if(result[0].equalsIgnoreCase("start")){
                                startGame();
                            }
                            System.out.println(result);
                        }


                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(readerThread.isAlive() + "failed");
            }
        }
    }

    public void setupNetworking() {
        try {
            connectToServer = new Socket(game.ip, 3074);
            InputStreamReader streamReader = new InputStreamReader(connectToServer.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(connectToServer.getOutputStream());
//            System.out.println("Networking established");
//            System.out.println("New Game Created");
//            connectToServerButton.setBackground(connectionColor);
//            connectToServerButton.setText("Connected!");
//            connectToServerButton.setEnabled(false);
//            playerOneIPLabel.setText("searching...");
//            playerTwoIPLabel.setText("searching...");
            readerThread = new Thread(new IncomingReader());
            readerThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Error: Could not connect to server");

        }
    }

}
