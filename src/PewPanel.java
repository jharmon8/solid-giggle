import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

public class PewPanel extends JPanel implements KeyListener, ActionListener {

    private Timer timer;

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private int sHeight = screenSize.height;
    private int sWidth = screenSize.width;
    private int sMargin = 0;

    private int railRadius = sHeight * 4 / 10;
    private int spawnRadius = railRadius * 3 / 5;

    private int numPlayers = 3;

    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Entity> enemies = new ArrayList<Entity>();
    ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private boolean simulateProjectorAspectRatio = true;

    private HashMap<Player, int[]> playersToKeys = new HashMap<>();
    private HashMap<Integer, Boolean> keysToPressed = new HashMap<>();

    // These are the default controls for each player
    // I guess right now the order is shoot, swap, left, right
    private int[][] defaultControls = {
            {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R}, // player 1
            {KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F}, // player 2
            {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V}, // etc
    };

    PewPanel () {
        Color[] playerColors = {
                Color.red,      // player 1
                Color.green,    // player 2
                Color.blue,     // etc
                Color.yellow,
                Color.pink,
                Color.orange,
                Color.gray
        };

        if(simulateProjectorAspectRatio) {
            sWidth = screenSize.height * 4 / 3;
            sMargin = (screenSize.width - sWidth) / 2;
        }

        for(int i = 0; i < numPlayers; i++) {
            double rads = 6.28 / numPlayers * i;
            double size = sWidth / 32;

            Player p = new Player(rads, sWidth / 32, playerColors[i], railRadius);

            players.add(p);
            playersToKeys.put(p, defaultControls[i]);
        }

        addKeyListener(this);

        if(!PewPewDriver.FULLSCREEN) {
            setPreferredSize(new Dimension(sWidth, sHeight));
            sMargin = 0;
        }

/*
        Projectile test = new BasicBullet(0,0,10,0,Color.green, players.get(0));
        projectiles.add(test);
*/

        timer = new Timer(50, this);
        timer.setInitialDelay(1000);
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

        // Draw bullets
        for(Projectile p : projectiles) {
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

        // Take player input
        for(Player p : players) {
            int leftMoveKey = playersToKeys.get(p)[2];
            boolean shouldMoveLeft = false;

            if(keysToPressed.containsKey(leftMoveKey)) {
                shouldMoveLeft = keysToPressed.get(leftMoveKey);
            }

            int rightMoveKey = playersToKeys.get(p)[3];
            boolean shouldMoveRight = false;

            if(keysToPressed.containsKey(rightMoveKey)) {
                shouldMoveRight = keysToPressed.get(rightMoveKey);
            }

            int fireKey = playersToKeys.get(p)[0];
            boolean shouldFire = false;

            if(keysToPressed.containsKey(fireKey)) {
                shouldFire = keysToPressed.get(fireKey);
            }

            if(shouldMoveLeft && !shouldMoveRight) {
                p.move(false);
            }
            if(!shouldMoveLeft && shouldMoveRight) {
                p.move(true);
            }

            if(shouldFire) {
                Projectile newP = p.fire();

                if(newP != null) {
                    projectiles.add(newP);
                }
            }

            p.update();
        }

        // update projectiles
        ArrayList<Projectile> projToRemove = new ArrayList<>();
        for(Projectile proj : projectiles) {
            for(Player player : players) {
                if (player.collides(proj)) {
                    proj.onCollide(player);
                }
            }

            proj.update();

            if(!proj.onScreen(sWidth, sHeight)) {
                projToRemove.add(proj);
                continue;
            }
        }

        // remove any projectiles that have left the screen
        for(Projectile deleteMe : projToRemove) {
            projectiles.remove(deleteMe);
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), true);
/*
        switch(e.getKeyCode()) {
            case KeyEvent.VK_Q:
                break;
            case KeyEvent.VK_W:
                players.get(0).move(true);
                break;
            case KeyEvent.VK_E:
                break;
            case KeyEvent.VK_R:
                players.get(0).move(false);
                break;
            case KeyEvent.VK_SPACE:
                break;
        }
*/

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

}
