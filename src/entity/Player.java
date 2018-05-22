package entity;

import entity.projectile.HeavyBullet;
import entity.projectile.LightBullet;
import entity.projectile.Projectile;
import graphics.GraphicsWrapper;
import util.GameUtils;

import java.awt.*;

public class Player extends EntityPolar {
    private int fireDelay = 10;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private boolean dead = false;

    private double speed = 0.06;

    private Color shieldColor;

    public int shieldRefreshMax = 1000;
    public int shieldRefreshDuration = 200;
    public int shieldRefreshCurrent = 0;
    public boolean shielded = false;

    public boolean canScreenClear = true;

    // idk I guess -1 is no powerup and we'll figure the rest out
    public int powerup = -1;

    private int enemySurvivedDamage;

    public int countTick;

    public Player(double theta, Color color, double radius) {
        this.theta = theta;
        this.radius = radius;
        this.countTick = 8;
        this.enemySurvivedDamage = 2;

        this.size = 2.0;

        this.color = color;

        this.maxHealth = 20;
        this.health = maxHealth;

        this.shieldColor = new Color(180,200,255);
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
        }

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
    }

    // returns a projectile to be added to the projectiles array
    // null if I can't fire
    public Projectile fire() {
        if(fireDelayTimer < 0) {
            GameUtils.BulletVector vec = GameUtils.bulletVector(
                    radius * .95,
                    theta
            );

            Projectile p = new LightBullet(
                    vec.px,
                    vec.py,
                    vec.vx,
                    vec.vy,
                    this
            );

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

        health -= dmg;
        countTick = 0;
    }
}
