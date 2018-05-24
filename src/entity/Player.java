package entity;

import engine.util.AudioManager;
import entity.powerup.Powerup;
import entity.powerup.LaserPowerup;
import entity.projectile.LightBullet;
import entity.projectile.MediumLaser;
import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;
import engine.util.GameUtils;
import javafx.scene.effect.Light;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Player extends EntityPolar {
    public int playerNum;

    private int fireDelay = 10;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private boolean dead = false;

    private double speed = 0.06;

    private Color shieldColor;

    public int shieldRefreshMax = 600;
    public int shieldRefreshDuration = 400;
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

    public Player(double theta, Color color, double radius, int playerNum) {
        this.playerNum = playerNum;

        this.theta = theta;
        this.radius = radius;
        this.countTick = 8;
        this.enemySurvivedDamage = 2;

        this.size = 2.0;

        this.color = color;

        this.maxHealth = 8;
        this.health = maxHealth;

        this.shieldColor = new Color(180,200,255);
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
        }

        iFramesLeft--;

        countTick++;

        fireDelayTimer--;

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
                powerup = null;
            }
        }

        // make sure our health is within bounds
        if(health > maxHealth) {
            health = maxHealth;
        }
    }

    // returns a projectile to be added to the projectiles array
    // null if I can't fire
    public Projectile fire() {
        if(fireDelayTimer < 0) {
            Projectile p = createBullet();

            fireDelayTimer = fireDelay;

            return p;
        }

        return null;
    }

    public void shield() {
        if(shieldRefreshCurrent >= shieldRefreshMax) {
            shielded = true;
        }
    }

    public void move(boolean clockwise) {
        if(clockwise) {
            this.theta += speed;
        } else {
            this.theta -= speed;
        }
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
        health -= dmg;
        countTick = 0;

        AudioManager.playSound("res/hit_0" + (int)(Math.random()*3 + 1) + ".wav", -20f);
    }

    public boolean isDead() {
        return dead;
    }

    public void givePowerup(Powerup p){
        powerup = p;
    }

    private Class getAmmoType() {
        // if we have a powerup and it changes our ammo type
        if(powerup != null && powerup.getAmmoType() != null) {
            return powerup.getAmmoType();
        }

        return defaultAmmoType;
    }

    private Projectile createBullet (){
        Projectile output = null;
        Class ammoType = getAmmoType();

        GameUtils.BulletVector vec = GameUtils.bulletVector(
                radius * .95,
                theta
        );

        // fancy bullet reflection constructor
        // I know it's gross, but if it works, it should make our lives easy
        // in particular, this will break if we change the way projectile constructors work.
        try {
            Constructor constructor = ammoType.getConstructor(new Class[]{Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Entity.class});
            output = (Projectile) constructor.newInstance(vec.px, vec.py, vec.vx, vec.vy, this);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Player cannot create bullet of type " + ammoType);
            e.printStackTrace();
        }

        return output;
    }

    public Powerup getPowerup() {
        return powerup;
    }
}
