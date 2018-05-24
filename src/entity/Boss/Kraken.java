
package entity.Boss;

import entity.Player;
import entity.projectile.Projectile;
import entity.projectile.SlowBullet;
import engine.util.GraphicsWrapper;
import entity.projectile.TrackingLaser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

import static engine.util.GameUtils.distance;
import static engine.util.GameUtils.flipAngle;

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

    private int[] maxCooldown = {0,0,0,0,0,0};
    private int[] attackLength = {0,0,0,0,0,0};
    private int[] currentCooldown = {0,0,0,0,0,0};

    private int numbAttack = 6;
    private int wepToFire;

    private double initialThetaRange = Math.PI * 2;
    private double[] lastPlayerPx;
    private double[] lastPlayerPy;

    private ArrayList<Projectile> projAdded = new ArrayList<>();

    public Kraken(int escapeRadius, int spawnRadius) {
        this.x = 0;
        this.y = 0;

        this.size = 3.5;
        this.speed = 0.25;
        this.collisionDamage = 3;

        this.color = Color.lightGray;
        this.highlite = Color.white;

        this.maxHealth = 3;
        this.health = maxHealth;
        this.escapeRadius = escapeRadius;
        this.spawnRadius = spawnRadius;

        this.wepToFire = -1;

        maxCooldown[0] = 200;
        maxCooldown[1] = 80;
        maxCooldown[2] = 100;
        maxCooldown[3] = 200;
        maxCooldown[4] = 150;
        maxCooldown[5] = 10;

        attackLength[0] = 5;
        attackLength[1] = 100;
        attackLength[2] = 80;
        attackLength[3] = 400;
        attackLength[4] = 100;
        attackLength[5] = 25;

        currentCooldown[0] = -1 * maxCooldown[0];
        currentCooldown[1] = -1 * maxCooldown[1];
        currentCooldown[2] = -1 * maxCooldown[2];
        currentCooldown[3] = -1 * maxCooldown[3];
        currentCooldown[4] = -1 * maxCooldown[4];
        currentCooldown[5] = -1 * maxCooldown[5];

        damageTick = 8;

        direction = computeTrajectory(x,y);
        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;
    }

    @Override
    public void update() {
        frame++;
        damageTick++;
        wepToFire = -1;

        boolean doMove = true;
        for (int i = 0; i < numbAttack; i++) {
            if (currentCooldown[i] > 0) {
                currentCooldown[i] = currentCooldown[i] - 1;
                doMove = false;

                if(i == 0){
                    if (currentCooldown[i] >= (attackLength[i] - 100)){
                        wepToFire = i;
                    }
                }
            } else if (Math.abs(currentCooldown[i]) < maxCooldown[i]){
                currentCooldown[i] = currentCooldown[i] - 1;
            }
        }

        if (doMove) {
            x += vx;
            y += vy;
            if ((int) getR() == spawnRadius) {
                direction = flipAngle(direction);
                vx = -vx;
                vy = -vy;
            } else if (distance(this.x, this.y) < spawnRadius*0.01){
                double thetaOffset = (Math.random() - 0.5) * initialThetaRange;
                double thetaCenter = Math.atan2(-y, -x);
                direction = thetaCenter + thetaOffset;
                vx = Math.cos(direction) * speed;
                vy = Math.sin(direction) * speed;
            }
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
/*
    @Override
    public int selectShoot (int[] availWep){
        int[] working = {0};
        for(int i = 0; i < availWep.length; i ++) {
            if (availWep[i] > 0) {
                working[working.length] = i;
            }
        }
        int toFire = working[1 + (int) (Math.random() * (working.length-1))];
        return toFire;
    }
*/
    public ArrayList<Projectile> laserTrack (ArrayList<Player> players){
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        for(Player play : players) {
            Projectile p = new TrackingLaser(x, y, play.getX(), play.getY(), this);
            projToAdd.add(p);
        }

        return projToAdd;
    }

    public ArrayList<Projectile> simpleShot (ArrayList<Player> players){
        Player weakPlayer = targetWeakPlayer(players);
        Projectile p = new SlowBullet(x, y, weakPlayer.getX() - x, weakPlayer.getY() - y,this);
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        projToAdd.add(p);
        currentCooldown[5] = currentCooldown[5] - 1;
        return projToAdd;
    }

    @Override
    public ArrayList<Projectile> attemptShoot (ArrayList<Player> players){
        projAdded.clear();
        if (wepToFire < 0) {
            int[] canShootArray = canShoot(currentCooldown, maxCooldown);

            if (canShootArray != null) {
                wepToFire = selectShoot(canShootArray);
            }
        }

         if (wepToFire == 0) {
            projAdded = laserTrack(players);
            if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                currentCooldown[wepToFire] = attackLength[wepToFire];
            }
            return projAdded;
        } else if (wepToFire == 1){
            //projAdded = laserVomit();

             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }
             return null;
        } else if (wepToFire == 2){
            //projAdded = interArc();

             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }
             return null;
        } else if (wepToFire == 3){
            //projAdded = waveArc();

             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }
             return null;
        } else if (wepToFire == 4) {
            projAdded = simpleShot(players);
             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }
             return projAdded;
        }
        return null;
    }

    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        damageTick = 0;
    }

    @Override
    public boolean collides(Projectile proj) {
        if(proj.ignoreList.contains(this)) {
            return false;
        }

        return collides(proj.getX(), proj.getY(), proj.getSize());
    }
}
