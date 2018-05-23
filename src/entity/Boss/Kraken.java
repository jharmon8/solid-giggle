
package entity.Boss;

import entity.Player;
import entity.projectile.Projectile;
import entity.projectile.SlowBullet;
import engine.util.GraphicsWrapper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class Kraken extends Boss {
    private int frame;
    private int damageTick;

    private double escapeRadius;
    private double spawnRadius;
    private double speed;
    private double direction;
    private double vx;
    private double vy;

    private Color highlite;

    private int aimLaserCooldown;
    private int laserVomitCooldown;
    private int interlockArcCooldown;
    private int bulletPieCooldown;
    private int spawnEnemyCooldown;
    private int fireNormalShotCooldown;

    private int aimLaserLength;
    private int laserVomitLength;
    private int interlockArcLength;
    private int bulletPieLength;
    private int spawnEnemyLength;
    private int fireNormalShotLength;

    private double shootChance;

    private Vector<Integer> attackTimer = new Vector<>();
    private Vector<Integer> attackLength = new Vector<>();

    public Kraken(int escapeRadius, int spawnRadius) {
        this.x = 0;
        this.y = 0;

        shootChance = 0.33;

        this.size = 3.5;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 3;
        this.health = maxHealth;
        this.escapeRadius = escapeRadius;
        this.spawnRadius = spawnRadius;

        aimLaserCooldown = 200;
        laserVomitCooldown = 80;
        interlockArcCooldown = 100;
        bulletPieCooldown = 200;
        spawnEnemyCooldown = 150;
        fireNormalShotCooldown = 10;

        aimLaserLength = 200;
        laserVomitLength = 100;
        interlockArcLength = 80;
        bulletPieLength = 400;
        spawnEnemyLength = 100;
        fireNormalShotLength = 1;

        damageTick = 8;

        attackTimer.add(aimLaserCooldown);
        attackTimer.add(laserVomitCooldown);
        attackTimer.add(interlockArcCooldown);
        attackTimer.add(bulletPieCooldown);
        attackTimer.add(spawnEnemyCooldown);
        attackTimer.add(fireNormalShotCooldown);

        attackLength.add(aimLaserLength);
        attackLength.add(laserVomitLength);
        attackLength.add(interlockArcLength);
        attackLength.add(bulletPieLength);
        attackLength.add(spawnEnemyLength);
        attackLength.add(fireNormalShotLength);

        direction = computeTrajectory(x,y);
        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;
    }

    @Override
    public void update() {
        frame++;
        damageTick++;

        boolean doMove = true;
        for (int i = 0; i < attackLength.size(); i++) {
            if (attackLength.get(i) > 0) {
                attackLength.set(i, attackLength.get(i) - 1);
                doMove = false;
            } else {
                attackTimer.set(i, attackTimer.get(i) - 1);
            }
        }

        if (doMove) {
            if (x == 0 && y == 0) {
                direction = computeTrajectory(x, y);
                vx = Math.cos(direction) * speed;
                vy = Math.sin(direction) * speed;
            } else if ((int) getR() == spawnRadius) {
                direction = direction - Math.PI;
                vx = -vx;
                vy = -vy;
            }
            x += vx;
            y += vy;
        }
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
    public int selectShoot (Vector<Integer> availWep){
        if (Math.random() < shootChance && availWep != null) {
            int wepToFire = (int) (Math.random() * availWep.size());
            return availWep.get(wepToFire);
        }
        return -1;
    }

    /*
    public ArrayList<Projectile> laserTrack (ArrayList<Player> players){
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        for(Player play : players) {
            Projectile p = new TrackingLaser(x, y, play.getX(), play.getY(), this);
            projToAdd.add(p);
        }
        return projToAdd;
    }
    */

    public ArrayList<Projectile> simpleShot (ArrayList<Player> players){
        Player weakPlayer = targetWeakPlayer(players);
        Projectile p = new SlowBullet(x, y, weakPlayer.getX() - x, weakPlayer.getY() - y,this);
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        projToAdd.add(p);
        attackTimer.set(attackTimer.size()-1, fireNormalShotCooldown);
        return projToAdd;
    }

    public ArrayList<Projectile> attemptShootKraken (ArrayList<Player> players, int wepToFire){
        ArrayList<Projectile> projAdded = new ArrayList<>();

        if (wepToFire == 0) {
            //projAdded = laserTrack();
        } else if (wepToFire == 1){
            //projAdded = laserVomit();
        } else if (wepToFire == 2){
            //projAdded = interArc();
        } else if (wepToFire == 3){
            //projAdded = waveArc();
        } else if (wepToFire == 4) {
            projAdded = simpleShot(players);
        } else {
            projAdded = null;
        }

        if (wepToFire >= 0 && wepToFire != 4) {
            for (int i = 0; i < (attackTimer.size() - 1); i ++) {
                attackTimer.set(i, attackTimer.get(i) + 20);
            }
        }
        return projAdded;
    }

    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        damageTick = 0;
    }
}
