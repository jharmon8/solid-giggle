package entity.enemy;

import entity.Player;
import entity.powerup.Powerup;
import entity.powerup.LaserPowerup;
import entity.projectile.Projectile;
import entity.projectile.SlowBullet;
import engine.util.GraphicsWrapper;
import engine.util.GameUtils;
import entity.projectile.TrackingLaser;

import java.awt.Color;
import java.util.ArrayList;

public class ShootEnemy extends Enemy {
    // Instead of doing the chord math, we just have an angular range and it picks a direction in that range
    private double initialThetaRange = Math.PI / 2;

    private double vx, vy;

    private double speed;

    private int shotTick;
    private int shotInterval;

    public ShootEnemy(double x, double y, int escapeRadius) {
        this.scoreValue = 100;

        this.x = x;
        this.y = y;

        this.size = 1.75;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.countTick = 10;

        this.color = Color.blue;
        this.highlite = Color.cyan;

        this.maxHealth = 3;
        this.health = maxHealth;
        
        this.shotInterval = 75;
        this.shotTick = 0;

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
    }

    @Override
    public void update() {
        if(health <= 0) {
            dead = true;
        }

        countTick++;
        shotTick ++;
        damageTick++;

        x += vx;
        y += vy;
    }

    @Override
    public ArrayList<Projectile> attemptShoot(ArrayList<Player> players) {
        if (shotTick % shotInterval == 0 && shotTick > 0) {
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
