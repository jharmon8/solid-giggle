package entity;

import entity.projectile.LightBullet;
import entity.projectile.Projectile;
import util.GameUtils;

import java.awt.*;

public class Player extends EntityPolar {
    private int fireDelay = 10;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private boolean dead = false;

    private double angularVelocity = 0.075;

    public Player(double theta, double size, Color color, double radius) {
        this.theta = theta;
        this.radius = radius;

        this.size = size;

        this.color = color;

        this.maxHealth = 5;
        this.health = maxHealth;
    }

    public void update() {
        if(health <= 0) {
            dead = true;
        }

        fireDelayTimer--;
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

    public void swap() {

    }

    public void move(boolean clockwise) {
        if(clockwise) {
            this.theta += angularVelocity;
        } else {
            this.theta -= angularVelocity;
        }
    }

    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY());
    }

    public boolean collides(double x, double y) {
        double dx = getX() - x;
        double dy = getY() - y;

        // We give a leeway on player size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < size * 0.9;
    }

    public void draw(Graphics g) {
        if(dead) {
            g.setColor(Color.white);
        } else {
            g.setColor(color);
        }
        g.fillOval((int)(getX() - size), (int)(getY() - size), (int)(size*2), (int)(size*2));
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }
}
