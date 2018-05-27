package entity.enemy;

import engine.util.GameUtils;
import entity.Player;
import entity.powerup.Powerup;
import entity.powerup.LaserPowerup;
import entity.projectile.Projectile;
import engine.util.GraphicsWrapper;
import entity.projectile.SlowBullet;

import java.awt.Color;
import java.util.ArrayList;

public class ArcShootEnemy extends Enemy {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;

    private double speed;

    private int escapeRadius;
    private int rotationSide;
    private int countTick;
    private double thetaCenter;
    private double arcFactor;

    private int shotInterval;
    private int shootTick;

    public ArcShootEnemy(double x, double y, int escapeRadius, int rotationSide) {
        this.x = x;
        this.y = y;

        this.size = 1.75;
        this.speed = 0.5;
        this.collisionDamage = 3;

        this.countTick = 0;
        this.damageTick = 8;
        this.shootTick = 0;

        this.highlite = new Color(0, 200, 0);
        this.color = highlite.darker();

        this.maxHealth = 3;
        this.health = maxHealth;
        this.rotationSide = rotationSide;

        this.shotInterval = 50;

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
        shootTick++;

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
    public void takeDamage(int dmg) {
        health -= dmg;
        damageTick = 0;
    }

    @Override
    public ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        if (shootTick % shotInterval == 0 && shootTick > 0) {
            double minDist = -1;
            Player closestPlayer = null;
            for (Player p : players) {
                double dist = GameUtils.distance(x, y, p.getX(), p.getY());
                if (dist < minDist || minDist < 0) {
                    closestPlayer = p;
                    minDist = dist;
                }
            }
            if (closestPlayer == null){
                return null;
            }

            Projectile p = new SlowBullet(x, y, closestPlayer.getX() - x, closestPlayer.getY() - y,this);
            //Projectile p = new TrackingLaser(x, y, closestPlayer.getX(), closestPlayer.getY(),this);
            ArrayList<Projectile> projToAdd = new ArrayList<>();
            projToAdd.add(p);
            return projToAdd;
        }

        return null;
    }
}
