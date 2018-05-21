package graphics;

import entity.Entity;
import entity.Player;
import entity.enemy.BasicEnemy;
import entity.projectile.Projectile;
import util.GameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PewPanel extends JPanel implements KeyListener, ActionListener {

    private Timer timer;

    /*
     * There are two vector spaces in play here.
     * The graphics space is in units of pixels, and is defined by the screen size (and sWidth/sHeight) below
     * The game space is always the same size, defined by gameWidth, below
     *
     * To make the two play nice, wrapper methods for all graphics are defined in util.GraphicsWrappers
     *
     * EVERY draw command from a gameplay element should be done through GraphicsWrappers.
     */
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public int sHeight = screenSize.height;
    public int sWidth = screenSize.width;
    public int sMargin = 0;

    public int gameWidth = 100;
    public int gameHeight = 100;

    public GraphicsWrapper graphicsWrapper;

    // nothing has to be scaled to screen size anymore
    // graphics wrapper handles all of that for us
    private int railRadius = 32;
    private int spawnRadius = railRadius / 2;
    private int escapeRadius = railRadius * 3 / 2;

    private int numPlayers = 3;

    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Entity> enemies = new ArrayList<Entity>();
    ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private boolean simulateProjectorAspectRatio = true;

    private HashMap<Player, int[]> playersToKeys = new HashMap<>();
    private HashMap<Integer, Boolean> keysToPressed = new HashMap<>();

    private int frame = 0;

    // These are the default controls for each player
    // I guess right now the order is shoot, swap, left, right
    private int[][] defaultControls = {
            {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R}, // player 1
            {KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F}, // player 2
            {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V}, // etc
    };

    public PewPanel () {
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

            Player p = new Player(rads, playerColors[i], railRadius);

            players.add(p);
            playersToKeys.put(p, defaultControls[i]);
        }

        addKeyListener(this);

        if(!PewPewDriver.FULLSCREEN) {
            setPreferredSize(new Dimension(sWidth, sHeight));
            sMargin = 0;
        }

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

/*
        Projectile test = new BasicBullet(0,0,10,0,Color.green, players.get(0));
        projectiles.add(test);
*/

        BasicEnemy enemyTest = new BasicEnemy(10, 10, escapeRadius);
        enemies.add(enemyTest);

        timer = new Timer(50, this);
        timer.setInitialDelay(1000);
        timer.start();

        setFocusable(true);
        requestFocus();

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        // margins
        g.setColor(Color.gray);
        g.fillRect(0, 0, screenSize.width, screenSize.height);

        // black background
        g.setColor(Color.black);
        g.fillRect(0 + sMargin, 0, sWidth, sHeight);

        // from now on, (0,0) is the middle of the screen
        // and we'll use graphics wrappers
        g.translate(sWidth/2 + sMargin, sHeight/2);
        graphicsWrapper.setGraphics(g);

        // draw the player rail outline first
        g.setColor(Color.darkGray);
        graphicsWrapper.drawCircle(-railRadius, -railRadius, railRadius * 2);

        // draw spawn area (probably just for testing)
        g.setColor(Color.darkGray);
        graphicsWrapper.drawCircle(-spawnRadius, -spawnRadius, spawnRadius * 2);

        // draw spawn area (probably just for testing)
        g.setColor(Color.darkGray);
        graphicsWrapper.drawCircle(-escapeRadius, -escapeRadius, escapeRadius * 2);

        // Draw Player
        for(Player p : players) {
            p.draw(graphicsWrapper);
        }

        // Draw bullets
        for(Projectile p : projectiles) {
            p.draw(graphicsWrapper);
        }

        // Draw enemies
        for(Entity e : enemies) {
            e.draw(graphicsWrapper);
        }

        // side panels
        g.setColor(Color.blue);
        int sidePanelWidth = sWidth / 8;
        int sidePanelHeight = sidePanelWidth * 3;
        int sidePanelYOffset = (sHeight - sidePanelHeight) / 2;

        // translate back for the side panels
        g.translate(-sWidth/2 - sMargin, -sHeight/2);

        g.fillRect(sMargin, sidePanelYOffset, sidePanelWidth, sidePanelHeight);
        g.fillRect(sMargin + sWidth - sidePanelWidth, sidePanelYOffset, sidePanelWidth, sidePanelHeight);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        frame++;
        if(frame % 100 == 0) {
            spawn();
        }

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

        // update enemies
        ArrayList<Entity> entitiesToRemove = new ArrayList<>();
        for(Entity e : enemies) {
            e.update();
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

            if(!proj.onScreen(gameWidth, gameHeight)) {
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

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void spawn() {
        // at a random angle
        double theta = Math.random() * 2 * Math.PI;
        // pick a random point from 10% spawn radius to 100% spawn radius
        double radius = (Math.random() * 0.9 + 0.1) * spawnRadius;

        GameUtils.Position p = GameUtils.radialLocation(radius, theta);
        BasicEnemy newEnemy = new BasicEnemy(p.x, p.y, escapeRadius);

        enemies.add(newEnemy);
    }
}
