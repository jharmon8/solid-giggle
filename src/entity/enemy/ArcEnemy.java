package entity.enemy;

import entity.EntityCartesian;
import entity.Player;
import entity.projectile.Projectile;
import graphics.GraphicsWrapper;
import util.GameUtils;

import java.awt.Color;
import java.util.ArrayList;

public class ArcEnemy extends Enemy {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;

    private double speed;

    // just used for graphics
    private double direction;

    private Color highlite;

    private int escapeRadius;
    private int rotationSide;
    private int countTick;
    private int damageTick;
    private double thetaCenter;

    public ArcEnemy(double x, double y, int escapeRadius, int rotationSide) {
        this.x = x;
        this.y = y;

        this.size = 1.75;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.countTick = 0;
        this.damageTick = 8;

        this.highlite = new Color(0, 200, 0);
        this.color = highlite.darker();

        this.maxHealth = 3;
        this.health = maxHealth;
        this.rotationSide = rotationSide;

        // calculate trajectory
        double thetaOffset = (Math.random() - 0.5) * initialThetaRange;
        thetaCenter = Math.atan2(-y, -x);
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
        damageTick++;

        x += vx;
        y += vy;
        updateVel();

    }

    public void updateVel() {
        double arcFactor = (Math.PI/30)*(0.5 + 2.5*Math.random());
        int critTick = 40;
        double accelFactor = 1;

        if (rotationSide == 0) {
            thetaCenter = Math.atan2(-y, -x);
            direction = thetaCenter + Math.PI / 2 + arcFactor;
        } else {
            thetaCenter = Math.atan2(y, x);
            direction = thetaCenter + Math.PI / 2 - arcFactor;
        }

        if (((countTick/critTick) % 2) == 0) {
            //accelFactor += Math.pow(getR() / escapeRadius, 2);
            accelFactor = accelFactor+(double)(countTick % critTick)/(10*critTick);
        } else {
            //accelFactor -= Math.pow(getR() / escapeRadius, 2);
            accelFactor = accelFactor-(double)(countTick % critTick)/(10*critTick);
        }

        double radialScale = 0.33 + Math.pow(getR() / escapeRadius, 2);
        vx = Math.cos(direction) * (speed*accelFactor*radialScale);
        vy = Math.sin(direction) * (speed*accelFactor*radialScale);
    }

    @Override
    public void draw(GraphicsWrapper gw) {
        if (Math.ceil(damageTick / 3) == 0 || Math.ceil(damageTick / 3) == 2) {
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
        damageTick = 0;
    }

    @Override
    public  ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        return null;
    }
}
