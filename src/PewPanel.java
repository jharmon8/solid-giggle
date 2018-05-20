import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class PewPanel extends JPanel implements KeyListener, ActionListener {

    private Timer timer;

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private int sHeight = screenSize.height;
    private int sWidth = screenSize.width;
    private int sMargin = 0;

    private int railRadius = sHeight * 4 / 10;
    private int spawnRadius = railRadius * 3 / 5;

    private int numPlayers = 1;

    ArrayList<Player> players = new ArrayList<Player>();

    private boolean simulateProjectorAspectRatio = true;

    PewPanel () {
        Color[] playerColors = {Color.red, Color.blue, Color.white, Color.yellow, Color.green, Color.orange, Color.gray};

        if(simulateProjectorAspectRatio) {
            sWidth = screenSize.height * 4 / 3;
            sMargin = (screenSize.width - sWidth) / 2;
        }

        for(int i = 0; i < numPlayers; i++) {
            double rads = 6.28 / numPlayers * i;
            double size = sWidth / 32;

            players.add(new Player(rads, sWidth / 32, playerColors[i], railRadius));
        }

        addKeyListener(this);
//        setPreferredSize(new Dimension(sWidth, sHeight));

        timer = new Timer(1000, this);
        timer.setInitialDelay(4000);
        timer.start();

        setFocusable(true);
        requestFocus();

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        // margins and game area
        g.setColor(Color.gray);
        g.fillRect(0, 0, screenSize.width, screenSize.height);

        g.setColor(Color.black);
        g.fillRect(0 + sMargin, 0, sWidth, sHeight);

        // side panels
        g.setColor(Color.blue);
        int sidePanelWidth = sWidth / 8;
        int sidePanelHeight = sidePanelWidth * 3;
        int sidePanelYOffset = (sHeight - sidePanelHeight) / 2;

        g.fillRect(sMargin, sidePanelYOffset, sidePanelWidth, sidePanelHeight);
        g.fillRect(sMargin + sWidth - sidePanelWidth, sidePanelYOffset, sidePanelWidth, sidePanelHeight);

        // from now on, (0,0) is the middle of the screen
        g.translate(sWidth/2 + sMargin, sHeight/2);

        // Draw Player
        for(Player p : players) {
            p.draw(g);
        }


/*
        for(int i = 0; i < numPlayers; i++) {
            int playerX = (int)playerLocation(i).x;
            int playerY = (int)playerLocation(i).y;
            int playerSize = sWidth / 32;

            g.setColor(playerColors[i]);
            g.fillOval(sMargin + playerX - playerSize / 2 + sWidth / 2, playerY - playerSize / 2 + sHeight / 2, playerSize, playerSize);
        }
*/
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
                players.get(0).move(true);
                break;
            case KeyEvent.VK_S:
                break;
            case KeyEvent.VK_D:
                players.get(0).move(false);
                break;
            case KeyEvent.VK_SPACE:
                break;
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

}
