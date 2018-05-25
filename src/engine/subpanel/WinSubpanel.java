package engine.subpanel;

import engine.util.*;
import engine.PewPanel;
import engine.util.GraphicsWrapper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class WinSubpanel implements Subpanel {
    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    PewPanel parent;

    private int numSecondsToWait = 30;
    public int ticksUntilNextGame;
    public int currentTicks;

    private int score;

    private DecimalFormat scoreFormatter = new DecimalFormat("###,###");

    public WinSubpanel(int sWidth, int sHeight, int score, PewPanel parent) {
        ticksUntilNextGame = numSecondsToWait * 1000 / PewPanel.timerDelay;
        currentTicks = ticksUntilNextGame;

        this.score = score;
        this.parent = parent;

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        AudioManager.stopAllSounds();
        AudioManager.playSound("res/lose.wav", -8f);
    }

    @Override
    public void paintComponent(Graphics g) {
        graphicsWrapper.setGraphics(g);

        graphicsWrapper.drawImage("res/new_victory.png", -gameWidth/2, -gameHeight/2, gameWidth, gameHeight);

        graphicsWrapper.setColor(Color.white);
        graphicsWrapper.drawText("Score: " + scoreFormatter.format(score), -gameWidth*0.49, gameHeight * 0.48, 4, false);

        String time = (currentTicks * PewPanel.timerDelay / 1000) + "";
        graphicsWrapper.drawText(time, gameWidth * 0.44, gameHeight * 0.48, 4, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentTicks--;

        if(currentTicks <= 0) {
            parent.declareSubpanelFinished(MenuSubpanel.class);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void close() {

    }
}
