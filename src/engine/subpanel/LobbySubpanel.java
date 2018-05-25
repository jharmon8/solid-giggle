package engine.subpanel;

import engine.util.AudioManager;
import engine.util.GraphicsWrapper;
import engine.PewPanel;
import engine.util.GameUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbySubpanel implements Subpanel {
    private HashMap<Integer, Boolean> keysToPressed = new HashMap<>();

    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    PewPanel parent;

    private int numSecondsToWait = 15;
    public int ticksUntilNextGame;
    public int currentTicks;

    public boolean[] joined = new boolean[6];

    private int[][] controls = GameUtils.getControls();

    private int prevTime = 9;

    public LobbySubpanel(int sWidth, int sHeight, PewPanel parent) {

        this.parent = parent;

        ticksUntilNextGame = numSecondsToWait * 1000 / PewPanel.timerDelay;
        currentTicks = ticksUntilNextGame;

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        AudioManager.stopAllSounds();
    }

    @Override
    public void paintComponent(Graphics g) {
        graphicsWrapper.setGraphics(g);

        graphicsWrapper.drawImage("res/new_lobby.png", -gameWidth/2, -gameHeight/2, gameWidth, gameHeight);

        String time = (currentTicks * PewPanel.timerDelay / 1000) + "";
        graphicsWrapper.setColor(Color.white);
        graphicsWrapper.drawText(time, -4, 5, 8);

        double checkmarkWidth = gameWidth * 0.25;
        double checkmarkHeight = gameHeight * 0.3;

        // Draw checkmarks
        double[] xPos = {-gameWidth/2, -gameWidth/6, gameWidth/6};
        double[] yPos = {-gameHeight/2, gameHeight*0.1};

        for(int i = 0; i < joined.length; i++) {
            if(joined[i]) {
                graphicsWrapper.drawImage("res/check.png", xPos[i % 3], yPos[i / 3], checkmarkWidth, checkmarkHeight);
            }
        }

        // Draw button pushes
        double[] xBtnOffsets = {12, 16, 20, 24, 8.5, 12.5, 16.5, 20.5, 6.7, 10.7, 14.7, 18.7};
        double[] yBtnOffsets = {gameWidth * 0.03, gameHeight * 0.275};

        for(int i = 0; i < joined.length; i++) {
            for(int btn = 0; btn < 4; btn++) {
                boolean pressed = false;
                if(keysToPressed.containsKey(controls[i][btn])) {
                    pressed = keysToPressed.get(controls[i][btn]);
                }

                double lineThickness = 0.4;

                // black background
                graphicsWrapper.setColor(Color.black);
                graphicsWrapper.fillCircle(xPos[i % 3] + xBtnOffsets[btn + (i % 3)*4], yPos[i / 3] + yBtnOffsets[i / 3], 3);

                // highlight center
                Color center = btn < 2 ? Color.red.darker().darker() : Color.green.darker().darker();
                graphicsWrapper.setColor(pressed ? center : Color.darkGray);
                graphicsWrapper.fillCircle(xPos[i % 3] + xBtnOffsets[btn + (i % 3)*4] + lineThickness, yPos[i / 3] + yBtnOffsets[i / 3] + lineThickness, 3 - lineThickness * 2);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentTicks--;

        int time = (currentTicks * PewPanel.timerDelay / 1000);
        if(time != prevTime) {
            if(time == 0) {
                AudioManager.playSound("res/count1.wav", -12f);
            } else {
                AudioManager.playSound("res/count0.wav", -15f);
            }
        }
        prevTime = time;

        if(currentTicks <= 0) {
            ArrayList<Integer> playerList = new ArrayList<>();
            for(int i = 0; i < joined.length; i++) {
                if(joined[i]) {
                    playerList.add(i);
                }
            }

            if(playerList.size() == 0) {
                parent.declareSubpanelFinished(MenuSubpanel.class);
            } else {
                parent.declareSubpanelFinished(GameSubpanel.class, playerList);
            }
        }
    }

    // check if anyone has joined
    public void checkKeys() {
        // go through every player
        for(int i = 0; i < controls.length; i++) {

            // check that player's 4 buttons
            boolean[] buttons = new boolean[4];
            for(int b = 0; b < 4; b++) {
                if(keysToPressed.containsKey(controls[i][b])) {
                    buttons[b] = keysToPressed.get(controls[i][b]);
                }
            }

            // if all the buttons are pushed
            if(buttons[0] && buttons[1] && buttons[2] && buttons[3]) {
                // then that player joins the game
                join(i);
            }
        }
    }

    // player #playerNum joins the game
    public void join(int playerNum) {
        if(!joined[playerNum]) {
            AudioManager.playSound("res/Blip 002.wav", -10);
        }

        joined[playerNum] = true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), true);

        checkKeys();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void close() {}
}
