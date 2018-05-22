package graphics;

import entity.Entity;
import entity.Player;
import entity.enemy.BasicEnemy;
import entity.projectile.Projectile;
import util.GameUtils;

import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Trying to figure out how to best package this so that menus and stuff will be easy
 */
public class GameSubpanel implements Subpanel {

    private int numPlayers = 1;

    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<BasicEnemy> enemies = new ArrayList<BasicEnemy>();
    ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private HashMap<Player, int[]> playersToKeys = new HashMap<>();
    private HashMap<Integer, Boolean> keysToPressed = new HashMap<>();

    private int frame = 0;

    // nothing has to be scaled to screen size anymore
    // graphics wrapper handles all of that for us
    private int railRadius = 32;
    private int spawnRadius = railRadius / 2;
    private int escapeRadius = railRadius * 3 / 2;

    public GraphicsWrapper graphicsWrapper;

    public int gameWidth = 100;
    public int gameHeight = 75;

    public int scoreboard = 0;

    private PewPanel parent;

    public GameSubpanel(int sWidth, int sHeight, PewPanel parent) {
        this.parent = parent;

        for(int i = 0; i < numPlayers; i++) {
            double rads = 6.28 / numPlayers * i;

            Player p = new Player(rads, GameUtils.playerColors[i], railRadius);

            players.add(p);
            playersToKeys.put(p, GameUtils.defaultControls[i]);
        }

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        BasicEnemy enemyTest = new BasicEnemy(10, 10, escapeRadius);
        enemies.add(enemyTest);
    }

    @Override
    public void paintComponent(Graphics g) {
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
        double sidePanelWidth = gameWidth / 8.0;
        double sidePanelHeight = gameHeight * 2 /3;
//        double sidePanelYOffset = gameHeight / 3.0;

        graphicsWrapper.setColor(Color.blue);
        graphicsWrapper.fillRect(-gameWidth / 2.0, -sidePanelHeight / 2.0, sidePanelWidth, sidePanelHeight);
        graphicsWrapper.fillRect(gameWidth / 2.0 - sidePanelWidth, -sidePanelHeight / 2.0, sidePanelWidth, sidePanelHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // check enemy collision with players
        for (BasicEnemy enemy : enemies) {
            for (Player p : players) {
                if (enemy.collides(p)) {
                    enemy.onCollide(p);
                }
            }
        }

        frame++;
        if(frame % 50 == 0) {
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
        for(Entity enemy : enemies) {
            enemy.update();
        }

        // update projectiles
        ArrayList<Projectile> projToRemove = new ArrayList<>();
        ArrayList<BasicEnemy> enemyToRemove = new ArrayList<>();
        for(Projectile proj : projectiles) {
            for(Player player : players) {
                if (player.collides(proj)) {
                    proj.onCollide(player);
                }
            }

            for(BasicEnemy enemy : enemies) {
                if (enemy.collides(proj)) {
                    proj.onCollide(enemy);
                }
            }

            proj.update();

            if(!proj.onScreen(gameWidth, gameHeight)) {
                projToRemove.add(proj);
            }

            if(proj.dead) {
                projToRemove.add(proj);
            }
        }
        //check for dead enemies
        for (BasicEnemy enemy : enemies) {
            if (enemy.dead) {
                enemyToRemove.add(enemy);
            }

            if (!enemy.inPlayfield(escapeRadius)) {
                enemyToRemove.add(enemy);
                for (Player player: players) {
                    player.enemyPassed();
                }
            }
        }

        // remove any projectiles that have left the screen
        for(Projectile deleteMe : projToRemove) {
            projectiles.remove(deleteMe);
        }

        for(BasicEnemy deleteMe : enemyToRemove) {
            enemies.remove(deleteMe);
            scoreboard = scoreboard + 100;
        }

        parent.repaint();
    }

    private void spawn() {
        // at a random angle
        double theta = Math.random() * 2 * Math.PI;
        // pick a random point from 10% spawn radius to 100% spawn radius
        double radius = (Math.random() * 0.9 + 0.1) * spawnRadius;

        GameUtils.Position p = GameUtils.radialLocation(radius, theta);
        BasicEnemy newEnemy = new BasicEnemy(p.x, p.y, escapeRadius);

        enemies.add(newEnemy);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysToPressed.put(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void close() {}
}
