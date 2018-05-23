package engine.subpanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ErrorSubpanel implements Subpanel {
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(-10000, -10000, 20000, 20000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

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
