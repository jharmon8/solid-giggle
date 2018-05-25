package engine.subpanel;

import engine.util.AudioManager;
import engine.util.GraphicsWrapper;
import engine.PewPanel;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MenuSubpanel implements Subpanel {
    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    PewPanel parent;

    public MenuSubpanel(int sWidth, int sHeight, PewPanel parent) {
        this.parent = parent;

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        AudioManager.playSound("res/short_song.wav", -15f);
    }

    @Override
    public void paintComponent(Graphics g) {
        graphicsWrapper.setGraphics(g);

        graphicsWrapper.drawImage("res/new_main_menu.png", -gameWidth/2, -gameHeight/2, gameWidth, gameHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        parent.declareSubpanelFinished(InstructionsSubpanel.class);
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
