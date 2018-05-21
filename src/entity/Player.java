package entity;

import entity.projectile.LightBullet;
import entity.projectile.Projectile;
import engine.GraphicsWrapper;
import util.GameUtils;

import java.awt.*;

public class Player extends EntityPolar {
    private int fireDelay = 5;
    private int fireDelayTimer = 0;

    private int bulletDamage = 4;

    private boolean dead = false;

    private double speed = 0.06;

    private Color shieldColor;

    public Player(double theta, Color color, double radius) {
        this.theta = theta;
        this.radius = radius;

        this.size = 2.0;

        this.color = color;

        this.maxHealth = 5;
        this.health = maxHealth;

        this.shieldColor = new Color(180,200,255);
    }

    @Override
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

        return collides(proj.getX(), proj.getY());
    }

    private boolean collides(double x, double y) {
        double dx = getX() - x;
        double dy = getY() - y;

        // We give a leeway on player size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < size * 0.9;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        double x = getX();
        double y = getY();

        if(dead) {
            gw.setColor(Color.white);
        } else {
            gw.setColor(color);
        }
        gw.fillTriangle(x, y, GameUtils.flipAngle(theta), size * 0.9);

        gw.setColor(shieldColor);
        gw.drawCircle(x - size, y - size, size*2);
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }
}
