package engine.subpanel;

import engine.util.AudioManager;
import engine.util.GraphicsWrapper;
import engine.PewPanel;
import entity.Entity;
import entity.Player;
import entity.enemy.*;
import entity.projectile.Projectile;
import engine.util.GameUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Trying to figure out how to best package this so that menus and stuff will be easy
 */
public class GameSubpanel implements Subpanel {

    private boolean GODMODE = false;

    private int numPlayers = 1;

    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Projectile> projectiles = new ArrayList<>();

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

    private int numEnemy = 5;

    private PewPanel parent;

    private DecimalFormat scoreFormatter = new DecimalFormat("###,###");

    public GameSubpanel(int sWidth, int sHeight, PewPanel parent, int numPlayers) {
        this.parent = parent;

        for(int i = 0; i < numPlayers; i++) {
            double rads = 6.28 / numPlayers * i;

            Player p = new Player(rads, GameUtils.playerColors[i], railRadius, i + 1);

            players.add(p);
            playersToKeys.put(p, GameUtils.getControls()[i]);
        }

        graphicsWrapper = new GraphicsWrapper(sWidth, sHeight, gameWidth, gameHeight);

        BasicEnemy enemyTest = new BasicEnemy(10, 10, escapeRadius);
        enemies.add(enemyTest);

        AudioManager.stopAllSounds();
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
        double sidePanelHeight = gameHeight * .49;
//        double sidePanelYOffset = gameHeight / 3.0;

        graphicsWrapper.setColor(Color.white);
        graphicsWrapper.fillRect(-gameWidth / 2.0, -sidePanelHeight / 2.0, sidePanelWidth, sidePanelHeight);
        graphicsWrapper.fillRect(gameWidth / 2.0 - sidePanelWidth, -sidePanelHeight / 2.0, sidePanelWidth, sidePanelHeight);

        drawStatusPanels(graphicsWrapper);

        // arrange the score nicely
        String score = scoreFormatter.format(scoreboard);
        graphicsWrapper.setColor(new Color(255,255,255,175));
        graphicsWrapper.drawText(score, -gameWidth*(0.015 * score.length()), -gameHeight/2.4, 5, false);
    }

    /*
     * This is the main game loop, which is called on every tick
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // First, check for game over
        if(players.isEmpty()) {
            parent.declareSubpanelFinished(LoseSubpanel.class);
            return;
        }

        // check enemy collision with players
        for (Enemy enemy : enemies) {
            for (Player p : players) {
                if (enemy.collides(p)) {
                    enemy.onCollide(p);
                }
            }
        }

        // shitty test spawn system
        frame++;
        if(frame % 50 == 0) {
            spawn((int)(Math.random()*numEnemy)+1);
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

            int secondaryKey = playersToKeys.get(p)[1];
            boolean shouldSecondary = false;

            if(keysToPressed.containsKey(secondaryKey)) {
                shouldSecondary = keysToPressed.get(secondaryKey);
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

            if (shouldSecondary) {
                p.shield();
            }

            p.update();
        }

        // Removal arrays to prevent concurrent modification
        ArrayList<Projectile> projToRemove = new ArrayList<>();
        ArrayList<Enemy> enemyToRemove = new ArrayList<>();
        ArrayList<Player> playersToRemove = new ArrayList<>();

        // update enemies
        ArrayList<Enemy> entitiesToRemove = new ArrayList<>();
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        for(Enemy enemy : enemies) {
            enemy.update();
            projToAdd = enemy.attemptShoot(players);
            if (projToAdd != null) {
                for (Projectile projAdded : projToAdd) {
                    projectiles.add(projAdded);
                }
            }

            if (enemy.isDead()) {
                enemyToRemove.add(enemy);
                // this should probably be somewhere else
                scoreboard = scoreboard + enemy.getScoreValue();
            }

            // bounds check
            if (!enemy.inPlayfield(escapeRadius)) {
                enemyToRemove.add(enemy);

                // removing enemy escape damage for now
/*
                for (Player player: players) {
                    player.enemyPassed();
                }
*/
            }
        }

        // update projectiles
        projToAdd = new ArrayList<>();
        for(Projectile proj : projectiles) {
            // Projectile/player collision check
            for(Player player : players) {
                if (player.collides(proj)) {
                    proj.onCollide(player);
                }
            }

            // Projectile/enemy collision check
            for(Enemy enemy : enemies) {
                if (enemy.collides(proj)) {
                    proj.onCollide(enemy);
                }
            }

            proj.update();

            // Explosion processing
            ArrayList<Projectile> projTemp = proj.attemptExplode(proj.getX(), proj.getY(), proj.vx, proj.vy);
            if (projTemp != null) {
                projToAdd.addAll(projTemp);
                projToRemove.add(proj);
            }

            // bounds check
            if(!proj.onScreen(gameWidth, gameHeight)) {
                projToRemove.add(proj);
            }

            // remove dead projectiles
            if(proj.dead) {
                projToRemove.add(proj);
            }
        }

        // check for dead players
        if(!GODMODE) {
            for(Player p : players) {
                if(p.isDead()) {
                    playersToRemove.add(p);
                }
            }
        }

        // remove particles
        projectiles.removeAll(projToRemove);
        if (projToAdd != null){
            projectiles.addAll(projToAdd);
        }

        // remove enemies
        for(Enemy deleteMe : enemyToRemove) {
            enemies.remove(deleteMe);
        }

        // remove players
        players.removeAll(playersToRemove);

        parent.repaint();
    }

    private void spawn(int enemyToSpawn) {
        // at a random angle
        double theta = Math.random() * 2 * Math.PI;
        // pick a random point from 10% spawn radius to 100% spawn radius
        double radius = (Math.random() * 0.9 + 0.1) * spawnRadius;

        GameUtils.Position p = GameUtils.radialLocation(radius, theta);
        //BasicEnemy newEnemy = new BasicEnemy(p.x, p.y, escapeRadius);

        Enemy newEnemy;
        if (enemyToSpawn == 1) {
            newEnemy = new BasicEnemy(p.x, p.y, escapeRadius);
        } else if (enemyToSpawn == 2) {
            newEnemy = new ShootEnemy(p.x, p.y, escapeRadius);
        } else if (enemyToSpawn == 3) {
            newEnemy = new LaserEnemy(p.x, p.y, escapeRadius);
        } else if (enemyToSpawn == 4) {
            newEnemy = new BombEnemy(p.x, p.y, escapeRadius);
        } else {
            int rotateSide = (int)Math.round(Math.random());
            newEnemy = new ArcEnemy(p.x, p.y, escapeRadius, rotateSide);
        }

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

    // todo WTF THIS IS HORRIBLE
    public void drawStatusPanels(GraphicsWrapper gw) {
        // yes, apparently this is the best way to do this
        ArrayList<Integer> slotList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
        }};

        for(int i = 1; i < 7; i++) {
            Player current = null;
            for(Player p : players) {
                if(p.playerNum == i) {
                    current = p;
                    break;
                }
            }

            if(i < 4) {
                // left side of screen
                drawAvatar(-gameWidth/2 + gameWidth*0.0075, -gameHeight/2 + gameHeight*(0.16 * ((i - 1)%3 + 1.62)), current, 11, gw);
            } else {
                // right side of screen
                drawAvatar(gameWidth/2 - 11 - gameWidth*0.0075, -gameHeight/2 + gameHeight*(0.16 * ((i - 1)%3 + 1.62)), current, 11, gw);
            }
        }

    }

    public void drawAvatar(double x, double y, Player p, double size, GraphicsWrapper gw) {
        // if panel is disabled
        if(p == null) {
            // gray background square
            gw.setColor(Color.black);
            gw.fillRect(x, y, size, size);
            return;
        }

        // gray background square
        gw.setColor(Color.gray);
        gw.fillRect(x, y, size, size);

        // jump right in to the health bar
        gw.setColor(Color.red);
        gw.fillRect(x + size/18, y + size/18, size * 4 / 18, size * 16/18);

        double healthPortion = Math.max(0, p.getHealth()) / (double) p.getMaxHealth();

        gw.setColor(Color.green);
        gw.fillRect(x + size/18, y + size/18 - (size * 16/18 * healthPortion) + size * 16/18, size * 4 / 18, size * 16/18 * healthPortion);

        // health borders
        gw.setColor(Color.black);
        gw.drawLine(x + size/18, y + size/18, x + size*5/18, y + size/18, 0.3);
        gw.drawLine(x + size/18, y + size/18, x + size/18, y + size*17/18, 0.3);
        gw.drawLine(x + size*5/18, y + size*17/18, x + size*5/18, y + size/18, 0.3);
        gw.drawLine(x + size*5/18, y + size*17/18, x + size/18, y + size*17/18, 0.3);

        // shield bar
        gw.setColor(new Color(191, 219, 221));
        gw.fillRect(x + size/3 + size/18, y + size/18, size * 4 / 18, size * 16/18);

        double shieldPortion = p.shieldRefreshCurrent / (double) p.shieldRefreshMax;

        gw.setColor(Color.blue);
        gw.fillRect(x + size/3 + size/18, y + size/18 - (size * 16/18 * shieldPortion) + size * 16/18, size * 4 / 18, size * 16/18 * shieldPortion);

        // shield borders
        gw.setColor(Color.black);
        gw.drawLine(x + size/3 + size/18, y + size/18, x + size/3 + size*5/18, y + size/18, 0.3);
        gw.drawLine(x + size/3 + size/18, y + size/18, x + size/3 + size/18, y + size*17/18, 0.3);
        gw.drawLine(x + size/3 + size*5/18, y + size*17/18, x + size/3 + size*5/18, y + size/18, 0.3);
        gw.drawLine(x + size/3 + size*5/18, y + size*17/18, x + size/3 + size/18, y + size*17/18, 0.3);

        // ScreenClear Box
        gw.setColor(p.canScreenClear ? Color.green : Color.darkGray);
        gw.fillRect(x + size*2/3 + size/18, y + size/18, size * 4 / 18, size * 4/18);

        // ScreenClear borders
        gw.setColor(Color.black);
        gw.drawLine(x + size*2/3 + size/18, y + size/18, x + size*2/3 + size*5/18, y + size/18, 0.3);
        gw.drawLine(x + size*2/3 + size/18, y + size/18, x + size*2/3 + size/18, y + size*5/18, 0.3);
        gw.drawLine(x + size*2/3 + size*5/18, y + size*5/18, x + size*2/3 + size*5/18, y + size/18, 0.3);
        gw.drawLine(x + size*2/3 + size*5/18, y + size*5/18, x + size*2/3 + size/18, y + size*5/18, 0.3);

        // Powerup Box
        switch(p.powerup) {
            case 1:
                gw.setColor(Color.pink);
                break;
            case 2:
                gw.setColor(Color.cyan);
                break;
            case 3:
                gw.setColor(new Color(142,199,129));
                break;
            default:
                gw.setColor(Color.darkGray);
        }

        gw.fillRect(x + size*2/3 + size/18, y + size*2/3 + size/18, size * 4 / 18, size * 4/18);


        // todo I bet these could be an "outline rectangle" method
        // Powerup borders
        gw.setColor(Color.black);
        gw.drawLine(x + size*2/3 + size/18, y + size*13/18, x + size*2/3 + size*5/18, y + size*13/18, 0.3);
        gw.drawLine(x + size*2/3 + size/18, y + size*13/18, x + size*2/3 + size/18, y + size*17/18, 0.3);
        gw.drawLine(x + size*2/3 + size*5/18, y + size*17/18, x + size*2/3 + size*5/18, y + size*13/18, 0.3);
        gw.drawLine(x + size*2/3 + size*5/18, y + size*17/18, x + size*2/3 + size/18, y + size*17/18, 0.3);

        // The player number
        gw.setColor(Color.black);
        gw.drawText(p.playerNum + "", x + size*2/3 + size * 0.033, y + size*2/3, 4, false);
    }
}