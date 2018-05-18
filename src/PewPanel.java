import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

public class PewPanel extends JPanel implements KeyListener, ActionListener {

    private Timer timer;

    private int sWidth = (int)(1024 * 2.5);
    private int sHeight = (int)(768 * 2.5);

    private int gameWidth = (int)(640 * 2.5);

    private int playerDegree = 0;

    PewPanel () {
        addKeyListener(this);
        setPreferredSize(new Dimension(sWidth, sHeight));

        timer = new Timer(1000, this);
        timer.setInitialDelay(4000);
        timer.start();

        setFocusable(true);
        requestFocus();

        repaint();
    }

    private Point2D playerLocation() {
        int x = (int)(Math.cos(playerDegree) * 100);
        int y = (int)(Math.sin(playerDegree) * 100);

        return new Point(x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        // Panel Test
        int avatarPanelWidth = (sWidth - gameWidth) / 2;
        int bottomPanelHeight = sHeight = gameWidth;

        g.setColor(Color.blue);
        g.fillRect(0, 0, avatarPanelWidth, gameWidth);
        g.fillRect(avatarPanelWidth + gameWidth, 0, avatarPanelWidth, gameWidth);

        g.setColor(Color.black);
        g.fillRect(avatarPanelWidth, 0, gameWidth, gameWidth);

        g.setColor(Color.red);
        g.fillRect(0, gameWidth, sWidth, bottomPanelHeight);
    }

    public void actionPerformed(ActionEvent ev) {
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
                break;
            case KeyEvent.VK_A:
                playerDegree++;
                break;
            case KeyEvent.VK_S:
                break;
            case KeyEvent.VK_D:
                playerDegree--;
                break;
            case KeyEvent.VK_SPACE:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
