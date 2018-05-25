package entity;

import engine.util.AudioManager;
import entity.powerup.Powerup;
import entity.powerup.LaserPowerup;
import entity.projectile.*;
import engine.util.GraphicsWrapper;
import engine.util.GameUtils;
import javafx.scene.effect.Light;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static engine.util.GameUtils.distance;
import static engine.util.GameUtils.flipAngle;
import static engine.util.GameUtils.radialLocation;

public class Player extends EntityPolar {
    public int playerNum;

    private int fireDelay = 15;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private boolean dead = false;

    private double speed = 0.04;

    private Color shieldColor;

    public int shieldRefreshMax = 150;
    public int shieldRefreshDuration = 80;
    public int shieldRefreshCurrent = 0;
    public boolean shielded = false;

    public boolean canScreenClear = true;

    // idk I guess -1 is no powerup and we'll figure the rest out
    // nah, we'll make it an actual powerup, and null is no powerup
    private Powerup powerup = null;

    // this is the bullet used if there's no powerup
    private Class defaultAmmoType = LightBullet.class;

    private int enemySurvivedDamage;

    // after taking damage, the player will be given 50 iFrames (i.e. 50 ticks of invincibility)
    private int iFramesLeft = 0;
    private int iFramesAfterDamage = 20;

    public int countTick;

    // the 'action' stuff is my hold logic
    private boolean previousAction = false;
    private int actionCount = 0;
    private int holdThreshold = 35; // tuning things like this is hard since framerate might be slow on my computer

    private boolean previousFire = false;

    private boolean reloading = false;
    private final int maxAmmo = 15;
    public int ammo = maxAmmo;

    private int reloadFramesLeft = 0;
    private int maxReloadFrames = 250;
    private int reloadFramesFromButton = 25;

    private int powerupPolygonFaces = 6;
    private double powerupPolygonTheta = 0;
    private double powerupPolygonChange = 0.05;

    public Player(double theta, Color color, double radius, int playerNum) {
        this.playerNum = playerNum;

        this.theta = theta;
        this.radius = radius;
        this.countTick = 8;
        this.enemySurvivedDamage = 2;

        this.size = 2.0;

        this.color = color;

        this.maxHealth = 10;
        this.health = maxHealth;

        this.shieldColor = new Color(180,200,255);
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
            AudioManager.playSound("res/death.wav", -8f);
        }

        iFramesLeft--;
        countTick++;
        fireDelayTimer--;
        powerupPolygonTheta -= powerupPolygonChange;

        if(shieldRefreshCurrent < shieldRefreshMax && !shielded) {
            shieldRefreshCurrent++;
        } else if(shielded) {
            shieldRefreshCurrent -= shieldRefreshMax / shieldRefreshDuration;
        }

        if(shieldRefreshCurrent <= 0) {
            shielded = false;
        }

        // update our powerup
        if(powerup != null) {
            powerup.playerUpdate();
            health += powerup.getHeal();

            // all powerup logic has to go above this
            if (powerup.isFinished()) {
                AudioManager.playSound("res/deactivate.wav", -12f);
                powerup = null;
            }
        }

        // reloading
        if(reloading) {
            reloadFramesLeft--;

            if(reloadFramesLeft <= 0) {
                reloading = false;
                ammo = maxAmmo;
            }
        } else {
            if(ammo <= 0) {
                reloading = true;
                reloadFramesLeft = maxReloadFrames;
            }
        }

        // make sure our health is within bounds
        if(health > maxHealth) {
            health = maxHealth;
        }
    }

    // returns a projectile to be added to the projectiles array
    // null if I can't fire
    public ArrayList<Projectile> firePressed() {
        if(reloading) {
            // if reloading and upward edge, speed the reload
            if (!previousFire) {
                int fireSound = (int)(Math.random() * 4);
                AudioManager.playSound("res/dud" + fireSound + ".wav", -20f);
                reloadFramesLeft -= reloadFramesFromButton;
            }
        } else {
            // if not reloading, do shoot logic
            if(fireDelayTimer < 0) {
                if(ammo > 0) {
                    int fireSound = (int)(Math.random() * 6);
                    AudioManager.playSound("res/shoot0" + fireSound + ".wav", -15f);
                    ArrayList<Projectile> p = createBullet();

                    fireDelayTimer = fireDelay;
                    ammo--;

                    previousFire = true;
                    return p;
                }
            }
        }

        // this would be well done with a finally statement...
        previousFire = true;
        return null;
    }

    // necessary for the stupid reload system
    public void fireReleased() {
        previousFire = false;
    }

    public void actionIsPressed() {
        if(!previousAction) {
            // this means it's just been pressed
            previousAction = true;
            actionCount = 0;
        } else {
            // this means it's being held
            actionCount++;

            if(actionCount > holdThreshold) {
                powerup();
            }
        }

        previousAction = true;
    }

    public void actionIsReleased() {
        if(previousAction) {
            // this means it's just being released
            if(actionCount > holdThreshold) {
//                powerup();
            } else {
                shield();
            }

            actionCount = 0;
        } else {
            // this means it was already released
            // do nothing
        }

        previousAction = false;
    }

    private void shield() {
        if(shieldRefreshCurrent >= shieldRefreshMax) {
            shielded = true;
        }
    }

    private void powerup() {
        if(powerup != null) {
            if(!powerup.isActive()) {
                AudioManager.playSound("res/activate.wav", -12f);
                powerup.activate();
            }
        }
    }

    public void move(boolean clockwise) {
        if(clockwise) {
            this.theta += speed;
        } else {
            this.theta -= speed;
        }
    }

    // for status panel
    public double getReloadPercentage() {
        if(!reloading) {
            return 0;
        }

        return (double) (maxReloadFrames - reloadFramesLeft) / maxReloadFrames;
    }

    public boolean isReloading() {
        return reloading;
    }

    @Override
    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY(), proj.getSize());
    }

    private boolean collides(double x, double y, double targetSize) {
        double dx = getX() - x;
        double dy = getY() - y;

        // We give a leeway on player size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < (size + targetSize) * 0.9;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        double x = getX();
        double y = getY();

        if (Math.ceil(countTick / 3) == 0 || Math.ceil(countTick / 3) == 2) {
            if (dead) {
                gw.setColor(Color.white.darker().darker());
            } else {
                gw.setColor(color.darker().darker());
            }
            gw.fillTriangle(x, y, GameUtils.flipAngle(theta), size * 0.9);

            gw.setColor(shieldColor.darker().darker());
            gw.drawCircle(x - size, y - size, size * 2);
        }
        else {
            if (dead) {
                gw.setColor(Color.white);
            } else {
                gw.setColor(color);
            }
            gw.fillTriangle(x, y, GameUtils.flipAngle(theta), size * 0.9);

            gw.setColor(shieldColor);
            gw.drawCircle(x - size, y - size, size * 2);
        }

        if(shielded) {
            gw.setColor(new Color(140,150,240,85));
            gw.fillCircle(x - size * 1.2, y - size * 1.2, size * 2.4);
        }

        if(powerup != null) {
            if(powerup.isActive()) {
                gw.setColor(powerup.getStatusColor());
//                gw.drawCircle(x - size * 1.3, y - size * 1.3, size * 2.6);
                gw.drawPolygon(x, y, size * 1.2, powerupPolygonFaces, powerupPolygonTheta);
            }
        }
    }

    public void enemyPassed() {
        takeDamage(enemySurvivedDamage);
    }

    public void takeDamage(int dmg) {
        if(shielded) {
            return;
        }

        if(iFramesLeft > 0) {
            return;
        }

        iFramesLeft = iFramesAfterDamage;
        //health -= dmg;
        countTick = 0;

        AudioManager.playSound("res/hit_0" + (int)(Math.random()*3 + 1) + ".wav", -20f);
    }

    public boolean isDead() {
        return dead;
    }

    public void givePowerup(Powerup p){
        if(powerup == null) {
            powerup = p;
            return;
        }

        if(!powerup.isActive()) {
            powerup = p;
        }
    }

    private Class getAmmoType() {
        // if we have a powerup and it changes our ammo type
        if(powerup != null && powerup.getAmmoType() != null) {
            return powerup.getAmmoType();
        }

        return defaultAmmoType;
    }

    private ArrayList<Projectile> createBullet (){
        Projectile output = null;
        ArrayList<Projectile> outputArray = new ArrayList<>();
        Class ammoType = getAmmoType();

        GameUtils.BulletVector vec = GameUtils.bulletVector(
                radius * .95,
                theta
        );

        // fancy bullet reflection constructor
        // I know it's gross, but if it works, it should make our lives easy
        // in particular, this will break if we change the way projectile constructors work.
        try {
            if (ammoType == Splitshot.class){
                double theta = Math.atan(vec.vy/vec.vx);
                double thetaTop = theta + Math.PI/36;
                double thetaBot = theta - Math.PI/36;

                double botVX = radialLocation(distance(0.0, 0.0, vec.vx, vec.vy), thetaBot).x ;
                double botVY = radialLocation(distance(0.0, 0.0, vec.vx, vec.vy), thetaBot).y ;

                //double botVX = -1 * distance(distance(0.0, 0.0, vec.vx, vec.vy), (theta)) * Math.cos(thetaBot) ;
                //double botVY = -1 * distance(distance(0.0, 0.0, vec.vx, vec.vy), (theta)) * Math.sin(thetaBot) ;

                //double topVX = distance(0.0, 0.0, vec.vx, vec.vy) * Math.cos(distance(1.0,thetaTop)) - vec.vx;
                //double topVY = distance(0.0, 0.0, vec.vx, vec.vy) * Math.sin(distance(1.0,thetaTop)) - vec.vy;

                double topVX = radialLocation(distance(0.0, 0.0, vec.vx, vec.vy), thetaTop).x ;
                double topVY = radialLocation(distance(0.0, 0.0, vec.vx, vec.vy), thetaTop).y ;

                if (vec.px > 0){
                    topVX = -1 * topVX;
                    topVY = -1 * topVY;
                    botVX = -1 * botVX;
                    botVY = -1 * botVY;
                }
                //double topVX = -1 * distance(distance(0.0, 0.0, vec.vx, vec.vy), (theta)) * Math.sin(thetaTop) ;
                //double topVY = -1 * distance(distance(0.0, 0.0, vec.vx, vec.vy), (theta)) * Math.cos(thetaTop) ;

                outputArray.add(new MediumBullet(vec.px, vec.py, vec.vx, vec.vy, this));
                outputArray.add(new MediumBullet(vec.px, vec.py, botVX, botVY, this));
                outputArray.add(new MediumBullet(vec.px, vec.py, topVX, topVY, this));
            } else {
                Constructor constructor = ammoType.getConstructor(new Class[]{Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Entity.class});
                output = (Projectile) constructor.newInstance(vec.px, vec.py, vec.vx, vec.vy, this);
                outputArray.add(output);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Player cannot create bullet of type " + ammoType);
            e.printStackTrace();
        }

        return outputArray;
    }

    public Powerup getPowerup() {
        return powerup;
    }
}
