package entity.enemy;

import entity.Player;
import entity.powerup.Powerup;
import entity.powerup.laserPowerup;
import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;

import java.awt.Color;
import java.util.ArrayList;

public class BasicEnemy extends Enemy {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;

    private double speed;

    // just used for graphics
    private double direction;

    private Color highlite;

    private int escapeRadius;

    private int countTick;

    public BasicEnemy(double x, double y, int escapeRadius) {
        this.scoreValue = 100;

        this.x = x;
        this.y = y;

        this.size = 1.75;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.countTick = 8;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 3;
        this.health = maxHealth;

        this.powerupChance = 1;

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

    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        countTick = 0;
    }

    @Override
    public  ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        return null;
    }

    @Override
    public ArrayList<Powerup> dropPowerup() {
        if(Math.random() <= powerupChance){
            ArrayList<Powerup> droppedPowerup= new ArrayList<>();
            double powerupSelect = (int) Math.random()*1;
            if (powerupSelect == 0) {
                Powerup p = new laserPowerup(getX(), getY(), vx, vy);
                droppedPowerup.add(p);
            }

            return droppedPowerup;
        }
        return null;
    }
}
