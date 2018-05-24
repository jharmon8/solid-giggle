package entity.enemy;

import entity.Player;
import entity.powerup.Powerup;
import entity.powerup.LaserPowerup;
import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;

import java.awt.Color;
import java.util.ArrayList;

public class ArcEnemy extends Enemy {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;

    private double speed;

    private int escapeRadius;
    private int rotationSide;
    private double thetaCenter;
    private double arcFactor;

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

        this.powerupChance = 1;

        arcFactor = (Math.PI/30)*(0.5 + 2.5*Math.random());

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
    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY(), proj.getSize());
    }

    @Override
    public  ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        return null;
    }
}
