package graphics;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public interface Subpanel {
    public void paintComponent(Graphics g);

    public void actionPerformed(ActionEvent e);

    public void keyPressed(KeyEvent e);
    public void keyReleased(KeyEvent e);
    public void keyTyped(KeyEvent e);

    public void close();
}
