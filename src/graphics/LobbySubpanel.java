package graphics;

import entity.Player;
import util.GameUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class LobbySubpanel implements Subpanel {
    private HashMap<Integer, Boolean> keysToPressed = new HashMap<>();

    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    PewPanel parent;

    private int numSecondsToWait = 10;
    public int ticksUntilNextGame;
    public int currentTicks;

    public boolean[] joined = new boolean[6];

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

        graphicsWrapper.drawImage("res/lobby.png", -gameWidth/2, -gameHeight/2, gameWidth, gameHeight);

        String time = (currentTicks * PewPanel.timerDelay / 1000) + "";
        graphicsWrapper.setColor(Color.black);
        graphicsWrapper.drawText(time, -4, 5, 8);

        double checkmarkWidth = gameWidth * 0.25;
        double checkmarkHeight = gameHeight * 0.3;

        double[] xPos = {-gameWidth/2, -gameWidth/6, gameWidth/6};
        double[] yPos = {-gameHeight/2, gameHeight*0.1};

        for(int i = 0; i < joined.length; i++) {
            if(joined[i]) {
                graphicsWrapper.drawImage("res/check.png", xPos[i % 3], yPos[i / 3], checkmarkWidth, checkmarkHeight);
            }
        }

        // lmao I made the lobby image so poorly that I need to hardcode the checkmarks for now
/*
        if(joined[0]) {
            graphicsWrapper.drawImage("res/check.png", -gameWidth/2, -gameHeight/2, checkmarkWidth, checkmarkHeight);
        }

        if(joined[1]) {
            graphicsWrapper.drawImage("res/check.png", -gameWidth/6, -gameHeight/2, checkmarkWidth, checkmarkHeight);
        }

        if(joined[2]) {
            graphicsWrapper.drawImage("res/check.png", gameWidth/6, -gameHeight/2, checkmarkWidth, checkmarkHeight);
        }

        if(joined[3]) {
            graphicsWrapper.drawImage("res/check.png", -gameWidth/2, 0, checkmarkWidth, checkmarkHeight);
        }

        if(joined[4]) {
            graphicsWrapper.drawImage("res/check.png", -gameWidth/6, 0, checkmarkWidth, checkmarkHeight);
        }

        if(joined[5]) {
            graphicsWrapper.drawImage("res/check.png", gameWidth/6, 0, checkmarkWidth, checkmarkHeight);
        }
*/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentTicks--;

        if(currentTicks <= 0) {
            int numPlayers = 0;
            for(int i = 0; i < joined.length; i++) {
                if(joined[i]) {
                    numPlayers++;
                }
            }

            if(numPlayers == 0) {
                parent.declareSubpanelFinished(MenuSubpanel.class);
            } else {
                parent.declareSubpanelFinished(GameSubpanel.class, numPlayers);
            }
        }
    }

    // check if anyone has joined
    public void checkKeys() {
        int[][] controls = GameUtils.getControls();

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
