package entity.enemy;

import entity.EntityCartesian;
import entity.Player;
import entity.projectile.Projectile;
import graphics.GraphicsWrapper;
import util.GameUtils;

import java.awt.Color;

public class BasicEnemy extends EntityCartesian {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;
    public boolean dead = false;

    private double speed;
    private int collisionDamage;

    // just used for graphics
    private double direction;

    private Color highlite;

    private int escapeRadius;

    public int countTick;

    public BasicEnemy(double x, double y, int escapeRadius) {
        this.x = x;
        this.y = y;

        this.size = 1.5;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.countTick = 8;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 5;
        this.health = maxHealth;

        // calculate trajectory
        double thetaOffset = (Math.random() - 0.5) * initialThetaRange;
        double thetaCenter = Math.atan2(-y, -x);
        direction = thetaCenter + thetaOffset;

        if(Double.isNaN(direction)) {
            System.err.println("NaN angle in enemy constructor!");
        }

        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;

        this.escapeRadius = escapeRadius;
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
        }

        countTick++;

        x += vx;
        y += vy;
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        if (Math.ceil(countTick / 3) == 0 || Math.ceil(countTick / 3) == 2) {
            gw.setColor(color.darker().darker());
            gw.fillCircle(getX() - size, getY() - size, size * 2);

            if (!Double.isNaN(direction)) {
                gw.setColor(highlite.darker().darker());
                gw.fillTriangle(x, y, direction, size);
            }
        }
        else {
            gw.setColor(color);
            gw.fillCircle(getX() - size, getY() - size, size * 2);

            if (!Double.isNaN(direction)) {
                gw.setColor(highlite);
                gw.fillTriangle(x, y, direction, size);
            }
        }

    }

    @Override
    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY(), proj.getSize());
    }

    public boolean collides(Player p) {
        return collides(p.getX(), p.getY(), p.getSize());
    }

    private boolean collides(double x, double y, double targetSize) {
        double dx = getX() - x;
        double dy = getY() - y;

        // We give a leeway on player size because we are generous gods
        return Math.sqrt(dx * dx + dy * dy) < (size+targetSize) * 0.9;
    }

    public void onCollide(Player p) {
        dead = true;

        p.takeDamage(collisionDamage);
    }

    public boolean inPlayfield(int escapeRadius) {
        if(getR() > escapeRadius * 1.1) {
            return false;
        }

        return true;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }
}
