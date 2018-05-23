package entity;

import engine.util.AudioManager;
import entity.powerup.Powerup;
import entity.powerup.laserPowerup;
import entity.projectile.LightBullet;
import entity.projectile.MediumLaser;
import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;
import engine.util.GameUtils;

import java.awt.*;
import java.util.ArrayList;

public class Player extends EntityPolar {
    public int playerNum;

    private int fireDelay = 10;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private boolean dead = false;

    private double speed = 0.06;

    private Color shieldColor;

    private int ammoType;
    private int powerupTicks;

    public int shieldRefreshMax = 600;
    public int shieldRefreshDuration = 400;
    public int shieldRefreshCurrent = 0;
    public boolean shielded = false;

    public boolean canScreenClear = true;

    // idk I guess -1 is no powerup and we'll figure the rest out
    public int powerup = -1;

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
        this.ammoType = 0;

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

        if (this.powerupTicks > 0){
            this.powerupTicks--;
        } else {
            this.ammoType = 0;
        }
    }

    // returns a projectile to be added to the projectiles array
    // null if I can't fire
    public Projectile fire() {
        if(fireDelayTimer < 0) {
            GameUtils.BulletVector vec = GameUtils.bulletVector(
                    radius * .95,
                    theta
            );

            Projectile p = bulletSelector(vec.px, vec.py, vec.vx, vec.vy, this);
            /*
            Projectile p = new LightBullet(
                    vec.px,
                    vec.py,
                    vec.vx,
                    vec.vy,
                    this
            );
            */

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

    public void getPowerup(Powerup p){
        if (p.getClass() == laserPowerup.class){
            this.ammoType = 1;
            this.powerupTicks = 50;
        }
    }

    public Projectile bulletSelector (double px, double py, double vx, double vy, Player p){
        //ArrayList<Projectile> bulletFired = new ArrayList<>();
        if (p.ammoType == 0){
            Projectile proj = new LightBullet(
                    px,
                    py,
                    vx,
                    vy,
                    this
            );
            return proj;
            //bulletFired.add(p);
        } else if (p.ammoType == 1) {
            Projectile proj = new MediumLaser(
                    px,
                    py,
                    vx,
                    vy,
                    this
            );
            return proj;
            //bulletFired.add(p);
        }
        return null;
    }
}
