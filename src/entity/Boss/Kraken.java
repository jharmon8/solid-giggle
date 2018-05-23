package entity.Boss;

import entity.Player;
import entity.projectile.Projectile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/*
 * This is where the fun begins
 */
public class Kraken extends Boss {
    private int frame;

    private Vector<Integer> attackTimer = new Vector<>();

    public Kraken(int escapeRadius, int spawnRadius) {
        this.x = 0;
        this.y = 0;

        this.size = 1.75;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 3;
        this.health = maxHealth;
        this.escapeRadius = escapeRadius;
        this.spawnRadius = spawnRadius;

        int aimLaserCooldown = 200;
        int laserVomit = 80;
        int interlockArc = 100;
        int bulletPie = 200;
        int spawnEnemy = 80;
        int fireNormalShots = 10;


        attackTimer.add(aimLaserCooldown);
        attackTimer.add(laserVomit);
        attackTimer.add(interlockArc);
        attackTimer.add(bulletPie);
        attackTimer.add(spawnEnemy);
        attackTimer.add(fireNormalShots);
    }

    @Override
    public void update() {
        frame++;
        direction = computeDirection(x,y);
        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;
        x += vx;
        y += vy;

        for (int attackType : attackTimer){
            attackType--;
        }

        attemptShoot(selectShoot(canShoot(attackTimer)));

    }

    @Override
    public Player targetPlayer(ArrayList<Player> players) {
        int minHP = -1;
        Player target;
        for (Player p : players) {
            if (p.getHealth() < minHP || minHP < 0) {
                minHP = p.getHealth();
                target = p;
            }
        }
        return target;
    }

    @Override
    public ArrayList<Projectile> attemptShoot (int wepToFire){
        return null;
    }
}
