package graphics;

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

        AudioManager.playMedia("res/song.mp3");
    }

    @Override
    public void paintComponent(Graphics g) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            parent.declareSubpanelFinished();
        }
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
