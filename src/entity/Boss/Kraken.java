
package entity.Boss;

import entity.Player;
import entity.projectile.*;
import engine.util.GraphicsWrapper;

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

    private double attackChance;

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
        this.spawnRadius = spawnRadius;
        this.escapeRadius = escapeRadius;

        this.wepToFire = -1;

        this.attackChance = 0.1;

        maxCooldown[0] = 400; //trackLaser
        maxCooldown[1] = 500; //laser vomit <- remove
        maxCooldown[2] = 350; //interlock arc
        maxCooldown[3] = 800; // wave arc
        maxCooldown[4] = 700; // shield?
        maxCooldown[5] = 30;  //simple shot

        attackLength[0] = 200;
        attackLength[1] = 100;
        attackLength[2] = 1000;
        attackLength[3] = 800;
        attackLength[4] = 100;
        attackLength[5] = -1;

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

                if(i == 0 || i == 2){
                    wepToFire = i;
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

    public ArrayList<Projectile> laserTrack (ArrayList<Player> players) {
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int deathTime = 50;
        int padding = 50;

        if (currentCooldown[0] >= (attackLength[0] - (attackLength[0] - deathTime - padding))) {
            lastPlayerPx = new double[players.size()];
            lastPlayerPy = new double[players.size()];
            for (int i = 0; i < players.size(); i ++) {
                Projectile p = new TrackingLaser(x, y, players.get(i).getX(), players.get(i).getY(), this);
                lastPlayerPx[i] = players.get(i).getX();
                lastPlayerPy[i] = players.get(i).getY();
                projToAdd.add(p);
            }
        } else if (currentCooldown[0] >= deathTime && currentCooldown[0] < (attackLength[0] - (attackLength[0] - deathTime - padding))) {
            for (int i = 0; i < lastPlayerPx.length; i++) {
                Projectile p = new TrackingLaser(x, y, lastPlayerPx[i], lastPlayerPy[i], this);
                projToAdd.add(p);
            }
        } else if (currentCooldown[0] < deathTime && currentCooldown[0] > 0) {
            for (int i = 0; i < lastPlayerPx.length; i++) {
                Projectile p = new DamageLaser(x, y, lastPlayerPx[i], lastPlayerPy[i], this);
                projToAdd.add(p);
            }
        }
        //System.out.println(projToAdd.size());
        return projToAdd;
    }

    public ArrayList<Projectile> interArc (ArrayList<Player> players) {
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int numberAroundCircumference = 6;
        double randomComponent = (2*Math.PI*Math.random());

        if (currentCooldown[2] % 200 == 0 && currentCooldown[2] > 0){
            for (int i = 0; i < numberAroundCircumference; i++) {
                double theta = i * 2 * Math.PI / numberAroundCircumference + randomComponent;
                double spawnX = x + size * Math.cos(theta);
                double spawnY = y + size * Math.sin(theta);
                Projectile l = new InterlockBullet(spawnX, spawnY, 0, 0, this, i % 2, theta);
                Projectile r = new InterlockBullet(spawnX, spawnY, 0, 0, this, (i + 1) % 2, theta);
                projToAdd.add(l);
                projToAdd.add(r);
            }
        }

        return projToAdd;
    }

    public ArrayList<Projectile> selectArc (){ // select two random enemies to shoot
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        int numberAroundCircumference = 36;
        double randomComponent = (2*Math.PI*Math.random());

        if (currentCooldown[2] % 125 == 0 && currentCooldown[2] > 0){
            double safeTheta = (2*Math.PI*Math.random());
            for (int i = 0; i < numberAroundCircumference; i++) {
                double theta = i * 2 * Math.PI / numberAroundCircumference + randomComponent;
                double spawnX = x + size * Math.cos(theta);
                double spawnY = y + size * Math.sin(theta);
                Projectile l = new InterlockBullet(spawnX, spawnY, 0, 0, this, i % 2, theta);
                Projectile r = new InterlockBullet(spawnX, spawnY, 0, 0, this, (i + 1) % 2, theta);
                projToAdd.add(l);
                projToAdd.add(r);
            }
        }

        return projToAdd;
    }

    public ArrayList<Projectile> simpleShot (ArrayList<Player> players){ // select two random enemies to shoot
        Player weakPlayer = targetWeakPlayer(players);
        Projectile p = new SlowBullet(x, y, weakPlayer.getX() - x, weakPlayer.getY() - y,this);
        ArrayList<Projectile> projToAdd = new ArrayList<>();
        projToAdd.add(p);
        currentCooldown[5] = currentCooldown[5] - 1;
        return projToAdd;
    }

    @Override
    public ArrayList<Projectile> attemptShoot (ArrayList<Player> players){
        ArrayList<Projectile> projAdded = new ArrayList<>();
        ArrayList<Projectile> projAddedTotal = new ArrayList<>();

        projAdded.clear();
        if (wepToFire < 0 && (1-Math.random()) < attackChance ) {
            int[] canShootArray = canShoot(currentCooldown, maxCooldown);

            if (canShootArray != null) {
                wepToFire = selectShoot(canShootArray);
            }
        }
        if (wepToFire == 2){ // overlapping arc
             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }

             projAdded = interArc(players);
             projAddedTotal.addAll(projAdded);
        } else if (wepToFire == 3){ //selective arc
            //projAdded = waveArc();

             if (currentCooldown[wepToFire] == -1* maxCooldown[wepToFire]) {
                 currentCooldown[wepToFire] = attackLength[wepToFire];
             }
            projAdded = selectArc();
            projAddedTotal.addAll(projAdded);
        }

        //weapons that may be fired in overlap
        if (frame % maxCooldown[5] == 0) { //constantfiring
             projAdded = simpleShot(players);
             projAddedTotal.addAll(projAdded);
        }

        if (currentCooldown[0] > 0 || currentCooldown[0] == -1 * maxCooldown[0]) { //lasertracking

            if (currentCooldown[0] == -1* maxCooldown[0]) {
                currentCooldown[0] = attackLength[0];
            }
            projAdded = laserTrack(players);
            projAddedTotal.addAll(projAdded);
        }

        //insert shield that may randomly occur. tie shield to power attack!

        return projAddedTotal;
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
