package graphics;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/*
 * Ahhh... Ye ol' game over screen
 */
public class LoseSubpanel implements Subpanel {
    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    PewPanel parent;

    public int ticksUntilNextGame = 250;
    public int currentTicks = ticksUntilNextGame;

    public LoseSubpanel(int sWidth, int sHeight, PewPanel parent) {
        currentTicks = ticksUntilNextGame;
        this.parent = parent;

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        AudioManager.stopAllSounds();
    }

    @Override
    public void paintComponent(Graphics g) {
        graphicsWrapper.setGraphics(g);

        graphicsWrapper.drawImage("res/lobby.png", -gameWidth/2, -gameHeight/2, gameWidth, gameHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentTicks--;

        if(currentTicks <= 0) {
            parent.declareSubpanelFinished(LobbySubpanel.class);
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
