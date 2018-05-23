package engine.subpanel;

import engine.util.AudioManager;
import engine.util.GameUtils;
import engine.util.GraphicsWrapper;
import engine.PewPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/*
 * Ahhh... Ye ol' game over screen
 */
public class LoseSubpanel implements Subpanel {
    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    PewPanel parent;

    private int numSecondsToWait = 10;
    public int ticksUntilNextGame;
    public int currentTicks;

    public LoseSubpanel(int sWidth, int sHeight, PewPanel parent) {

        this.parent = parent;

        ticksUntilNextGame = numSecondsToWait * 1000 / PewPanel.timerDelay;
        currentTicks = ticksUntilNextGame;

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        AudioManager.stopAllSounds();
        AudioManager.playSound("res/lose.wav", -8f);
    }

    @Override
    public void paintComponent(Graphics g) {
        graphicsWrapper.setGraphics(g);

        graphicsWrapper.drawImage("res/you_died.jpg", -gameWidth/2, -gameHeight/2, gameWidth, gameHeight);

        String time = (currentTicks * PewPanel.timerDelay / 1000) + "";
        graphicsWrapper.setColor(Color.white);
        graphicsWrapper.drawText(time, gameWidth * 0.4, gameHeight * 0.45, 8, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentTicks--;

        if(currentTicks <= 0) {
            parent.declareSubpanelFinished(MenuSubpanel.class);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void close() {}
}
